package com.speed.brainchallenge.stagethree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.speed.brainchallenge.GameMenuActivity;
import com.speed.brainchallenge.MainActivity;
import com.speed.brainchallenge.R;
import com.speed.brainchallenge.stageFour.StageFourActivity;
import com.speed.brainchallenge.utils.Constant;

import java.util.Random;

public class StageThreeActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private String username;
    private Boolean isHorizontal = null;
    private TextView answerView;
    private String answer;

    private EditText inputAnswer;

    private Chronometer timer;

    private Button btnSubmit;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stage_three);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(this);

        // get username from intent
        username = getIntent().getStringExtra(Constant.USERS);

        init(this);
        initSensor(this);
        initTimer(this);

        btnSubmit = findViewById(R.id.StageThreeSubmitBtn);
        btnSubmit.setOnClickListener(this);

        inputAnswer = findViewById(R.id.editTextAnswer);
        // start timer
        timer.start();
    }

    private void initTimer(Context context) {
         timer = findViewById(R.id.timer);

         // set the timer format to MM:SS not hh:mm:ss
         timer.setFormat("%s");
         // reset timer
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
    }

    private void initSensor(Context context) {
        // get Sensor object
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    public void init(Context context) {
        // get Answer text view
        answerView = findViewById(R.id.textViewAnswer);
        // generate a random 4-digit number
        Random random = new Random();
        answer = String.format("%04d",random.nextInt(10000));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Calculate the angle
        double newAngle = Math.toDegrees(Math.acos(z / Math.sqrt(x * x + y * y + z * z)));
        // if the angle is less than 45, then the phone is horizontal
        // else is vertical
        if (isHorizontal == null) {
            if (newAngle < 45) {
                isHorizontal = true;
            } else {
                isHorizontal = false;
            }
        }

        // if horizontal
        if (isHorizontal) {
            if (newAngle >= 80 && newAngle <= 90) {
                answerView.setText(answer);
                answerView.setTextColor(Color.WHITE);
            } else {
                answerView.setText("");
            }

            // Change Background color
           if (newAngle >= 0 && newAngle <= 90) {
               int alpha = (int) ((newAngle / 90) * 255);
               answerView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
           } else {
                answerView.setBackgroundColor(Color.WHITE);
           }

        } else {
            // if vertical
            if (newAngle >= 0 && newAngle <= 10) {
                answerView.setText(answer);
                answerView.setTextColor(Color.WHITE);
            } else {
                answerView.setText("");
            }
            // Change Background color
            if (newAngle >= 0 && newAngle <= 90) {
                int alpha = 255 - (int) ((newAngle / 90) * 255);
                answerView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
            } else {
                answerView.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.StageThreeSubmitBtn) {
            // stop timer
            timer.stop();
            // Check the input answer is correct
            doChecking();
            return;
        }

        if (v.getId() == R.id.backButton) {
            // go back to main menu
            Intent intent = new Intent(this, GameMenuActivity.class).putExtra(Constant.USERS, username);
            startActivity(intent);
        }

    }

    private void doChecking() {
        String input = inputAnswer.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter the answer", Toast.LENGTH_SHORT).show();
            timer.start();
            return;
        }

        if (!input.equals(answer)) {
            Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show();
            return;
        }
        // if answer is correct
        Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        // get the time in seconds
        long time = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000;

        // calculate score
        int score = calculateScore(time);
        // Save the records on sharepreferences
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.STAGETHREE + username, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constant.SCORE, score);
        editor.putLong(Constant.TIME, time);
        editor.apply();

        // Show dialog and go to next stage
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        dialog.setTitle("Congratulations");
        dialog.setMessage("You have completed the stage 3 with score " + score);
        dialog.setPositiveButton("Next", (dialog1, which) -> {
            // go to next stage
            Intent intent = new Intent(this, StageFourActivity.class).putExtra("username", username);
            startActivity(intent);
        });
        dialog.setNegativeButton("Back to Menu", (dialog1, which) -> {
            // go back to main menu
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        dialog.show();

    }

    private int calculateScore(long time) {
        int score = 2;
        // get 5 score if the time is within 5 minutes
        if (time <= 300) {
            score = 5;
        }
        // get 8 score if the time is within 2 minutes
        if (time <= 120) {
            score = 8;
        }
        // get 10 score if the time is within 1 minute
        if (time <= 60) {
            score = 10;
        }

        return score;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

}