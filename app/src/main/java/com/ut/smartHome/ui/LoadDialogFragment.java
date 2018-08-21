package com.ut.smartHome.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ut.smartHome.R;

public class LoadDialogFragment extends DialogFragment{

    private TextView tip;
    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_load, null);
        tip = view.findViewById(R.id.tip);
        tip.setText(message);
        dialog.setContentView(view);
        return dialog;
    }

    public void setMessage(String text) {
        message = text;
    }
}
