package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/** ZoomedLocation - The page that allows a user to select the X,Y coordinate of the location of
 *                   the lesion on the zoomed in body part being imaged. */
public class ZoomedLocation extends ActionBarActivity {
    // The X and Y coordinates of where the user has selected on the zoomed image
    public double xCoord;
    public double yCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomed_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // To allow "Back" button usage

        // Pull out the side of the body and location data from the previous activity
        Bundle extras = getIntent().getExtras();
        int side = extras.getInt("side"); // Side of the body (Front or Back)
        int location = extras.getInt("location"); // Location on the body - Region number

        // Get the ImageView that will be used to display the zoomed body part
        final ImageView iv = (ImageView) findViewById(R.id.zoomedview);

        // Set the zoomed area to the correct image based on side and location
        if( side == 1 ) { // Full body
            if( location == 6 ) {
                iv.setImageResource(R.drawable.cid5);}
            else if( location == 7 ) {
                iv.setImageResource(R.drawable.cid6);}
            else if( location == 8 ) {
                iv.setImageResource(R.drawable.cid7);}

        } else if( side == 2 ) { // Face
            if( location == 1 ) {
                iv.setImageResource(R.drawable.cid0);}
            else if( location == 2 ) {
                iv.setImageResource(R.drawable.cid1);}
            else if( location == 3 ) {
                iv.setImageResource(R.drawable.cid2);}
            else if( location == 4 ) {
                iv.setImageResource(R.drawable.cid3);}
            else if( location == 5 ) {
                iv.setImageResource(R.drawable.cid4);}
        }

        // OnTouch code for selecting position on zoomed image
        View.OnTouchListener otl = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                // What will happen here onTouch
                final int action = ev.getAction();

                // X and Y coordinates that were touched
                float evX = ev.getX();
                float evY = ev.getY();

                // Width and Height of the Zoomed body part preview
                final float ivX = (float) iv.getWidth();
                final float ivY = (float) iv.getHeight();

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        // When Pressed
                        xCoord = Math.ceil((evX / ivX) * 100);
                        yCoord = Math.ceil((evY / ivY) * 100);

                        // The pointer / crosshairs showing the location that's selected
                        ImageView pointer = (ImageView) findViewById(R.id.dot);

                        pointer.setX(evX);
                        pointer.setY(evY);
                        pointer.setVisibility(View.VISIBLE); // Make the crosshairs image visible

                        break;

                    case MotionEvent.ACTION_UP:
                        // Place a marker here
                        break;
                }
                return true;
            }
        };

        iv.setOnTouchListener(otl); // Set otl above to be the onTouchListener for the zoomed image preview

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

    /* goToPhotos - Passes the appropriate information along and loads the First Photo page */
    public void goToPhotos(View v) {
        final Bundle extras = getIntent().getExtras();
        String uniqueID = extras.getString("uid"); // The Unique ID
        int bodySide = extras.getInt("side"); // Front or back of the body
        int location = extras.getInt("location"); // The location or region of the body

        // Encode the Location, Body Side, X and Y coordinates into a BodyLocation string
        String bodyLocation = "L:" + Integer.toString(location) + ",S:" + Integer.toString(bodySide)
                + ",X:" + Double.toString(xCoord)
                + ",Y:" + Double.toString(yCoord);

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
