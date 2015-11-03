package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class Confirm extends ActionBarActivity {
    // Fields to store globally - enables retake functionality
    String uniqueID;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Unique ID and location string
        final Bundle extras = getIntent().getExtras();
        uniqueID = extras.getString("uid");
        location = extras.getString("location");

        // Get the current AbnormPhoto instance
        SQLiteHelper db = new SQLiteHelper(this);
        DiagInstance dInst = db.getDiagInstance(uniqueID);
        db.close();

        // Get all AbnormPhoto instances associated with this UID
        PhotoDBHelper pdb = new PhotoDBHelper(this);
        List<AbnormPhoto> allTotal = pdb.getPhoto(uniqueID);

        // Add patient information text to the TextView
        String pInfo = "";
        String lb = System.getProperty("line.separator"); // Line break
        TextView tvConfirm = (TextView) findViewById(R.id.confirm_patient_info);

        /* TODO: Change to use String resource for language compatibility */
        pInfo = "Nurse Name: " + dInst.getNurse() + lb +
                "Patient Name: " + dInst.getPatient() + lb +
                "Date and Time: " + dInst.getDate() + " at " + dInst.getTime();

        tvConfirm.setText(pInfo);

        // This section sets up the image previews
        ImageButton firstPreview = (ImageButton) findViewById(R.id.confirm_prev_first);
        ImageButton secondPreview = (ImageButton) findViewById(R.id.confirm_prev_second);

        // Create bitmap images of the photos
        // First - pick the most recent photos taken under this DiagInstance
        AbnormPhoto recentPhoto = allTotal.get(allTotal.size()-1);

        Bitmap prev1 = loadImage(recentPhoto.getImage1());
        Bitmap prev2 = loadImage(recentPhoto.getImage2());

        // Get the aspect ratio of the images and set the correct button height
        int width = prev1.getWidth();
        int height = prev1.getHeight();
        double aspectRatio = (double) width / height;
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        final int screenWidth = metrics.widthPixels; // Screen width, in pixels

        int widthScaled;
        int heightScaled;
        if( prev1.getHeight() > prev1.getWidth() ) {
            widthScaled = Math.round(((screenWidth / 2) * 9) / 10); // 90% of half the screen width - width to scale bitmap previews
            heightScaled = (int) Math.round(widthScaled / aspectRatio);
        } else {
            heightScaled = Math.round(((screenWidth / 2) * 9) / 10);
            widthScaled = (int) Math.round(heightScaled * aspectRatio);
        }

        float wScale = (float) widthScaled / width;
        float hScale = (float) heightScaled / height;

        // Resize the preview and create thumbnails
        Matrix matrix = new Matrix();
        matrix.postScale(wScale, hScale);

        if( prev1.getWidth() > prev1.getHeight() ) {
            matrix.postRotate(90);
        }

        Bitmap thumbnail1 = Bitmap.createBitmap(prev1, 0, 0, width, height, matrix, false);
        Bitmap thumbnail2 = Bitmap.createBitmap(prev2, 0, 0, width, height, matrix, false);

        boolean badQuality = false;

        /* Do image quality check */
        if( !qualityCheck(prev1, thumbnail1) ) {
            // Red overlay on the image
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(100);
            Canvas cThumb1 = new Canvas(thumbnail1);
            cThumb1.drawRect(0,0,width,height,paint);
            cThumb1.drawBitmap(thumbnail1,0,0,null);

            badQuality = true;
        }

        if( !qualityCheck(prev1, thumbnail2) ) {
            // Red overlay on the image
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(100);
            Canvas cThumb1 = new Canvas(thumbnail2);
            cThumb1.drawRect(0,0,width,height,paint);
            cThumb1.drawBitmap(thumbnail2,0,0,null);

            badQuality = true;
        }
        /* End of quality check code */

        // Set the thumbnails to each image button
        firstPreview.setImageBitmap(thumbnail1);
        secondPreview.setImageBitmap(thumbnail2);

        // Print a prompt if the image quality is bad
       if(badQuality) {
           TextView userPrompt = (TextView) findViewById(R.id.confirm_text);
           userPrompt.setTextColor(Color.RED);
           userPrompt.setText(R.string.confirm_badqual);
       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

    protected void onDestroy() {
        super.onDestroy();
    }

    /* loadImage - Loads an image that was previously saved to Internal storage */
    public Bitmap loadImage(String path) {
        Bitmap b = null;
        try {
            File f = new File(path);
            // b = BitmapFactory.decodeStream(new FileInputStream(f));
            b = decodeSampledBitmapFromFile(f.getAbsolutePath(), 2592, 1944);

            return b;

            /*
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            */

        } catch (Exception e) {
        }
        return b;
    }

    /* takeAnother - Take another photo associated with the same patient */
    public void takeAnother(View v) {
        final Bundle extras = getIntent().getExtras();
        String uniqueID = extras.getString("uid");

        try {
            Intent i = new Intent(getBaseContext(), BodyLocation.class);
            i.putExtra("uid", uniqueID); // Same UID (ie. same patient, etc.)
            startActivity(i);
            finish();
        } catch (Exception e) {
        }
    }

    /* addToQueue - Adds this set of diagnostic instances (inc. all photos) to the queue */
    public void addToQueue(View v) {
        try {
            Intent i = new Intent(getBaseContext(), PhotoQueue.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
        }
    }

    /* retakeFirst - Retake the first preview photo */
    public void retakeFirst(View v) {
        try {
            Intent i = new Intent(getBaseContext(), PreviewToRetake.class);
            i.putExtra("uid", uniqueID);
            i.putExtra("location", location);
            i.putExtra("imagenum",1);
            startActivity(i);
            finish();
        } catch(Exception e) {}
    }

    /* retakeSecond - Retake the second preview photo */
    public void retakeSecond(View v) {
        try {
            Intent i = new Intent(getBaseContext(), PreviewToRetake.class);
            i.putExtra("uid", uniqueID);
            i.putExtra("location", location);
            i.putExtra("imagenum",2);
            startActivity(i);
            finish();
        } catch (Exception e) {}
    }

    // qualityCheck - returns true if quality is ok, and false if quality is below standards
    public boolean qualityCheck(Bitmap orig, Bitmap b) {
        boolean quality = true; // Quality starts as ok


        // First quality check - Resolution
        int lowRes = 3500000; // 3.5 MP is the lowest size that will be acceptable

        if( Math.ceil(orig.getWidth() * orig.getHeight()) < (double)lowRes ) {
            quality = false;
        }

        // Second quality check - brightness (Don't accept all black images)
        float darkThresh = b.getWidth()*b.getHeight()*0.85f;
        int darkPx = 0;

        int[] pixels = new int[b.getWidth()*b.getHeight()];
        b.getPixels(pixels,0,b.getWidth(),0,0,b.getWidth(),b.getHeight());

        for( int i = 1; i < pixels.length; i++ ) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int bl = Color.blue(color);
            double luminance = (0.299*r+0.0f + 0.587*g+0.0f + 0.114*bl+0.0f);
            if( luminance < 100 ) {
                darkPx++;
            }
        }

        if( darkPx > darkThresh ) {
            quality = false;
        }

        return quality;
    }




    // Attempting some Bitmap compression code thing to help with the OOM problem

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }



}
