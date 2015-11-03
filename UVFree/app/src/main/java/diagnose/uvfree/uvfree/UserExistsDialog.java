package diagnose.uvfree.uvfree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/* UserExistsDialog - this class defines the DialogFragment object that will be created when the
                         user tries to register an account with a name that already exists.
 */
public class UserExistsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.user_exists) // Prompt to be displayed
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog when user presses "Ok" button
                    }
                });

        return builder.create(); // Create the dialog box
    }
}
