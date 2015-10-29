package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class LesionInfo extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesion_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    public void takeFirstPhoto(View v) {
        final Bundle extras = getIntent().getExtras();
        String uniqueID = extras.getString("uid"); // The Unique ID
        String bodyLocation = extras.getString("location");

        EditText nurseCommentsET = (EditText) findViewById(R.id.nurse_comments);
        String nurseComments = nurseCommentsET.getText().toString();

        RadioGroup timeLesionRG = (RadioGroup) findViewById(R.id.radio_timelesion);
        int selectedTimeLesion = timeLesionRG.getCheckedRadioButtonId();

        int timelesion;

        if(selectedTimeLesion == R.id.timelesion_low) {
            timelesion = 1;
        } else if(selectedTimeLesion == R.id.timelesion_med) {
            timelesion = 2;
        } else if(selectedTimeLesion == R.id.timelesion_high) {
            timelesion = 3;
        } else {
            timelesion = 4;
        }

        try {
            Intent i = new Intent(getBaseContext(), FirstPhoto.class);
            i.putExtra("timelesion",timelesion);
            i.putExtra("nursecomments",nurseComments);
            i.putExtra("uid",uniqueID);
            i.putExtra("location", bodyLocation); // Location on the body
            i.putExtra("retake", false); // This isn't a retake - MUST be passed for correct loading
            startActivity(i);
            finish();
        } catch(Exception e) { }


    }


}
