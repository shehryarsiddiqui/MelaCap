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
import java.util.List;

public class Register extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    /* registerUser - Registers a new user onto the Users Database */
    public void registerUser(View v) {
        // Get all of the text boxes - username, password, name, and email address
        EditText username = (EditText) findViewById(R.id.reg_user);
        EditText password = (EditText) findViewById(R.id.reg_pass);
        EditText name = (EditText) findViewById(R.id.reg_name);
        EditText email = (EditText) findViewById(R.id.reg_email);

        // Convert the values in the text boxes to Strings
        String strUsername = username.getText().toString();
        String strPassword = password.getText().toString();
        String strName = name.getText().toString();
        String strEmail = email.getText().toString();

        // Set up the User object with the strings captured from the text boxes
        User tryUser = new User(strUsername, strPassword, strName, strEmail, 0);

        // Get all users from the database
        UserDBHelper udb = new UserDBHelper(this);
        List<User> allUsers = udb.getAllUsers();

        // Check if the user already exists (username is unique)
        boolean exists = false;

        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(tryUser.getUsername())) {
                // Error message here, prompt user to re-try
                exists = true;
                break;
            }
        }

        // Check if any fields are empty, and produce a dialog box if so
        boolean isBlank = false;
        if (strUsername.trim().length() == 0 ||
                strPassword.trim().length() == 0 ||
                strName.trim().length() == 0 ||
                strEmail.trim().length() == 0) {
            isBlank = true;
            RegConfirmDialog fieldsEmptyPopup = new RegConfirmDialog();
            fieldsEmptyPopup.show(getSupportFragmentManager(), "empty_fields");
        }

        // Perform additional checks if the user exists, and add the user to the database
        if (exists) {
            UserExistsDialog userExists = new UserExistsDialog();
            userExists.show(getSupportFragmentManager(), "user_exists");
        } else if (!exists && !isBlank) {
            udb.addUser(tryUser);
            /* TODO: Add some code to say "Success" when the user is registered. */
            // Once the user is added to the Users database, redirect to the login page.
            try {
                Intent i = new Intent(getBaseContext(), Login.class);
                startActivity(i);
                finish();
            } catch(Exception e) {}
        }

    }
}
