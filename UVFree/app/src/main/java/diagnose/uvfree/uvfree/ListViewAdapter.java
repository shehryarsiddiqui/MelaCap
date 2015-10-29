package diagnose.uvfree.uvfree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<DiagInstance> {
    // The core fields of this adapter
    List<DiagInstance> diagInstances = null; // Begin with no DiagInstances in the adapter
    Context context; // To match ArrayAdapter constructor
    boolean isEmpty; // Used in constructor to differentiate between an empty adapter and a full one

    // Constructor
    public ListViewAdapter(Context context, List<DiagInstance> instances, boolean isEmpty) {
        super(context, R.layout.queue_row, instances);
        this.context = context;
        this.diagInstances = instances;
        this.isEmpty = isEmpty;
    }

    // Main Method
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /* Use a LayoutInflater to set the view, which represents one row of the ListView
         *  to be the queue_row layout. */
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.queue_row, parent, false);
        convertView.setClickable(true); // Make each row clickable

        // Detect if the user taps each row, and launch a preview of the DiagInstance
        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    // Load the PreviewDiag class - the DiagInstance preview page
                    Intent i = new Intent(getContext(), PreviewDiag.class);
                    // Pass the 'position' in the list that was clicked to the preview page
                    // This is required so the PreviewDiag class knows which one to display
                    i.putExtra("position", Integer.toString(position));
                    context.startActivity(i);
                } catch (Exception e) {}
            }
        });

        // Pull the LinearLayout in queue_row, the patient name TextView, and the checkbox
        final LinearLayout queueRow = (LinearLayout) convertView.findViewById(R.id.queue_row);
        TextView patientNameTV = (TextView) convertView.findViewById(R.id.queue_text);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.check_box);

        /* If the current DiagInstance object shows that it should be checked, set the checkbox to
         * the checked state and change the background color to be darker. If is it not checked,
         * be sure to return the background color to default (otherwise rows with checked boxes that
         * are later unchecked will remain differently colored */
        if( diagInstances.get(position).getChecked() ) {
            cb.setChecked(true);
            queueRow.setBackgroundColor(Color.parseColor("#B4B4B4"));
        } else {
            queueRow.setBackgroundColor(Color.parseColor("#E1E1E1"));
        }

        /* If the adapter is empty, print a message. Otherwise, add patient information to the row. */
        if( isEmpty ) {
            cb.setVisibility(View.GONE); // Remove the checkbox
            patientNameTV.setText(R.string.photo_queue_empty);
        } else {
            String patientName = diagInstances.get(position).getPatient();
            String output = patientName + " - " +
                    diagInstances.get(position).getDate() +
                    " @ " + diagInstances.get(position).getTime();
            patientNameTV.setText(output);
        }

        /* Listener that handles checking and unchecking of checkboxes, as well as background color */
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    queueRow.setBackgroundColor(Color.parseColor("#B4B4B4"));
                    diagInstances.get(position).setChecked(true);
                } else if (!isChecked) {
                    queueRow.setBackgroundColor(Color.parseColor("#E1E1E1"));
                    diagInstances.get(position).setChecked(false);
                }
            }
        });

        return convertView; // Return the changed view
    }

}
