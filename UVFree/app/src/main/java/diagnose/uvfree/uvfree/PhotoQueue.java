package diagnose.uvfree.uvfree;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class PhotoQueue extends ActionBarActivity {
    // The two fields in the PhotoQueue activity
    ListView lv; // ListView that holds a list of previews
    List<DiagInstance> diagInstances; // List of all DiagInstance objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_queue);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Pull all DiagInstance objects currently in the photo upload queue
        SQLiteHelper db = new SQLiteHelper(this);
        diagInstances = db.getAllDiagInstances();

        // Create a variable to refer to the ListView that will hold the preview list
        lv = (ListView) findViewById(R.id.queued_photos);

        // Create the ListViewAdapter that will handle this ListView
        ListViewAdapter adapter = new ListViewAdapter(this, diagInstances, false);

        /* Can't pass an empty list to the adapter, so if there are no DiagInstances
         *  just create a list of one empty DiagInstance and pass that along. */
        if (diagInstances.size() == 0) {
            DiagInstance di = new DiagInstance();
            diagInstances.add(di);
            adapter = new ListViewAdapter(this, diagInstances, true);
        }

        // Set this ListViewAdapter as the adapter for the ListView
        lv.setAdapter(adapter);
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

    /* uploadPhotos - Uploads the selected photos to the database */
    public void uploadPhotos(View v) {
        // First, create a list of the UniqueIDs of the DiagInstance objects to be uploaded
        List<String> toUpload = new ArrayList<String>();

        // If the DiagInstance object is checked, add its UniqueID to the list.
        for (int i = 0; i < diagInstances.size(); i++) {
            if (diagInstances.get(i).getChecked()) {
                toUpload.add(diagInstances.get(i).getUID());
            }
        }

        /* Create a simple String array of the UniqueIDs to be passed on, instead of a
         * List object, so it can be passed via the Intent.putExtras() function. */
        String[] toUploadArray = new String[toUpload.size()]; // Preallocate
        toUploadArray = toUpload.toArray(toUploadArray); // Convert

        // Add this String array into the Bundle to be passed to the next Activity
        Bundle b = new Bundle();
        b.putStringArray("instup", toUploadArray);

        // Load the Uploading.class Activity. where the uploads will occur and be confirmed.
        try {
            Intent i = new Intent(getBaseContext(), Uploading.class);
            i.putExtras(b);
            startActivity(i);
            finish();
        } catch (Exception e) {}
    }

    /* uploadAll - Upload all . */
    public void uploadAll(View v) {
        List<String> toUpload = new ArrayList<String>();

        for( int i = 0; i < diagInstances.size(); i++ ) {
            toUpload.add(diagInstances.get(i).getUID());
        }

        /* Create a simple String array of the UniqueIDs to be passed on, instead of a
         * List object, so it can be passed via the Intent.putExtras() function. */
        String[] toUploadArray = new String[toUpload.size()]; // Preallocate
        toUploadArray = toUpload.toArray(toUploadArray); // Convert

        // Add this String array into the Bundle to be passed to the next Activity
        Bundle b = new Bundle();
        b.putStringArray("instup", toUploadArray);

        // Load the Uploading.class Activity. where the uploads will occur and be confirmed.
        try {
            Intent i = new Intent(getBaseContext(), Uploading.class);
            i.putExtras(b);
            startActivity(i);
            finish();
        } catch (Exception e) {}
    }

    /* uploadPhotos - Deletes the selected database entries and related images */
    public void deletePhotos(View v) {
        // First, create a list of the UniqueIDs of the DiagInstance objects to be deleted
        List<DiagInstance> toDelete = new ArrayList<DiagInstance>();

        // If the DiagInstance object is checked, add it to the list.
        for (int i = 0; i < diagInstances.size(); i++) {
            if (diagInstances.get(i).getChecked()) {
                toDelete.add(diagInstances.get(i));
            }
        }

        // Delete the DiagInstance entries and AbnormPhotos associated
        SQLiteHelper db = new SQLiteHelper(this);
        PhotoDBHelper pdb = new PhotoDBHelper(this);

        for( int i = 0; i < toDelete.size(); i++ ) {
            db.deleteDiagInstance(toDelete.get(i));

            List<AbnormPhoto> uidPhoto = pdb.getPhoto(toDelete.get(i).getUID());

            for( int j = 0; j < uidPhoto.size(); j++ ) {
                pdb.deletePhoto(uidPhoto.get(j));
            }

        }

        // Refresh the activity
        onRestart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(getBaseContext(),PhotoQueue.class);
        startActivity(i);
        finish();
    }
}
