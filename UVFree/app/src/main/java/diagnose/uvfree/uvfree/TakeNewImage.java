package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

/* TakeNewImage - The TakeNewImage activity handles
 */
public class TakeNewImage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_new_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Date of Birth Select
        Spinner dobDaySpin = (Spinner) findViewById(R.id.dob_day);
        ArrayAdapter<CharSequence> dobDayAdapt = ArrayAdapter.createFromResource(this,
                R.array.dobday, android.R.layout.simple_spinner_dropdown_item);
        dobDayAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dobDaySpin.setAdapter(dobDayAdapt);

        Spinner dobMonthSpin = (Spinner) findViewById(R.id.dob_month);
        ArrayAdapter<CharSequence> dobMonthAdapt = ArrayAdapter.createFromResource(this,
                R.array.dobmonth,android.R.layout.simple_spinner_dropdown_item);
        dobMonthSpin.setAdapter(dobMonthAdapt);

        // Year is handled as an EditText

        // Gender Select
        Spinner genderSpin = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> genderAdapt = ArrayAdapter.createFromResource(this,R.array.genders,
                android.R.layout.simple_spinner_dropdown_item);
        genderAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpin.setAdapter(genderAdapt);

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

    /* takeFirstPhoto - Redirects to the Lesion Info screen */
    public void lesionInfoGo(View v) {
        // Pull the patient name typed by the user
        EditText patientName = (EditText) findViewById(R.id.patient_name);
        String patient = patientName.getText().toString();

        // Pull DOB
        Spinner dobDaySpin = (Spinner) findViewById(R.id.dob_day);
        String dobDay = dobDaySpin.getSelectedItem().toString();

        Spinner dobMonthSpin = (Spinner) findViewById(R.id.dob_month);
        String dobMonth = dobMonthSpin.getSelectedItem().toString();

        EditText dobYearET = (EditText) findViewById(R.id.dob_year);
        String dobYear = dobYearET.getText().toString();

        if ( dobMonth.equals("(1)Jan") ) { dobMonth = "1"; }
        else if ( dobMonth.equals("(2)Feb") ) { dobMonth = "2"; }
        else if ( dobMonth.equals("(3)Mar") ) { dobMonth = "3"; }
        else if ( dobMonth.equals("(4)Apr") ) { dobMonth = "4"; }
        else if ( dobMonth.equals("(5)May") ) { dobMonth = "5"; }
        else if ( dobMonth.equals("(6)Jun") ) { dobMonth = "6"; }
        else if ( dobMonth.equals("(7)Jul") ) { dobMonth = "7"; }
        else if ( dobMonth.equals("(8)Aug") ) { dobMonth = "8"; }
        else if ( dobMonth.equals("(9)Sep") ) { dobMonth = "9"; }
        else if ( dobMonth.equals("(10)Oct") ) { dobMonth = "10"; }
        else if ( dobMonth.equals("(11)Nov") ) { dobMonth = "11"; }
        else if ( dobMonth.equals("(12)Dec") ) { dobMonth = "12"; }

        // Compile a DOB string to pass into patient. Format: DAY-MONTH-YEAR
        String patientDOB = dobYear + "-" + dobMonth + "-" + dobDay;

        // Pull Gender
        Spinner genderSpin = (Spinner) findViewById(R.id.gender_spinner);
        String patientGender = genderSpin.getSelectedItem().toString();

        // If the patient name or birth year is blank, or no Month or day has been selected, produce an error message
        if( patient.trim().length() == 0 || dobDay.trim().equals("Day") || dobMonth.trim().equals("Month") || dobYear.trim().length() == 0 || dobYear.trim().equals("Year") ) {
            RegConfirmDialog blankSpaces = new RegConfirmDialog();
            blankSpaces.show(getSupportFragmentManager(), "empty_name_field");
        } else {
            // If the patient name is considered valid, do the following:

            // Pull the current logged in user (nurse) from the database
            UserDBHelper udb = new UserDBHelper(this);
            User currentUser = udb.getActiveUser();
            String nurseName = currentUser.getName();
            String nurseEmail = currentUser.getEmail();
            String nurseUsername = currentUser.getUsername();

            // Get the current date and time, and record as Strings
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            String imageDate = year + "-" + month + "-" + day;
            String imageTime = hour + ":" + minute + ":" + second;

            // Define a unique ID for this diagnostic instance - for parsing purposes
            String uniqueID = patient
                    + Integer.toString(month)
                    + Integer.toString(day)
                    + Integer.toString(year)
                    + Integer.toString(hour)
                    + Integer.toString(minute)
                    + Integer.toString(second);

            uniqueID = uniqueID.replaceAll("\\s", ""); // Remove spaces

            // Record GPS location
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            double gpslat;
            double gpslng;

            // If no location is available, set the lat/lng to 0. Otherwise, record the lat and lng.
            if( location == null ) {
                gpslat = 0;
                gpslng = 0;
            } else {
                gpslat = location.getLatitude();
                gpslng = location.getLongitude();
            }

            // Pull the Mother's Name and City Name from the corresponding fields
            EditText mothersNameET = (EditText) findViewById(R.id.mothers_name);
            String mothersName = mothersNameET.getText().toString();

            EditText cityNameET = (EditText) findViewById(R.id.city_name);
            String cityName = cityNameET.getText().toString();

            // Pull the radio button response for family and personal history
            RadioGroup famHistoryRG = (RadioGroup) findViewById(R.id.radio_famhistory);
            int famHistoryResID = famHistoryRG.getCheckedRadioButtonId();

            int famHistory;

            if(famHistoryResID == R.id.famhistory_yes) {
                famHistory = 1;
            } else if(famHistoryResID == R.id.famhistory_no) {
                famHistory = 2;
            } else {
                famHistory = 3;
            }

            RadioGroup personalHistoryRG = (RadioGroup) findViewById(R.id.radio_personalhistory);
            int personalHistoryResID = personalHistoryRG.getCheckedRadioButtonId();

            int personalHistory;

            if(personalHistoryResID == R.id.personalhistory_yes) {
                personalHistory = 1;
            } else if( personalHistoryResID == R.id.personalhistory_no) {
                personalHistory = 2;
            } else {
                personalHistory = 3;
            }

            // Get the patient's CPF number
            EditText cpfET = (EditText) findViewById(R.id.patient_cpf);
            String uniquePatientID = cpfET.getText().toString();

            // Create a new instance of the DiagInstance database, and add to database
            SQLiteHelper db = new SQLiteHelper(this);
            DiagInstance dInst = new DiagInstance(nurseName, nurseUsername, nurseEmail, patient,
                                                    patientDOB, patientGender, mothersName, cityName,
                                                    imageDate, imageTime, uniqueID, uniquePatientID, gpslat, gpslng,
                                                    famHistory, personalHistory, false);

            /**TODO: If the DiagInstance already exists in the database, update it */
            db.addDiagInstance(dInst);

            // Load the BodyLocation.class Activity
            try {
                Intent i = new Intent(getBaseContext(), BodyLocation.class);
                i.putExtra("uid", uniqueID); // Pass the UID to the next Activity
                startActivity(i);
            } catch (Exception e) {}
        }
    }
}
