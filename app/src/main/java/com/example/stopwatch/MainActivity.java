package com.example.stopwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView imgClock;
    NumberPicker numberPicker;
    Button btnMain, btnLap, btnReset;
    Chronometer chrono;
    ListView lst;
    private long countdownDuration;
    private CountDownTimer countDownTimer;
    private long remainingCountdownTime;  // Store remaining countdown time when paused
    private boolean isRunning = false;
    private long pauseOffset;
    TextView tvTime;
    private ArrayList<String> lapTimes;
    private ArrayAdapter<String> lapAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgClock = findViewById(R.id.imgClock);
        tvTime = findViewById(R.id.tvTime);
        btnMain = findViewById(R.id.btnMain);
        btnLap = findViewById(R.id.btnLap);
        btnReset = findViewById(R.id.btnReset);
        chrono = findViewById(R.id.chrono);
        lst = findViewById(R.id.lst);
        lapTimes = new ArrayList<>();
        lapAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapTimes);
        lst.setAdapter(lapAdapter);


        imgClock.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
//                        remainingCountdownTime = countdownDuration - (SystemClock.elapsedRealtime() - chrono.getBase());
//                        chrono.setBase(SystemClock.elapsedRealtime() - remainingCountdownTime);
                    }
                    chrono.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
                    btnMain.setText("Run");
                } else {
                    if(tvTime.getText().toString().equals("00:00")){
                        chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        chrono.start();
                        btnMain.setText("Stop");
                    }
                    else{
                        if (countDownTimer == null) {
                            int min = numberPicker.getValue(); // Get the selected minutes from the user
                            countdownDuration = min * 60 * 1000; // Convert minutes to milliseconds
                            startCountDown();
                        } else {
//                            startCountDown();
                            countDownTimer.start();
                        }
                        btnMain.setText("Stop");

                    }

                }

                isRunning = !isRunning;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                chrono.stop();
                chrono.setBase(SystemClock.elapsedRealtime());
                lapTimes.clear();
                lapAdapter.notifyDataSetChanged();
                isRunning = false;
                chrono.setText("00:00");
                btnMain.setText("Run");
                tvTime.setText("00:00");
                pauseOffset = 0;
            }
        });

        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lapTime = chrono.getText().toString();
                lapTimes.add(0, lapTime);
                lapAdapter.notifyDataSetChanged();
                lst.setSelection(0);
                lst.setCacheColorHint(Color.BLACK);
            }
        });

    }
    private void startCountDown(){

        countDownTimer = new CountDownTimer(countdownDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished / 1000;
                long minutes = remainingSeconds / 60;
                long seconds = remainingSeconds % 60;
                String time = String.format("-%02d:%02d", minutes, seconds);
                chrono.setText(time);
            }

            @Override
            public void onFinish() {
                chrono.stop();
                chrono.setText("00:00");
                isRunning = false;
                btnMain.setText("Run");
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showTimeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.set_time, null);
        builder.setView(dialogView);
//        builder.setView(R.layout.set_time);
        final Dialog dialog = builder.create();
        numberPicker = dialogView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(59);
        // Set the text color to black
        numberPicker.setTextColor(Color.BLACK);
        dialog.show();
        dialogView.findViewById(R.id.btnSetTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String min = String.valueOf(numberPicker.getValue()).toString();
                tvTime.setText(min + " min.");
                dialog.dismiss();
            }
        });
    }
}