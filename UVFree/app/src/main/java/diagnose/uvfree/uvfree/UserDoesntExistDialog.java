package diagnose.uvfree.uvfree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/* UserDoesntExistDialog - this class defines the DialogFragment object that will be created when
                           the user tries to log in with a username that doesn't exist.
 */
public class UserDoesntExistDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.user_doesnt_exist) // Dialog to be displayed to the user
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog when the user presses "Ok"
                    }
                });

        return builder.create(); // Create the dialog box
    }
}
