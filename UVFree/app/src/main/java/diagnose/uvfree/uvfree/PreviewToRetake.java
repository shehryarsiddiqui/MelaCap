package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class PreviewToRetake extends ActionBarActivity {
    String uniqueID;
    String location;
    int imagenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_to_retake);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        TouchImageView tiv = (TouchImageView) findViewById(R.id.taken_prev);

        final Bundle extras = getIntent().getExtras();
        uniqueID = extras.getString("uid");
        location = extras.getString("location");
        imagenum = extras.getInt("imagenum");

        PhotoDBHelper pdb = new PhotoDBHelper(this);
        List<AbnormPhoto> photosThisUID = pdb.getPhoto(uniqueID);
        AbnormPhoto thisPhoto = photosThisUID.get(photosThisUID.size()-1);

        Bitmap thisImage;

        /* TODO: Change these to use image1post and image2post -- maybe? */
        if(imagenum == 1) {
            thisImage = loadImage(thisPhoto.getImage1());
            Matrix matrix = new Matrix();
            if ( thisImage.getWidth() > thisImage.getHeight() ) {
                matrix.postRotate(90);
            }
            thisImage = Bitmap.createBitmap(thisImage,0,0,thisImage.getWidth(), thisImage.getHeight(),matrix,false);

            tiv.setImageBitmap(thisImage);
        } else {
            thisImage = loadImage(thisPhoto.getImage2());
            Matrix matrix = new Matrix();
            if ( thisImage.getWidth() > thisImage.getHeight() ) {
                matrix.postRotate(90);
            }
            thisImage = Bitmap.createBitmap(thisImage,0,0,thisImage.getWidth(), thisImage.getHeight(),matrix,false);
            tiv.setImageBitmap(thisImage);
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
            case R.id.action_settings:
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

    public void retakeImage(View v) {
        boolean retake = true;
        try {
            Intent i;
            if(imagenum == 1) {
                i = new Intent(getBaseContext(), FirstPhoto.class);
            } else {
                i = new Intent(getBaseContext(), SecondPhoto.class);
            }
            i.putExtra("uid", uniqueID);
            i.putExtra("location", location);
            i.putExtra("retake", true);
            startActivity(i);
            finish();
        } catch(Exception e) {}
    }

    public void keepImage(View v) {
        try {
            Intent i = new Intent(getBaseContext(), Confirm.class);
            i.putExtra("uid", uniqueID);
            i.putExtra("location", location);
            startActivity(i);
            finish();
        } catch (Exception e) {

        }

    }

    /* loadImage - Loads an image that was previously saved to Internal storage */
    public Bitmap loadImage(String path) {
        Bitmap b = null;
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
}
