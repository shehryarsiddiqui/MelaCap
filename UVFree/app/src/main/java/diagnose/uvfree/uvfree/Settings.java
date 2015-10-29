package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class Settings extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle the Checkboxes
        final CheckBox cb_en = (CheckBox) findViewById(R.id.cb_english);
        final CheckBox cb_pt = (CheckBox) findViewById(R.id.cb_port);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();

        String currentLanguage = config.locale.toString();

        if(currentLanguage.equals("pt")) {
            cb_pt.setChecked(true);
            cb_en.setChecked(false);
        } else {
            cb_pt.setChecked(false);
            cb_en.setChecked(true);
        }

        cb_en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_en.isChecked()) {
                    cb_pt.setChecked(false);
                } else {
                    cb_pt.setChecked(true);
                }
            }
        });

        cb_pt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_pt.isChecked()) {
                    cb_en.setChecked(false);
                } else {
                    cb_en.setChecked(true);
                }
            }
        });



        // Populate API field with API Key
        EditText editapi = (EditText) findViewById(R.id.editapi);
        UserDBHelper udb = new UserDBHelper(this);
        editapi.setText(udb.getActiveUser().getAPIKEY());

        // Populate Redcap URL field with stored URL
        EditText editRedcapURL = (EditText) findViewById(R.id.editredcapurl);
        editRedcapURL.setText(udb.getActiveUser().getRedcapURL());
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

    /* saveSettings - save the current settings and load the MainActivity.class Activity. */
    public void saveSettings(View v) {
        EditText apikeytext = (EditText) findViewById(R.id.editapi);
        EditText redcapurltext = (EditText) findViewById(R.id.editredcapurl);
        UserDBHelper udb = new UserDBHelper(this);
        User currentUser = udb.getActiveUser();

        String inputApiKey = apikeytext.getText().toString().trim();
        currentUser.setAPIKEY(inputApiKey);

        String inputRedcapURL = redcapurltext.getText().toString().trim();
        currentUser.setRedcapURL(inputRedcapURL);

        udb.updateUser(currentUser);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();

        String targetLanguage;

        CheckBox cb_en = (CheckBox) findViewById(R.id.cb_english);
        CheckBox cb_pt = (CheckBox) findViewById(R.id.cb_port);

        if( cb_en.isChecked() ) {
            targetLanguage = "en-US";
        } else {
            targetLanguage = "pt";
        }

        Locale newLang = new Locale(targetLanguage);

        config.locale = newLang;
        res.updateConfiguration(config, dm);

        try {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {}
    }

    /* changeLanguage - toggle between English and Portuguese languages */
    public void changeLanguage(View v) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();

        String targetLanguage;

        // Log.d("en-US", targetLanguage);

        if ( config.locale.toString().equals("pt") ) {
            targetLanguage = "en-US";
        } else {
            targetLanguage = "pt";
        }

        Locale newLang = new Locale(targetLanguage);

        config.locale = newLang;
        res.updateConfiguration(config, dm);

        // Reload current page with new language
        Intent refresh = new Intent(this, Settings.class);
        startActivity(refresh);
    }
}
