package diagnose.uvfree.uvfree;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Pull all users, find the active user, and use that user's name to greet them. */
        UserDBHelper udb = new UserDBHelper(this);
        User thisUser = udb.getActiveUser();

        udb.close();
    }

    protected void onDestroy() { super.onDestroy(); }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                try {
                    Intent i = new Intent(getBaseContext(), AboutApp.class);
                    startActivity(i);
                } catch (Exception e) {}
                return true;

            case R.id.action_settings:
                try {
                    Intent i = new Intent(getBaseContext(), Settings.class);
                    startActivity(i);
                } catch (Exception e) {}
                break;

            case  R.id.home:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }





    /* Handle the button presses with the following methods: */

    /* takeNewImage - begins the process to take a new image. by launching the TakeNewImage.class Activity. */
    public void takeNewImage(View v) {
        try {
            Intent i = new Intent(getBaseContext(), TakeNewImage.class);
            startActivity(i);
        } catch (Exception e) {}
    }

    /* photoQueue - loads the PhotoQueue.class Activity */
    public void photoQueue(View v) {
        try {
            Intent i = new Intent(getBaseContext(), PhotoQueue.class);
            startActivity(i);
        } catch( Exception e ) {}
    }

    /* goSettings - loads the Settings.class Activity */
    public void goSettings(View v) {
        try {
            Intent i = new Intent(getBaseContext(), Settings.class);
            startActivity(i);
        } catch (Exception e) {}
    }

    /* aboutThisApp - loads the AboutApp.class Activity */
    public void aboutThisApp(View v) {
        try {
            Intent i = new Intent(getBaseContext(), AboutApp.class);
            startActivity(i);
        } catch (Exception e) {}
    }

    /* logout - logs the current user out and returns to Login.class Activity */
    public void logout(View v) {
        // Pull all of the users in the database and find the current user
        UserDBHelper udb = new UserDBHelper(this);
        User thisUser = udb.getActiveUser();

        // Update the database to set the current user's status as logged out
        thisUser.setLoggedIn(0);
        udb.updateUser(thisUser);

        // Load the Login.class Activity
        try {
            Intent i = new Intent(getBaseContext(), Login.class);
            startActivity(i);
            finish(); // Close this instance of MainActivity.class
        } catch (Exception e) {}

    }
}
