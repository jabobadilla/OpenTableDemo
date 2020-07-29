package com.bobadilla.opentabledemo.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bobadilla.opentabledemo.R;
import com.bobadilla.opentabledemo.common.Singleton;

public class CustomDialogJava extends DialogFragment implements View.OnClickListener {

    private String title, body, action;
    private int actionId;

    public static CustomDialogJava newInstance(String title, String body, String action, int actionId){
        CustomDialogJava custoDialog = new CustomDialogJava();

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("body", body);
        bundle.putString("action", action);
        bundle.putInt("actionId", actionId);
        custoDialog.setArguments(bundle);

        return custoDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;

        title = getArguments().getString("title");
        body = getArguments().getString("body");
        action = getArguments().getString("action");

        actionId = getArguments().getInt("actionId");

        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(v);

        TextView titleTv = (TextView)v.findViewById(R.id.title);
        titleTv.setText(title);

        TextView bodyTv = (TextView)v.findViewById(R.id.body);
        //bodyTv.setText(body);
        bodyTv.setText(Html.fromHtml(body));

        Button actionBtn = (Button)v.findViewById(R.id.action);
        actionBtn.setText(action);
        actionBtn.setOnClickListener(this);

        Button cancel = (Button)v.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        if(actionId == 100)
            cancel.setVisibility(View.VISIBLE);
        else
            cancel.setVisibility(View.INVISIBLE);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB){
            Log.e("solved super error", "solved super error OK");
        } else
            super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.action:
                if(actionId == 0){
                    Singleton.dissmissCustom();
                }
            case R.id.cancel:
                Singleton.dissmissCustom();
                break;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
