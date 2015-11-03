package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class PreviewDiag extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_diag);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the position on the list that was clicked on
        Bundle extras = getIntent().getExtras();
        int position = Integer.parseInt(extras.getString("position"));

        // Pull the DiagInstance associated with that position from the db
        SQLiteHelper db = new SQLiteHelper(this);
        List<DiagInstance> allDiag = db.getAllDiagInstances();
        DiagInstance dInst = allDiag.get(position);

        // Pull all photos associated with this DiagInstance
        PhotoDBHelper pdb = new PhotoDBHelper(this);
        List<AbnormPhoto> allPhotos = pdb.getPhoto(dInst.getUID());

        // Pull the layout that will contain the previews
        LinearLayout containerLL = (LinearLayout) findViewById(R.id.containerLL);

        // First, present the general DiagInstance information here
        // Patient Name
        TextView patientName = new TextView(this);
        patientName.setText("" + dInst.getPatient());
        patientName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        patientName.setTextColor(Color.BLACK);
        containerLL.addView(patientName);

        // Nurse Name
        TextView nurseName = new TextView(this);
        nurseName.setText("Nurse Name: " + dInst.getNurse());
        nurseName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        nurseName.setTextColor(Color.GRAY);
        containerLL.addView(nurseName);

        // Date
        TextView dateInfo = new TextView(this);
        dateInfo.setText("Date and Time: " + dInst.getDate() + " @ " + dInst.getTime());
        dateInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        dateInfo.setTextColor(Color.GRAY);
        containerLL.addView(dateInfo);

        // Add Ruler to the page
        View ruler = new View(this);
        ruler.setBackgroundColor(Color.GRAY);
        containerLL.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));

        String lb = System.getProperty("line.separator");

        // Now add each AbnormPhoto as a preview, containing body location data and the photos
        for( int i = 0; i < allPhotos.size(); i++ ) {
            // Load up the current AbnormPhoto and its values
            AbnormPhoto currentPhoto = allPhotos.get(i);

            // Set the TextView Parameters
            TextView currentTV = new TextView(this);

            // Set up the output
            String tvOut = "Diagnostic Image Set " + Integer.toString(i+1);

            // Pull the current photo and its relevant information
            currentTV.setText(tvOut);
            currentTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            currentTV.setTextColor(Color.BLACK);
            containerLL.addView(currentTV); // Set the title of the preview

            // Decode Body Location
            TextView bodylocTV = new TextView(this);
            bodylocTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            bodylocTV.setTextColor(Color.GRAY);
            bodylocTV.setText("Location of lesion:");
            containerLL.addView(bodylocTV);

            // The body location parameters
            List<String> decodedLoc = decodeBodyLoc(allPhotos.get(i).getBodyLocation());
            int location = Integer.parseInt(decodedLoc.get(0));
            int side = Integer.parseInt(decodedLoc.get(1));

            ImageView iv = new ImageView(this);

            // Set the ImageView appropriately
            if( side == 1 ) { // Front
                if (location == 1) {
                    iv.setImageResource(R.drawable.front1);
                } else if (location == 2) {
                    iv.setImageResource(R.drawable.front2);
                } else if (location == 3) {
                    iv.setImageResource(R.drawable.front3);
                } else if (location == 4) {
                    iv.setImageResource(R.drawable.front4);
                } else if (location == 5) {
                    iv.setImageResource(R.drawable.front5);
                } else if (location == 6) {
                    iv.setImageResource(R.drawable.front6);
                } else if (location == 7) {
                    iv.setImageResource(R.drawable.front7);
                } else if (location == 8) {
                    iv.setImageResource(R.drawable.front8);
                } else if (location == 9) {
                    iv.setImageResource(R.drawable.front9);
                } else if (location == 10) {
                    iv.setImageResource(R.drawable.front10);
                } else if( location == 11 ) {
                    iv.setImageResource(R.drawable.front11);
                }
            } else if( side == 2 ) { // Back
                if (location == 1) {
                    iv.setImageResource(R.drawable.back1);
                } else if (location == 2) {
                    iv.setImageResource(R.drawable.back2);
                } else if (location == 3) {
                    iv.setImageResource(R.drawable.back3);
                } else if (location == 4) {
                    iv.setImageResource(R.drawable.back4);
                } else if (location == 5) {
                    iv.setImageResource(R.drawable.back5);
                } else if (location == 6) {
                    iv.setImageResource(R.drawable.back6);
                } else if (location == 7) {
                    iv.setImageResource(R.drawable.back7);
                } else if (location == 8) {
                    iv.setImageResource(R.drawable.back8);
                } else if (location == 9) {
                    iv.setImageResource(R.drawable.back9);
                } else if (location == 10) {
                    iv.setImageResource(R.drawable.back10);
                } else if (location == 11) {
                    iv.setImageResource(R.drawable.back11);
                }
            }

            containerLL.addView(iv);

            /* TODO: Add dot on above ImageView using the location of the dot from bodyLoc */


            // Set previews of images here
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);

            ImageView prevIV1 = new ImageView(this);
            ImageView prevIV2 = new ImageView(this);

            File p1 = new File(currentPhoto.getImage1());
            File p2 = new File(currentPhoto.getImage2());


            // TODO: Crashed caused by OOM in decodeFile?

            Bitmap prev1 = decodeSampledBitmapFromFile(p1.getAbsolutePath(), 1024,768);
            Bitmap prev2 = decodeSampledBitmapFromFile(p2.getAbsolutePath(), 1024,768);

//            Bitmap prev1 = BitmapFactory.decodeFile(p1.getAbsolutePath()).copy(Bitmap.Config.RGB_565, true);
//            Bitmap prev2 = BitmapFactory.decodeFile(p2.getAbsolutePath()).copy(Bitmap.Config.RGB_565, true);

//            Bitmap prev1 = BitmapFactory.decodeResource(getResources(), R.drawable.cid0);
//            Bitmap prev2 = BitmapFactory.decodeResource(getResources(), R.drawable.cid0);

            int width = prev1.getWidth();
            int height = prev1.getHeight();
            double aspectRatio = (double) width / height;
            int screenWidth = metrics.widthPixels;

            int widthScaled;
            int heightScaled;

            if( prev1.getHeight() > prev1.getWidth() ) {
                widthScaled = Math.round(((screenWidth / 2) * 9) / 10);
                heightScaled = (int) Math.round(widthScaled / aspectRatio);
            } else {
                heightScaled = Math.round(((screenWidth / 2) * 9) / 10);
                widthScaled = (int) Math.round(heightScaled * aspectRatio);     //Should it be heightScaled / aspectRatio instead of * ?
            }

            float wScale = (float) widthScaled / width;
            float hScale = (float) heightScaled / height;

            Matrix matrix = new Matrix();
            matrix.postScale(wScale, hScale);

            if( prev1.getWidth() > prev1.getHeight() ) {
                matrix.postRotate(90);
            }

            Bitmap thumb1 = Bitmap.createBitmap(prev1, 0, 0, width, height, matrix, false);
            Bitmap thumb2 = Bitmap.createBitmap(prev2, 0, 0, width, height, matrix, false);

            prevIV1.setImageBitmap(thumb1);
            prevIV2.setImageBitmap(thumb2);

            LinearLayout innerll = new LinearLayout(this);
            innerll.setOrientation(LinearLayout.HORIZONTAL);

            // Add two image previews
            TextView imgPrevTitle = new TextView(this);
            imgPrevTitle.setText("Photo Previews:");
            imgPrevTitle.setTextColor(Color.GRAY);
            imgPrevTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            containerLL.addView(imgPrevTitle);

            innerll.addView(prevIV1);
            innerll.addView(prevIV2);

            containerLL.addView(innerll);

            // Add a space
            TextView spacer = new TextView(this);
            spacer.setText(lb);
            containerLL.addView(spacer);

            /* TODO: set all strings to reference strings.xml for translation purposes */

            // Add a Ruler
            ruler = new View(this);
            ruler.setBackgroundColor(Color.GRAY);
            containerLL.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
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

    // TODO: Do we need this code anymore?
    /* decodeBodyLoc - Decodes the body location String into its corresponding parts */
    static public List<String> decodeBodyLoc( String bodyLoc ) {
        // Split the string at ,
        String[] splitFirst = bodyLoc.split(",");

        // Split the resulting Strings at colons (:) and pull the resulting numbers
        String location = splitFirst[0].split(":")[1];
        String side = splitFirst[1].split(":")[1];
 //       String xCoord = splitFirst[2].split(":")[1];
 //       String yCoord = splitFirst[3].split(":")[1];

        // Set up a list of strings as the output, add the strings from above, and return it
        List<String> output = new ArrayList<String>();
        output.add(location);
        output.add(side);
//        output.add(xCoord);
//        output.add(yCoord);

        return output;
    }


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

    public static Bitmap decodeSampledBitmapFromFile(String path,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(path,options);
    }

}
