package diagnose.uvfree.uvfree;

/* User - An object that handles the data about each User in the database. */
public class User {
    private String username; // The username of the user
    private String password; // The password of the user
    private String APIKEY; // The API Key the user has entered to connect to their RedCap Instance
    private String redcapURL; // The URL to the user's RedCap instance
    private String name; // The user's real name (first and last)
    private String email; // The user's email address
    private int loggedIn; // An integer that represents whether or not the User is logged in

    // Constructors
    public User() {}

    public User(String user, String pass, String name, String email, int loggedIn) {
        this.username = user;
        this.password = pass;
        this.APIKEY = "No API Key"; // API Key will be assigned later by the user
        this.redcapURL = "No Redcap URL"; // URL to Redcap server - to be assigned by the user
        this.name = name;
        this.email = email;
        this.loggedIn = loggedIn;
    }

    // Methods
    // toString - outputs some of the User's fields as a String - mainly for debug reasons.
    @Override
    public String toString() {
        return "User: " + username + " with name: " + name + ", and with email address: " + email
                + " Logged In? " + Integer.toString(loggedIn) + ".";
    }

    // Getters and Setters
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String user) {
        this.username = user;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getAPIKEY() {
        return this.APIKEY;
    }
    public void setAPIKEY(String APIKEY) {
        this.APIKEY = APIKEY;
    }

    public String getRedcapURL() {
        return this.redcapURL;
    }
    public void setRedcapURL(String redcapURL) {
        this.redcapURL = redcapURL;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoggedIn() {
        return this.loggedIn;
    }
    public void setLoggedIn(int loggedIn) {
        this.loggedIn = loggedIn;
    }
}
