package com.pasindukollure.productivityappbtns;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {

    TextView timeDisplay;
    SeekBar timerBar;
    CountDownTimer countDownTimer;
    Button startBtn;
    Button focusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        timerBar = (SeekBar) findViewById(R.id.seekBar);
        focusBtn = (Button) findViewById(R.id.focusBtn);
        startBtn = (Button) findViewById(R.id.startBtn);

        timerBar.setMax(1800);
        timerBar.setProgress(30);

        timerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void beginTimer(View view) {
        countDownTimer(timerBar.getProgress());
    }

    public void startFocusTimer(View view) {
        Handler delayer = new Handler();

        countDownTimer(1500);
        delayer.postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownTimer(300);
            }
        }, 1500000);
    }

    public void timer(int setTime) {
        int minutes = setTime / 60;
        int seconds = setTime % 60;

        String minString = String.valueOf(minutes);

        if(seconds <= 9){
            String secString = String.valueOf(seconds);
            secString = "0"+secString;
            timeDisplay.setText(minString+":"+secString);
        } else {
            String secString = String.valueOf(seconds);
            timeDisplay.setText(minString+":"+secString);
        }
    }

    public void countDownTimer(int time) {
        timerBar.setEnabled(false);
        startBtn.setEnabled(false);
        focusBtn.setEnabled(false);

        timerBar.setProgress(time); //25 minutes

        countDownTimer = new CountDownTimer(time*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secLeft = ((int) millisUntilFinished) / 1000;
                timer(secLeft);
            }

            @Override
            public void onFinish() {
                timerBar.setEnabled(true);
                startBtn.setEnabled(true);
                focusBtn.setEnabled(true);
                timeDisplay.setText("0:00");
            }
        }.start();
    }
}



