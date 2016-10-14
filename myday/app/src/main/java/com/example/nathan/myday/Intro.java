package com.example.nathan.myday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class Intro extends AppCompatActivity {
    Button main_menu;
    private String password;
    private String fromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().setTitle(R.string.title_activity_intro);
        main_menu = (Button) findViewById(R.id.button_main_menu);
        main_menu.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                menu_button_click();
            }
        });
    }

    Boolean menu_button_click(){
        Intent intent = getIntent();
        fromLogin = intent.getExtras().getString("fromLogin");
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        // only tries to get password if switched to intro from login (not from 3 dot menu)
        if (fromLogin.equals("true")) {
            password = intent.getExtras().getString("password");
            i.putExtra("password", password);
        }
        startActivity(i);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        return true;
    }


}
