package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.util.List;


public class ConfigureDB extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_db);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Print a list of all of the current users in the database, for debug purposes:
        /* TODO: Remove this before the production version. */
        TextView usersTV = (TextView) findViewById(R.id.configure_db_users);
        String usersSummary = "";
        String lb = System.getProperty("line.separator");

        UserDBHelper udb = new UserDBHelper(this);
        List<User> allUsers = udb.getAllUsers();

        for (int i = 0; i < allUsers.size(); i++) {
            usersSummary = usersSummary.concat((i+1) + ". " + allUsers.get(i).toString() + lb);
        }

        usersTV.setText(usersSummary);
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
}
