package diagnose.uvfree.uvfree;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Uploading extends Activity {
    PhotoDBHelper pdb;
    SQLiteHelper db;
    Bundle b;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploading);

        db = new SQLiteHelper(this);
        pdb = new PhotoDBHelper(this);
        b = this.getIntent().getExtras();

        AsyncLoad load = new AsyncLoad();
        load.execute();

    }


    protected void onDestroy() { super.onDestroy(); }

    private class AsyncLoad extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            // Retrieve list of UniqueIDs from the PhotoQueue Activity

            String[] uploadUIDstrings = b.getStringArray("instup");

            // Convert String array of UniqueIDs to an ArrayList of UniqueIDs as Strings
            ArrayList<String> uploadUIDs = new ArrayList<String>(Arrays.asList(uploadUIDstrings));

            // Create a list of DiagInstance from the UIDs fetched from the previous activity

            List<DiagInstance> diagInstances = db.getAllDiagInstances(); // All DiagInstance in db
            List<DiagInstance> targetInstances = new ArrayList<DiagInstance>(); // Target DiagInstance

            // Populate targetInstances based on UniqueID matches with DiagInstances in the db
            for (int i = 0; i < diagInstances.size(); i++) {
                for (int j = 0; j < uploadUIDs.size(); j++) {
                    if (diagInstances.get(i).getUID().equals(uploadUIDs.get(j))) {
                        targetInstances.add(diagInstances.get(i));
                    }
                }
            }

            List<String> allUIDs = new ArrayList<String>();

            // Make a list of all UniqueIDs so the related photos can be deleted as well
            for (int i = 0; i < targetInstances.size(); i++) {
                allUIDs.add(targetInstances.get(i).getUID());
            }

            /******************* Photo Upload to Redcap Code ******************/

            // Upload Files
            List<AbnormPhoto> targetPhotos = new ArrayList<AbnormPhoto>();
            for (int i = 0; i < targetInstances.size(); i++) {
                List<AbnormPhoto> photosThisUID = new ArrayList<AbnormPhoto>();
                photosThisUID = pdb.getPhoto(targetInstances.get(i).getUID());

                for (int j = 0; j < photosThisUID.size(); j++) {
                    targetPhotos.add(photosThisUID.get(j));
                }
            }


            RedcapHelper rhelp = new RedcapHelper(targetInstances, targetPhotos, getBaseContext());
            result = rhelp.upload(); // Upload to the Redcap database using the RedcapHelper class


            boolean success = true;

            if( result.isEmpty() ) {
                success = false;
            } else if(!result.substring(0,2).equals("id")) {
                success = false;
            }

            // Delete DiagInstance objects that were uploaded here from the database
            if(success == true) {
                for (int l = 0; l < targetInstances.size(); l++) {
                    db.deleteDiagInstance(targetInstances.get(l));
                }

                // Delete AbnormPhotos according to their UniqueIDs
                for (int i = 0; i < allUIDs.size(); i++) {
                    List<AbnormPhoto> photosToRm = pdb.getPhoto(allUIDs.get(i));
                    for (int j = 0; j < photosToRm.size(); j++) {
                        pdb.deletePhoto(photosToRm.get(j));

                        // Delete related photo files here
                        File p1 = new File(photosToRm.get(j).getImage1());
                        File p2 = new File(photosToRm.get(j).getImage2());
                        p1.delete();
                        p2.delete();
                    }
                }
            }

            try {
                Intent i = new Intent(getBaseContext(),UploadConfirm.class);
                i.putExtra("uploadedid",result);
                startActivity(i);
                finish();
            } catch(Exception e) {}

            return result;
        }

        protected void onPostExecute(Long result) {
            /*try {
                Intent i = new Intent(getBaseContext(),UploadConfirm.class);
                i.putExtra("uploadedid",result);
                startActivity(i);
                finish();
            } catch(Exception e) {}*/
        }
    }

}
