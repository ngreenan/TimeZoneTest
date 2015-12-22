package com.ngreenan.mytimechecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Nick on 22/12/2015.
 */
public class AboutDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AboutDialogListener {
            public void onDialogPositiveClick(DialogFragment dialog);
    }

    // use this instance of the interface to deliver the action events
    AboutDialogListener mListener;

    // override fragment.onAttach() method to instantiate the AboutDialogListener


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //verify the host implements callback interface
        try {
            mListener = (AboutDialogListener) activity;
        } catch (ClassCastException e) {
            //interface not implemented, throw exception
            throw new ClassCastException(activity.toString() + " must implement AboutDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //we don't need to do anything here - it'll be handled in the parent activity via the AboutDialogListener interface
            }
        });
        return builder.create();
    }
}
