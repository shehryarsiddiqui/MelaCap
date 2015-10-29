package diagnose.uvfree.uvfree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/* WrongPasswordDialog - this class defines the DialogFragment object that will be created when the
                         user enters an incorrect password into the Login page.
 */
public class WrongPasswordDialog extends DialogFragment {

    // onCreateDialog method is automatically called when this fragment is created
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.wrong_password) // The message to be shown
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog when user clicks the "Positive Button", ie. "Ok"
                    }
                });

        return builder.create(); // Create the box
    }
}
