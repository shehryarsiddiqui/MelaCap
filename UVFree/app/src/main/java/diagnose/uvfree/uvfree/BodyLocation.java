package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class BodyLocation extends ActionBarActivity {
    // Field that records the orientation of the body preview.
    // 1 = Front, 2 = Back
    public static int LOC_STATE = 1; // Start with Front (That is what is loaded in onCreate()
    public int location = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.body_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Set the front body diagram, and corresponding heatmaps to the ImageView items
        final ImageView iv = (ImageView) findViewById(R.id.body_loc_img);
        ImageView ivHeat = (ImageView) findViewById(R.id.body_loc_heatmap);

        // Start with the body diagram and heatmap for the Full body
        iv.setImageResource(R.drawable.body_front);
        ivHeat.setImageResource(R.drawable.cid_heatmap_full);

        // Generate text item that will update body part label
        final TextView lesionLabel = (TextView) findViewById(R.id.body_loc_label);
        lesionLabel.setText(R.string.lesion_label);

        // Set button to switch back to body
        final ImageButton bodySwitch = (ImageButton) findViewById(R.id.body_loc_switch);
        bodySwitch.setEnabled(false);
        bodySwitch.setVisibility(View.GONE);

        // Set button to select lesion
        final ImageButton lesionSelect = (ImageButton) findViewById(R.id.lesion_select_button);
        lesionSelect.setEnabled(false);
        lesionSelect.setVisibility(View.INVISIBLE);

        // Create a listener that will detect which part of the image is pressed and load
        // the corresponding zoomed in body location image.
        View.OnTouchListener otl = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                final int action = ev.getAction();

                // The X and Y locations of the area where the user presses
                final int evX = (int) ev.getX();
                final int evY = (int) ev.getY();

                // Initialize the location and color that are being touched, for the code below.
                //int location = 0; // TODO: Locations are encoded based on SNOMED CT specification
                int touchColor = 0;

                switch(action) {
                    case MotionEvent.ACTION_DOWN :
                        // Identify which region here
                        touchColor = getHotspotColor(R.id.body_loc_heatmap, evX, evY);

                        if( touchColor == Color.parseColor("#FF0000")) { location = 1; }
                        else if( touchColor == Color.parseColor("#FFF600")) { location = 2; }
                        else if( touchColor == Color.parseColor("#0018FF")) { location = 3; }
                        else if( touchColor == Color.parseColor("#FF00E4")) { location = 4; }
                        else if( touchColor == Color.parseColor("#00FFF6")) { location = 5; }
                        else if( touchColor == Color.parseColor("#00FF06")) { location = 6; }
                        else if( touchColor == Color.parseColor("#0B6100")) { location = 7; }
                        else if( touchColor == Color.parseColor("#000061")) { location = 8; }
                        else if( touchColor == Color.parseColor("#610056")) { location = 9; }
                        else if( touchColor == Color.parseColor("#4E0000")) { location = 10; }
                        else if( touchColor == Color.parseColor("#FF6D00")) { location = 11; }

                        // Load the Unique ID passed from the previous activity
                        final Bundle extras = getIntent().getExtras();
                        String uniqueID = extras.getString("uid");



                        if( location != 0) { // Do something only if a location was selected

                            // Change image according to selected region
                            if( LOC_STATE == 1 ) { // Full body
                                // This is a test to try and get the body view to switch
                                // when the face is pressed
                                if( location == 10 ) {
                                    flipDrawable(v);
                                    bodySwitch.setEnabled(true);
                                    bodySwitch.setVisibility(View.VISIBLE);}
                                else if( location == 6 ) {
                                    iv.setImageResource(R.drawable.cid5);
                                    lesionLabel.setText(R.string.C44_5);}
                                else if( location == 7 ) {
                                    iv.setImageResource(R.drawable.cid6);
                                    lesionLabel.setText(R.string.C44_6);}
                                else if( location == 8 ) {
                                    iv.setImageResource(R.drawable.cid7);
                                    lesionLabel.setText(R.string.C44_7);}

                            } else if( LOC_STATE == 2 ) { // Face
                                if( location == 1 ) {
                                    iv.setImageResource(R.drawable.cid0);
                                    lesionLabel.setText(R.string.C44_0);}
                                else if( location == 2 ) {
                                    iv.setImageResource(R.drawable.cid1);
                                    lesionLabel.setText(R.string.C44_1);}
                                else if( location == 3 ) {
                                    iv.setImageResource(R.drawable.cid2);
                                    lesionLabel.setText(R.string.C44_2);}
                                else if( location == 4 ) {
                                    iv.setImageResource(R.drawable.cid3);
                                    lesionLabel.setText(R.string.C44_3);}
                                else if( location == 5 ) {
                                    iv.setImageResource(R.drawable.cid4);
                                    lesionLabel.setText(R.string.C44_4);}
                            }

                            // Enable "Select" button, to ensure user has selected a body part
                            if(location != 0 && location != 10) {
                                lesionSelect.setEnabled(true);
                                lesionSelect.setVisibility(View.VISIBLE);
                            }

                        }

                        break;

                    case MotionEvent.ACTION_UP :
                        // Shouldn't ever reach here
                        break;
                }
                return true;
            }
        };

        // Set the above onTouchListener to the FrameLayout containing the ImageViews
        FrameLayout fl = (FrameLayout) findViewById(R.id.loc_container);
        fl.setOnTouchListener(otl);
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

    /* flipDrawable - Flip the current image between Front and Back when the button is pressed. */
    public void flipDrawable(View v) {
        // Flip the current state
        if( LOC_STATE == 1 ) { LOC_STATE = 2; }
        else if( LOC_STATE == 2) { LOC_STATE = 1; }

        // Load up the ImageViews for the body outline and heatmap
        ImageView iv = (ImageView) findViewById(R.id.body_loc_img);
        ImageView ivheat = (ImageView) findViewById(R.id.body_loc_heatmap);

        // Reset the label
        TextView lesionLabel = (TextView) findViewById(R.id.body_loc_label);
        lesionLabel.setText(R.string.lesion_label);

        // Reset "Select" button
        ImageButton lesionSelect = (ImageButton) findViewById(R.id.lesion_select_button);
        lesionSelect.setEnabled(false);
        lesionSelect.setVisibility(View.INVISIBLE);

        // Update the body switch button
        ImageButton bodySwitch = (ImageButton) findViewById(R.id.body_loc_switch);

        // Set the Image and Heatmap based on the LOC_STATE (front (1) or back (2))
        if( LOC_STATE == 1 ) {
            iv.setImageResource(R.drawable.body_front);
            ivheat.setImageResource(R.drawable.cid_heatmap_full);
            bodySwitch.setEnabled(false);
            bodySwitch.setVisibility(View.GONE);
        } else if( LOC_STATE == 2 ) {
            iv.setImageResource(R.drawable.face_front);
            ivheat.setImageResource(R.drawable.cid_heatmap_face);
            bodySwitch.setEnabled(true);
            bodySwitch.setVisibility(View.VISIBLE);
        }
    }


    /* getHotspotColor - Get the color of the pressed area.*/
    public int getHotspotColor(int hotspotId, int x, int y) {
        // Load the Heatmap ImageView
        ImageView ivHeat = (ImageView) findViewById(hotspotId);
        ivHeat.setDrawingCacheEnabled(true);

        Bitmap hotspots = Bitmap.createBitmap(ivHeat.getDrawingCache());
        ivHeat.setDrawingCacheEnabled(false);
        return hotspots.getPixel(x,y);
    }

    /* goToPhotos - Passes the appropriate information along and loads the First Photo page */
    public void goToPhotos(View v) {
        final Bundle extras = getIntent().getExtras();
        String uniqueID = extras.getString("uid"); // The Unique ID
        //int bodySide = extras.getInt("side"); // Front or back of the body
        //int location = extras.getInt("location"); // The location or region of the body

        int bodySide = LOC_STATE;

        // Encode the Location, Body Side, into a BodyLocation string
        String bodyLocation = "L:" + Integer.toString(location) + ",S:" + Integer.toString(bodySide);

        // Load the Lesion Info Activity
        try {
            Intent i = new Intent(getBaseContext(), LesionInfo.class);
            i.putExtra("location", bodyLocation); // Location on the body
            i.putExtra("uid", uniqueID); // Unique ID
            i.putExtra("retake", false); // This isn't a retake - MUST be passed for correct loading
            startActivity(i);
            finish();
        } catch (Exception e) {}
    }

}



