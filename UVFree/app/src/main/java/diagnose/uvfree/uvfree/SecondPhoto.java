package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("deprecation") // Because Camera is deprecated - Camera2 is latest
public class SecondPhoto extends ActionBarActivity {
    // Fields
    public static final int MEDIA_TYPE_IMAGE = 1; // Defines the media type, 1 = Photo/Image
    private Camera mCamera; // The Camera object
    private CamView mPreview; // The CamView preview handler

    /* getCameraInstance - Returns the current open camera instance */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {}

        return c; // Return the open camera
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_photo);

        // Disable "Back" button if retaking
        final Bundle extras = getIntent().getExtras();
        final boolean retake = extras.getBoolean("retake");

        if( retake ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create the Camera instance, create the CamView, and run it in the FrameLayout
        mCamera = getCameraInstance();
        mPreview = new CamView(this, mCamera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camview);
        preview.addView(mPreview); // Display the preview in the FrameLayout
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nooptions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: // When settings is opened
                return true;
            case R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() { super.onDestroy(); }

    /* onPause - override function to handle what happens when the Camera is paused. This can
     * occur when the user navigates away from the Activity or minimizes the app */
    @Override
    protected void onPause() {
        super.onPause();
        mCamera.setPreviewCallback(null);
        mPreview.getHolder().removeCallback(mPreview);
        releaseCamera();
    }

    /* releaseCamera - Release the active Camera and set to null */
    private void releaseCamera() {
        if( mCamera != null ) {
            mCamera.release();
            mCamera = null;
        }
    }

    /* saveImage - function to save an image to Internal Storage */
    public String saveImage(Bitmap b, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("diagimg", Context.MODE_PRIVATE);
        File myPath = new File(directory, name);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("saveImage", directory.getAbsolutePath());

        return myPath.getAbsolutePath();
    }



    // Refocus — this is an attempt to aid in focusing issues
    // Idea is that button will make camera refocus
    public void camRefocus(View v) {

        mCamera.stopPreview();
        Camera.Parameters p = mCamera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.setParameters(p);
        mCamera.startPreview();
        mCamera.autoFocus(null);

    }



    /* takeFirstPhoto - Capture the photo and add it to the database */
    public void takeSecondPhoto(View v) {
        // Pull up the extras
        final Bundle extras = getIntent().getExtras();
        final String uniqueID = extras.getString("uid");
        final String location = extras.getString("location"); // Encodes Location, Side, X, and Y
        final boolean retake = extras.getBoolean("retake");

        Camera.Parameters param;
        // Set Camera Parameters (width/height based on orientation)
        if(mCamera != null ) {
            param = mCamera.getParameters();
        } else {
            mCamera = getCameraInstance();
            try {
                Thread.sleep(2000);
            } catch(Exception ex) {}
            mCamera.open();
            param = mCamera.getParameters();
        }
        int maxResWidth = 320;
        int maxResHeight = 240;
        int tempW;
        int tempH;

        maxResWidth = param.getSupportedPictureSizes().get(1).width;
        maxResHeight = param.getSupportedPictureSizes().get(2).height;

        for (int i = 0; i < param.getSupportedPictureSizes().size(); i++) {
            tempW = param.getSupportedPictureSizes().get(i).width;
            tempH = param.getSupportedPictureSizes().get(i).height;

            if (maxResWidth <= tempW) {
                maxResWidth = tempW;
                maxResHeight = tempH;
            }
        }

        // PictureCallback - Write the photo
        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                PhotoDBHelper pdb = new PhotoDBHelper(getBaseContext());
                AbnormPhoto thisPhoto = new AbnormPhoto();

                String photoURL = uniqueID+location+"p2.jpg";

                // Write photo here
                List<AbnormPhoto> allPhotos = pdb.getPhoto(uniqueID);
                for( int i = 0; i < allPhotos.size(); i++ ) {
                    if( allPhotos.get(i).getBodyLocation().equals(location) ) {
                        thisPhoto = allPhotos.get(i);
                        break;
                    }
                }
                Bitmap capImg = BitmapFactory.decodeByteArray(data, 0, data.length);
                String imagePath = saveImage(capImg, photoURL);

                thisPhoto.setImage2(imagePath);
                thisPhoto.setImage2post("None");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 200);

                pdb.updatePhoto(thisPhoto);

                // Go to the Confirmation page
                try {
                    Intent i = new Intent(getBaseContext(), Confirm.class);
                    // Pass the values needed to pull out the AbnormPhoto again
                    i.putExtra("uid", uniqueID);
                    i.putExtra("location", location);
                    startActivity(i);
                    finish();
                } catch (Exception e) {}
            }
        };

        // Set picture parameters
        param.setPictureSize(maxResWidth, maxResHeight);
        param.setRotation(90);
        // param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //mCamera.setParameters(param);
        //mCamera.takePicture(null, null, mPicture);
        mPreview.thisCamera.setParameters(param);
        mPreview.thisCamera.takePicture(null,null,mPicture);
    }
}
