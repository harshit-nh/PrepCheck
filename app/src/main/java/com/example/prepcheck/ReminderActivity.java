package com.example.prepcheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.shape.MarkerEdgeTreatment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ReminderActivity extends AppCompatActivity {

    EditText dateTxt, timeTxt;
    Button confirmBtn;
    TextView confirmDate, confirmTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        createNotificationChannel();


        dateTxt= findViewById(R.id.dateEditTxt);
        timeTxt = findViewById(R.id.timeEditTxt);
        confirmBtn = findViewById(R.id.confirmBtn);
        confirmDate = findViewById(R.id.confirmDate1);
        confirmTime = findViewById(R.id.confirmTime1);


        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        timeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedDate = dateTxt.getText().toString().trim();
                String selectedTime = timeTxt.getText().toString().trim();

                if (selectedDate.isEmpty()) {
                    Toast.makeText(ReminderActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                } else if (selectedTime.isEmpty()) {
                    Toast.makeText(ReminderActivity.this, "Please select a time", Toast.LENGTH_SHORT).show();
                } else {
                    // Both date and time are selected, proceed with confirmation

                    Calendar calendar = parseTime(selectedTime);
                    if(calendar != null){

                        long timeInMillis = calendar.getTimeInMillis();

                        Intent intent = new Intent(ReminderActivity.this, NotificationBroadcastReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,0, intent, PendingIntent.FLAG_IMMUTABLE);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInMillis, pendingIntent);

                        Toast.makeText(ReminderActivity.this, "Your reminder has been set", Toast.LENGTH_SHORT).show();
                        updateConfirmation(v);
                    }else{
                        Toast.makeText(ReminderActivity.this, "Error parsing time", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        setStatusBarColor(getResources().getColor(R.color.status_bar_color3));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Method to change status bar color
    private void setStatusBarColor(@ColorInt int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }


    //Set date method
    private void setDate(){
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String formattedDate = formatDate(selection);
                dateTxt.setText(formattedDate);
            }
        });
        materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTxt.setText("");
            }
        });

        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }


    //Format date
    private String formatDate(long milliseconds){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy",Locale.getDefault());
        return simpleDateFormat.format(new Date(milliseconds));
    }


    //Set time method
    private void setTime(){

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText("Set Time")
                .build();
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedHour = timePicker.getHour();
                int selectedMinute= timePicker.getMinute();

                String amPm = (selectedHour >= 12) ? "PM" : "AM";
                if (selectedHour > 12) {
                    selectedHour -= 12;
                } else if (selectedHour == 0) {
                    selectedHour = 12; // Convert midnight (0 hour) to 12-hour format
                }

                String formattedTime = String.format(Locale.getDefault(), "%d:%02d %s", selectedHour, selectedMinute, amPm);
                timeTxt.setText(formattedTime);
            }
        });
        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTxt.setText("");
            }
        });
        timePicker.show(getSupportFragmentManager(),"tag");
    }


    //Method to update confirmed date and time textViews
    public void updateConfirmation(View view){
        String selectedDate = dateTxt.getText().toString();
        String selectedTime = timeTxt.getText().toString();

        confirmDate.setText(selectedDate);
        confirmTime.setText(selectedTime);

    }


    //Notification channel
    private void createNotificationChannel(){
        CharSequence name = "ReminderChannel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("reminder_channel", name, importance);
        channel.setDescription("Reminder to re-check prep");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    // Parse the selected time string into a Calendar object
    private Calendar parseTime(String selectedTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            Date parsedDate = dateFormat.parse(selectedTime);
            if (parsedDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null; // Return null if parsing fails
    }


}