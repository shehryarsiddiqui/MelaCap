package diagnose.uvfree.uvfree;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.util.List;

public class Login extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // If a user is already logged in, directly log into the app
        UserDBHelper udb = new UserDBHelper(this);
        List<User> allUsers = udb.getAllUsers();

        for (int j = 0; j < allUsers.size(); j++) {
            if (allUsers.get(j).getLoggedIn() == 1) {
                try {
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {}
            }
        }

    }

    protected void onDestroy() { super.onDestroy(); }

    /* Login - logs a user into the app. */
    public void login(View v) {
        // Find the username and password fields and get their values
        EditText username = (EditText) findViewById(R.id.username);
        String sUsername = username.getText().toString();
        EditText password = (EditText) findViewById(R.id.password);
        String sPassword = password.getText().toString();

        // Get all currently logged in users from the database
        UserDBHelper udb = new UserDBHelper(this);
        List<User> allUsers = udb.getAllUsers();

        /* Some flags to check:
         *  - Does this user exist?
         *  - Does the password match the password recorded for this user?
         *  - Are any fields blank?
         */
        boolean userExists = false;
        boolean passMatch = false;
        boolean isBlank = false;

        // Check if the typed in user exists
        User thisUser = new User();
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(sUsername)) {
                userExists = true;
                thisUser = allUsers.get(i);
                break;
            }
        }

        // Check if either the username or the password is blank
        if (sUsername.trim().length() == 0 || sPassword.trim().length() == 0) {
            isBlank = true;
        }

        // If the user exists (as checked above), check if the password matches
        if (userExists && thisUser.getPassword().equals(sPassword)) {
            passMatch = true;
        }

        /* If the user exists and the passwords match, go to MainActivity.class.
         * Also, update the logged in status of the user.
         * Else, pop up a window with the correct error message.
         */
        if (userExists && passMatch && !isBlank) {
            thisUser.setLoggedIn(1);
            udb.updateUser(thisUser);
            try {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            } catch (Exception e) {
            }
        } else if (isBlank) {
            RegConfirmDialog blankSpaces = new RegConfirmDialog();
            blankSpaces.show(getSupportFragmentManager(), "empty_login_fields");
        } else if (!userExists) {
            UserDoesntExistDialog wrongUser = new UserDoesntExistDialog();
            wrongUser.show(getSupportFragmentManager(), "wrong_user");
        } else if (!passMatch) {
            WrongPasswordDialog wrongPassword = new WrongPasswordDialog();
            wrongPassword.show(getSupportFragmentManager(), "wrong_password");
        }
    }

    /* register - Loads the new user registration screen. */
    public void register(View v) {
        try {
            Intent i = new Intent(getBaseContext(), Register.class);
            startActivity(i);
            finish();
        } catch(Exception e) {}
    }

}
