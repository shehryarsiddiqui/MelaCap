package diagnose.uvfree.uvfree;

public class DiagInstance {
    // The fields recorded in each instance of diagnosis
    private String nurseName; // Nurse Name
    private String nurseUsername; // Nurse username
    private String nurseEmail; // Nurse email address (For delivering diagnostic results)
    private String patientName; // Patient Name
    private String patientDOB; // Patient Date of Birth
    private String patientGender; // Patient Gender
    private String mothersName; // Patient's Mother's Name
    private String cityName; // Patient's City's Name
    private String date; // Date of image capture
    private String time; // Time of image capture
    private String uniqueID; // Unique ID for each instance (used for database parsing purposes)
    private String uniquePatientID; // An ID that can be used to track one patient over time.
    private double GPSlat; // Latitude, captured via GPS
    private double GPSlng; // Longitude, captured via GPS
    private int famHistory; // Family History: 1 = Yes, 2 = No, 3 = I don't know
    private int personalHistory; // Personal History: 1 = Yes, 2 = No, 3 = I don't know
    private boolean checked; // For use when displaying in a list. Whether the entry is checked or not.

    // Constructors
    public DiagInstance() {
        checked = false;
    }

    public DiagInstance(String nurseName, String nurseUsername, String nurseEmail, String patientName,
                        String patientDOB, String patientGender, String mothersName, String cityName,
                        String date, String time, String uniqueID, String uniquePatientID, double GPSlat, double GPSlng,
                        int famHistory, int personalHistory,boolean checked) {
        this.nurseName = nurseName;
        this.nurseUsername = nurseUsername;
        this.nurseEmail = nurseEmail;
        this.patientName = patientName;
        this.patientDOB = patientDOB;
        this.patientGender = patientGender;
        this.mothersName = mothersName;
        this.cityName = cityName;
        this.date = date;
        this.time = time;
        this.uniqueID = uniqueID;
        this.uniquePatientID = uniquePatientID;
        this.GPSlat = GPSlat;
        this.GPSlng = GPSlng;
        this.famHistory = famHistory;
        this.personalHistory = personalHistory;
        this.checked = checked;
    }

    // Methods
    /* toString - overrides default method. Outputs this DiagInstance as a String */
    public String toString() {
        return "DiagInstance [nurse=" + nurseName + ", nurse email=" + nurseEmail + ", patient=" + patientName +
                " Date=" + date + " time=" + time + " UID=" + uniqueID + " Patient UID="
                + uniquePatientID + "] ";
    }

    // Getters and Setters
    public String getNurse() {
        return this.nurseName;
    }
    public void setNurse(String nurse) {
        this.nurseName = nurse;
    }
    public String getNurseUsername() {
        return this.nurseUsername;
    }
    public void setNurseUsername(String nurseUsername) {
        this.nurseUsername = nurseUsername;
    }
    public String getNurseEmail() {
        return this.nurseEmail;
    }
    public void setNurseEmail(String nurseEmail) {
        this.nurseEmail = nurseEmail;
    }
    public String getPatient() {
        return this.patientName;
    }
    public void setPatient(String patient) {
        this.patientName = patient;
    }
    public String getPatientDOB() {
        return this.patientDOB;
    }
    public void setPatientDOB(String patientDOB) {
        this.patientDOB = patientDOB;
    }
    public String getPatientGender() {
        return this.patientGender;
    }
    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }
    public String getMothersName() {
        return this.mothersName;
    }
    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }
    public String getCityName() {
        return this.cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getUID() {
        return this.uniqueID;
    }
    public void setUID(String uid) {
        this.uniqueID = uid;
    }
    public String getPatientUID() {
        return this.uniquePatientID;
    }
    public void setPatientUID(String patientuid) {
        this.uniquePatientID = patientuid;
    }
    public double getLatitude() {
        return this.GPSlat;
    }
    public void setLatitude(double latitude) {
        this.GPSlat = latitude;
    }
    public double getLongitude() {
        return this.GPSlng;
    }
    public void setLongitude(double longitude) {
        this.GPSlng = longitude;
    }
    public int getFamHistory() {
        return this.famHistory;
    }
    public void setFamHistory(int famHistory) {
        this.famHistory = famHistory;
    }
    public int getPersonalHistory() {
        return this.personalHistory;
    }
    public void setPersonalHistory(int personalHistory) {
        this.personalHistory = personalHistory;
    }
    public boolean getChecked() {
        return this.checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
