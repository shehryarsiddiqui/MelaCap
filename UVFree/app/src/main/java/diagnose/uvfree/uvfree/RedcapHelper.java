package diagnose.uvfree.uvfree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.MultihomePlainSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/** RedcapHelper
 *   This class will take a list of diagInstance objects to upload, pull the appropriate local
 *   database information, including AbnormPhotos related to the diagInstances, and the API
 *   Key and Redcap Server URL that have been set up by each user. It will take that information
 *   and the contained fields and create the appropriate files, then open up a connection to
 *   the Redcap server and send the appropriate file by a POST connection.
 */
public class RedcapHelper {
    private List<DiagInstance> diagInstanceList;
    private List<AbnormPhoto> abnormPhotoList;
    private String APIKEY;
    private String RedcapURL;

    // Constructors
    public RedcapHelper(List<DiagInstance> diagInstanceList, List<AbnormPhoto> abnormPhotoList, Context context) {
        this.diagInstanceList = diagInstanceList;
        this.abnormPhotoList = abnormPhotoList;

        // Pull the API Key and Redcap URL from the User database
        UserDBHelper udb = new UserDBHelper(context);
        User currentUser = udb.getActiveUser();
        this.APIKEY = currentUser.getAPIKEY();
        this.RedcapURL = currentUser.getRedcapURL();

        // For testing going to try hardcoding REDCap info
        this.APIKEY = "6CED82787A50688E773F75955DFAFD82";
        this.RedcapURL = "http://hcbredcap.com.br/api/";

    }

    // Upload to Redcap
    public String upload() {

        // Define Redcap Variable Names
        String RC_recordID = "record_id";
        String RC_nurseName = "nursename";
        String RC_nurseEmail = "nurseemail";
        String RC_patientName = "patientname";
        String RC_patientDOB = "patientdob";
        String RC_patientGender = "patientgender";
        String RC_mothersname = "mothersname";
        String RC_cityname = "cityname";
        String RC_capDateTime = "capdatetime";
        String RC_uniquePatientID = "unique_patient_id";
        String RC_gpslat = "gpslat";
        String RC_gpslng = "gpslng";
        String RC_bodyRegion = "bodyregion";
        String RC_bodySide = "bodyside";
        String RC_famhistory = "famhistory";
        String RC_personalhistory = "personalhistory";
        String RC_image1 = "image1";
        String RC_image2 = "image2";
        String RC_timeoflesion = "timeoflesion";
        String RC_nursecomments = "nursecomments";

        // The fields to be sent to the Redcap Server
        String RedcapFields = RC_recordID + "," + RC_nurseName + "," + RC_nurseEmail + ","
                + RC_patientName + "," + RC_patientDOB + "," + RC_patientGender + ","
                + RC_mothersname + "," + RC_cityname + "," + RC_capDateTime + ","
                + RC_uniquePatientID + "," + RC_gpslat + "," + RC_gpslng + "," + RC_bodyRegion + ","
                + RC_bodySide + "," + RC_famhistory + "," + RC_personalhistory + ","
                + RC_timeoflesion + "," + RC_nursecomments + "\n";

        String qt = "\"";
        String di = "\",\""; // Divider between fields, basically prints this: ","

        String InstanceData = "";

        for (int i = 0; i < diagInstanceList.size(); i++ ) {

            DiagInstance dInst = diagInstanceList.get(i); // The DiagInstance to be uploaded

            // Build list of AbnormPhotos that match the UID
            List<AbnormPhoto> aPhotos = new ArrayList<AbnormPhoto>();
            for (int j = 0; j < abnormPhotoList.size(); j++ ) {
                if( abnormPhotoList.get(j).getParentUID().equals(dInst.getUID())) {
                    aPhotos.add(abnormPhotoList.get(j));
                }
            }

            // Convert Genders in dInst to 1/2 that Redcap supports
            if (dInst.getPatientGender().equals("Male")) {
                dInst.setPatientGender("1");
            } else {
                dInst.setPatientGender("2");
            }

            String RecordData = "";

            // Make a list of BodyLocations and BodySides for AbnormPhotos related to this dInst
            for(int j = 0; j < aPhotos.size(); j++) {
                List<String> decodedLoc = decodeBodyLoc(aPhotos.get(j).getBodyLocation());
                int loc = Integer.parseInt(decodedLoc.get(0));
                int side = Integer.parseInt(decodedLoc.get(1));

                // Add the body side
                String bodySide = Integer.toString(side);
                String bodyLoc = "";

                // Change location to reflect SNOMED CT Codes, and add to the array
                /*
                if( side == 1 ) {
                    if ( loc == 1 ) { bodyLoc = "361355005"; }
                    else if ( loc == 2 ) { bodyLoc = "368106002"; }
                    else if ( loc == 3 ) { bodyLoc = "368224007"; }
                    else if ( loc == 4 ) { bodyLoc = "368107006"; }
                    else if ( loc == 5 ) { bodyLoc = "368225008"; }
                    else if ( loc == 6 ) { bodyLoc = "264242009"; }
                    else if ( loc == 7 ) { bodyLoc = "302553009"; }
                    else if ( loc == 8 ) { bodyLoc = "209570001"; }
                    else if ( loc == 9 ) { bodyLoc = "213289002"; }
                    else if ( loc == 10 ) { bodyLoc = "209672000"; }
                    else if ( loc == 11 ) { bodyLoc = "213384005"; }
                } else {
                    if ( loc == 1 ) { bodyLoc = "361355005"; }
                    else if ( loc == 2 ) { bodyLoc = "368107006"; }
                    else if ( loc == 3 ) { bodyLoc = "368225008"; }
                    else if ( loc == 4 ) { bodyLoc = "368106002"; }
                    else if ( loc == 5 ) { bodyLoc = "368224007"; }
                    else if ( loc == 6 ) { bodyLoc = "35549004"; }
                    else if ( loc == 7 ) { bodyLoc = "37822005"; }
                    else if ( loc == 8 ) { bodyLoc = "209672000"; }
                    else if ( loc == 9 ) { bodyLoc = "213384005"; }
                    else if ( loc == 10 ) { bodyLoc = "209570001"; }
                    else if ( loc == 11 ) { bodyLoc = "213289002"; }
                }
                */


                if( side == 1 ) {
                    if ( loc == 6 ) { bodyLoc = "445"; }
                    else if ( loc == 7 ) { bodyLoc = "446"; }
                    else if ( loc == 8 ) { bodyLoc = "447"; }
                } else {
                    if ( loc == 1 ) { bodyLoc = "440"; }
                    else if ( loc == 2 ) { bodyLoc = "441"; }
                    else if ( loc == 3 ) { bodyLoc = "442"; }
                    else if ( loc == 4 ) { bodyLoc = "443"; }
                    else if ( loc == 5 ) { bodyLoc = "444"; }
                }



                int timeoflesion = aPhotos.get(j).getTimeofLesion();
                String nursecomments = aPhotos.get(j).getNurseComments();

                // Build upload string for each record
                RecordData = RecordData + "\n" + qt + dInst.getUID() + "r" + Integer.toString(j+1)
                        + di + dInst.getNurse() + di + dInst.getNurseEmail() + di + dInst.getPatient()
                        + di + dInst.getPatientDOB() + di + dInst.getPatientGender()
                        + di + dInst.getMothersName() + di + dInst.getCityName() + di + dInst.getDate()
                        + " " + dInst.getTime() + di + dInst.getPatientUID() + di + dInst.getLatitude()
                        + di + dInst.getLongitude() + di + bodyLoc + di + bodySide
                        + di + Integer.toString(dInst.getFamHistory()) + di + Integer.toString(dInst.getPersonalHistory())
                        + di + Integer.toString(timeoflesion) + di + nursecomments + qt;
            }

            InstanceData = InstanceData + RecordData;

        }

        // Prepare upload of records (not files)
        List<NameValuePair> eparams = new ArrayList<NameValuePair>();

        // Add appropriate API parameters
        eparams.add(new BasicNameValuePair("token", APIKEY));
        eparams.add(new BasicNameValuePair("content", "record"));
        eparams.add(new BasicNameValuePair("format", "csv"));
        eparams.add(new BasicNameValuePair("type", "flat"));
        eparams.add(new BasicNameValuePair("data", RedcapFields+InstanceData));
        eparams.add(new BasicNameValuePair("returnContent", "ids"));
        eparams.add(new BasicNameValuePair("returnFormat", "csv"));

        // Open HTTP POST Connection
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(RedcapURL);

        String httpOutput = "No Output";

        // Set Networking to run on Main Thread (bad practice / change if time)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Send POST connection to Redcap Server
        try {
            httppost.setEntity(new UrlEncodedFormEntity(eparams));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            httpOutput = EntityUtils.toString(entity);
            entity.consumeContent();
        } catch (UnsupportedEncodingException uee) {
            Log.d("Unsupported Encoding", uee.getMessage());
        } catch (IOException ioe) {
            Log.d("IO Error", ioe.getMessage());
        }

        /***** Upload Photos Here ******/
        // Upload all files
        for(int i = 0; i < diagInstanceList.size(); i++) {
            DiagInstance dInst = diagInstanceList.get(i);

            // Build list of AbnormPhotos that match the UID
            List<AbnormPhoto> aPhotos = new ArrayList<AbnormPhoto>();
            for (int j = 0; j < abnormPhotoList.size(); j++ ) {
                if( abnormPhotoList.get(j).getParentUID().equals(dInst.getUID())) {
                    aPhotos.add(abnormPhotoList.get(j));
                }
            }

            for (int j = 0; j < aPhotos.size(); j++) {
                // Do file upload here - reason is to encode record_id correctly with r1, r2, etc.
                AbnormPhoto toUpload = aPhotos.get(j);

                // Image 1
                File img1 = new File(toUpload.getImage1());
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                //Bitmap bmp1 = BitmapFactory.decodeFile(img1.getAbsolutePath());
                Bitmap bmp1 = decodeSampledBitmapFromFile(img1.getAbsolutePath(),2592,1944);
                bmp1.compress(Bitmap.CompressFormat.PNG, 80, baos1);
                byte[] byte1 = baos1.toByteArray();

                String boundary = "-------------" + System.currentTimeMillis();

                httppost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);

                ByteArrayBody bab1 = new ByteArrayBody(byte1, dInst.getPatient()+"_"+toUpload.getBodyLocation()+"_"+"near.png");

                HttpEntity entity1 = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .setBoundary(boundary)
                        .addTextBody("token",APIKEY)
                        .addTextBody("content","file")
                        .addTextBody("action","import")
                        .addTextBody("record",dInst.getUID()+"r"+Integer.toString(j+1))
                        .addTextBody("field",RC_image1)
                        .addPart("file",bab1)
                        .build();

                httppost.setEntity(entity1);

                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    entity.consumeContent();
                } catch(IOException ioe) {
                    Log.d("File IO Error",ioe.getMessage());
                }


                // Image 2
                File img2 = new File(toUpload.getImage2());
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                //Bitmap bmp2 = BitmapFactory.decodeFile(img2.getAbsolutePath());
                Bitmap bmp2 = decodeSampledBitmapFromFile(img2.getAbsolutePath(),2592,1944);
                bmp2.compress(Bitmap.CompressFormat.PNG, 80, baos2);
                byte[] byte2 = baos2.toByteArray();

                boundary = "-------------" + System.currentTimeMillis();

                httppost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);

                ByteArrayBody bab2 = new ByteArrayBody(byte2, dInst.getPatient()+"_"+toUpload.getBodyLocation()+"_"+"far.png");

                HttpEntity entity2 = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .setBoundary(boundary)
                        .addTextBody("token",APIKEY)
                        .addTextBody("content","file")
                        .addTextBody("action","import")
                        .addTextBody("record",dInst.getUID()+"r"+Integer.toString(j+1))
                        .addTextBody("field",RC_image2)
                        .addPart("file",bab2)
                        .build();

                httppost.setEntity(entity2);

                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    entity.consumeContent();
                } catch(IOException ioe) {
                    Log.d("File IO Error",ioe.getMessage());
                }

            }
        }

        return httpOutput;
    }

    static public List<String> decodeBodyLoc( String bodyLocation ) {
        // Split the string at ,
        String[] splitFirst = bodyLocation.split(",");

        // Split the resulting Strings at colons (:) and pull the resulting numbers
        String location = splitFirst[0].split(":")[1];
        String side = splitFirst[1].split(":")[1];
        //String xCoord = splitFirst[2].split(":")[1];
       // String yCoord = splitFirst[3].split(":")[1];

        // Set up a list of strings as the output, add the strings from above, and return it
        List<String> output = new ArrayList<String>();
        output.add(location);
        output.add(side);
        //output.add(xCoord);
        //output.add(yCoord);

        return output;
    }


    // Attempting some Bitmap compression code thing to help with the OOM problem

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        //TODO: appcrash caused by decodeFile, fix commented lines, inJustDecodeBounds = true
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
//        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap myBitmap = Bitmap.createBitmap(reqWidth,reqHeight,Bitmap.Config.RGB_565);
        //return BitmapFactory.decodeFile(path,options);
        return myBitmap;
    }


}
