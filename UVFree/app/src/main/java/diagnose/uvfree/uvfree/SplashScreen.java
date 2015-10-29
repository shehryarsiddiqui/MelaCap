package diagnose.uvfree.uvfree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/* SplashScreen.class
 *  The SplashScreen Activity shows the UV Free Diagnose image for a second
 *  prior to starting the app.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        /* Pause for one second on this screen */
        Thread bg = new Thread() {
            public void run() {
                try {
                    sleep(2500); // Pause for 2.5 second

                    // Now load the Login screen aka start the app
                    Intent i = new Intent(getBaseContext(), Login.class);
                    startActivity(i);

                    finish();
                } catch (Exception e) {}
            }
        };

        bg.start();
    }

    protected void onDestroy() { super.onDestroy(); }
}
