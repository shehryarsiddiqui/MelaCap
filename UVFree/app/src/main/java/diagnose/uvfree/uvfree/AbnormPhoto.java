package diagnose.uvfree.uvfree;

/** AbnormPhoto - An object that contains the photo information for one lesion on one patient */
public class AbnormPhoto {
    // Fields
    private String parentUID; // The UniqueID of the patient being diagnosed -- related to DiagInstance object
    private String bodyLocation; // Location on the body where the lesion is located
    private String image1; // First image (with standoff device)
    private String image1post; // Post-Processed (Calibrated) first image
    private String image2; // Second image (with longer device)
    private String image2post; // Post-Processed (Calibrated) second image
    private int timeOfLesion; // Amount of time patient has had lesion: 1 = <30day, 2 = 1-6 mo, 3 = 6+ mo, 4 = I dont know
    private String nurseComments; // Comments entered by the nurse

    // Constructors
    public AbnormPhoto() {}

    public AbnormPhoto(String parentUID, String bodyLocation) {
        this.parentUID = parentUID;
        this.bodyLocation = bodyLocation;
    }

    public AbnormPhoto(String parentUID, String bodyLocation, String image1, String image2) {
        this.parentUID = parentUID;
        this.bodyLocation = bodyLocation;
        this.image1 = image1;
        this.image2 = image2;
    }

    public AbnormPhoto(String parentUID, String bodyLocation, String image1, String image1post, String image2, String image2post,
                       int timeOfLesion, String nurseComments) {
        this.parentUID = parentUID;
        this.bodyLocation = bodyLocation;
        this.image1 = image1;
        this.image1post = image1post;
        this.image2 = image2;
        this.image2post = image2post;
        this.timeOfLesion = timeOfLesion;
        this.nurseComments = nurseComments;
    }

    // Getters and Setters
    public String getParentUID() { return this.parentUID; }
    public String getBodyLocation() { return this.bodyLocation; }
    public String getImage1() { return this.image1; }
    public String getImage1post() { return this.image1post; }
    public String getImage2() { return this.image2; }
    public String getImage2post() { return this.image2post; }
    public int getTimeofLesion() { return this.timeOfLesion; }
    public String getNurseComments() { return this.nurseComments; }
    public void setParentUID(String parentUID) { this.parentUID = parentUID; }
    public void setBodyLocation(String bodyLocation) { this.bodyLocation = bodyLocation; }
    public void setImage1(String image1) { this.image1 = image1; }
    public void setImage1post(String image1post) { this.image1post = image1post; }
    public void setImage2(String image2) { this.image2 = image2; }
    public void setImage2post(String image2post) { this.image2post = image2post; }
    public void setTimeOfLesion(int timeOfLesion) { this.timeOfLesion = timeOfLesion; }
    public void setNurseComments(String nurseComments) { this.nurseComments = nurseComments; }
}