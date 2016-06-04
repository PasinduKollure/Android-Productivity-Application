package com.pasindukollure.productivityappbtns;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void navigationBtns(View view) {

        Button btn = (Button)view;
        String btnTxt = btn.getText().toString();
        Intent open;

        if (btnTxt.matches("Notes")) {
            open = new Intent(this, NotesActivity.class);
            startActivity(open);
        } else if (btnTxt.matches("Timer")) {
            open = new Intent(this, TimerActivity.class);
            startActivity(open);
        } else if(btnTxt.matches("Reminders")){
            open = new Intent(this, RemindersActivity.class);
            startActivity(open);
        } else {
            AlertDialog.Builder calenderPopUp = new AlertDialog.Builder(this);
            calenderPopUp.setTitle("Coming Soon")
                    .setMessage("Press OK")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}


