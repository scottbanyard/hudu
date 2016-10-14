////// custom selected listener class for country spinner ///////

package com.example.nathan.myday;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;


public class CustomOnItemSelectedListener implements OnItemSelectedListener {
    private String item;

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        // changes colour of text to grey and sets text size of selected country
        ((TextView) view).setTextColor(Color.parseColor("#888888"));
        ((TextView) parent.getChildAt(0)).setTextSize(20);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}