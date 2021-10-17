package com.example.calendarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private mySQLLiteDBHandler dbHandler;

    private EditText editText;

    private CalendarView calendarView;

    private String selectedDate;

    private String[] currDate = {"Year", "Month", "Day"};
    private SQLiteDatabase sqLiteDatabase;

    public static final String SEND_DATE = "com.example.calendarapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);

        Button button = findViewById(R.id.buttonSave);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                selectedDate = Integer.toString(year) + Integer.toString(month) + Integer.toString(dayOfMonth);
                currDate[0] = Integer.toString(year);
                currDate[1] = Integer.toString(month);
                currDate[2] = Integer.toString(dayOfMonth);

                button.setEnabled(true);
            }
        });
    }

    public void OpenDate(View view) {
        Intent intent = new Intent(this, DayView.class);
        Bundle extras = new Bundle();

        extras.putString("YEAR", currDate[0]);
        extras.putString("MONTH", currDate[1]);
        extras.putString("DAY", currDate[2]);


        intent.putExtras(extras);
        startActivity(intent);
    }

    public void OpenStats(View view) {

        Intent intent = new Intent(this, Statistics.class);

        startActivity(intent);

    }
}