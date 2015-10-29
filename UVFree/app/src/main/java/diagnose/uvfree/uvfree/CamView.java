package diagnose.uvfree.uvfree;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.MotionEvent;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation") // Camera is always deprecated, because Camera2 is the latest API
public class CamView extends SurfaceView implements SurfaceHolder.Callback {
    // Define the Camera and display surface
    private SurfaceHolder previewFrame;
    public Camera thisCamera;

    // Constructor
    public CamView(Context context, Camera camera) {
        super(context);
        thisCamera = camera;
        previewFrame = getHolder();
        previewFrame.addCallback(this);
        previewFrame.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // For older Android support

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /* getOptimalSize(List<Size>, int w, int h)
     * Gets the optimal size to maintain aspect ratio for a Camera preview based on the supported
     * sizes of the screen, as reported by the driver. */
    public Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        // Define the aspect ratio
        final double ASPECT_RATIO_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;

        // Null check
        if (sizes == null) {
            return null;
        }

        // For loop to go through the supported sizes and find the highest resolution at the target aspect ratio.
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_RATIO_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        return optimalSize;
    }

    /* surfaceCreated - When the preview surface is created, define the "preview display"
     *  of the camera as the display surface defined above. */
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            thisCamera.stopPreview();
            Camera.Parameters p = thisCamera.getParameters();
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            thisCamera.setParameters(p);
            thisCamera.setPreviewDisplay(holder);
            thisCamera.startPreview();
            thisCamera.autoFocus(null);


            // thisCamera.setPreviewDisplay(holder);
            // thisCamera.startPreview();
        } catch (IOException e) {}
    }

    /* surfaceDestroyed - Release CamView in the activity using Camera.release().. */
    public void surfaceDestroyed(SurfaceHolder holder) {
        thisCamera.release();
        thisCamera = null;
    }

    /* surfaceChanged - Use this to change / rotate the camera image preview. */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Stop Preview -> Change -> Restart preview. IMPORTANT!

        // If there's no preview, get out of the camera class
        if (previewFrame.getSurface() == null) {
            return;
        }

        // Have to stop the preview before running the camera
        thisCamera.stopPreview();

        // Set the display parameters - width, height, and enable autofocus (if used)
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        // Autofocus Callback required for only some parameter values when using setFocusMode.
        //mainCamera.autoFocus(new AutoFocusCallback() {
        //    @Override
        //   public void onAutoFocus(boolean success, Camera camera) {
        // TODO Auto-generated method stub
        //  }
        //});

        //thisCamera.autoFocus(new Camera.AutoFocusCallback() {
         //   @Override
          //  public void onAutoFocus(boolean success, Camera camera) {

        //    }
        //});

        final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {

            }
        };

        // Set the Camera parameters (preview size and display orientation).
        Camera.Parameters param = thisCamera.getParameters();

        param.setPreviewSize(height, width); // Reversed from width,height for portrait mode

        // Get the available focus modes
        List<String> focusModes = param.getSupportedFocusModes();

        if( focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) ) {
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        thisCamera.setParameters(param); // Apply the parameters to the camera.
        thisCamera.setDisplayOrientation(90); // Rotate the display. Note that the app is limited to vertical in the layout file.

        // Update the display
        try {
            thisCamera.setPreviewDisplay(previewFrame);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // Start the preview again
        thisCamera.startPreview();
        thisCamera.autoFocus(null);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                thisCamera.autoFocus(autoFocusCallback);
            }
        }, 200);

        // trying to fix Auto Focus
        thisCamera.cancelAutoFocus();
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        thisCamera.setParameters(param);

    }


}
