package com.confessionsearch.release1;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogWindow extends DialogFragment implements  View.OnClickListener{
private static String TITLE = "title";
public Activity activity;

private static String MESSAGE= "message";
public static Button yesButton, noButton;

    public DialogWindow() {
        super();
    }

    public  DialogWindow(Activity importAct){

        activity = importAct;
}

public static DialogWindow newInstance(Button yes, Button no,
String titleValue, String msgValue)
{
    Bundle bundle = new Bundle();
   DialogWindow alertWindow = new DialogWindow();
   bundle.putString(MESSAGE,msgValue);
   bundle.putString(TITLE,titleValue);
   yesButton = yes;noButton=no;
   alertWindow.setArguments(bundle);
   return alertWindow;
}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
   //     return super.onCreateView(inflater, container, savedInstanceState);
   View view = inflater.inflate(R.layout.dialog_layout,container,false);
Button cancelButton = view.findViewById(R.id.NoButton);
        TextView titlebox = view.findViewById(R.id.titleView);
        TextView subTitleView = view.findViewById(R.id.subTitleView);
        titlebox.setText(getArguments().getString(TITLE,TITLE));
        subTitleView.setText(getArguments().getString(MESSAGE,MESSAGE));
Button confirmButton = view.findViewById(R.id.YesButton);
cancelButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        noButton.performClick(); dismiss();
    }
});
confirmButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        yesButton.performClick();dismiss();
    }
});
return view;
    }

    @Override
    public void onClick(View v) {

    }
}
