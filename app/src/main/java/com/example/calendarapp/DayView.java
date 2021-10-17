package com.example.calendarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DayView extends AppCompatActivity {

    private mySQLLiteDBHandler dbHandler;

    private EditText editText;

    private SQLiteDatabase sqLiteDatabase;

    private String selectedDate;

    private String year;

    private String month;

    private String day;

    private ScrollView scrlView;

    private LinearLayout ll;

    private ArrayList<EditText> etCollection = new ArrayList<EditText>();

    private ArrayList<Button> saveCollection = new ArrayList<Button>();

    private ArrayList<Button> removeCollection = new ArrayList<Button>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

        scrlView = findViewById(R.id.scrlView);

        ll = findViewById(R.id.l_list);

        String[] month_names = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        Bundle extras = getIntent().getExtras();
        year = extras.getString("YEAR");
        month = extras.getString("MONTH");
        day = extras.getString("DAY");

        String monthDisplay = month;

        selectedDate = year + month + day;

        int temp = Integer.parseInt(month);

        for(int i = 1; i <= 12; i++) {
            if(temp == i)
                monthDisplay = month_names[i];
        }

        TextView textView = findViewById(R.id.textView4);
        String message = year + " " + monthDisplay + " " + day;

        textView.setText(message);

        try {
            dbHandler = new mySQLLiteDBHandler(this, "DataBaseReminders", null, 3);
            sqLiteDatabase = dbHandler.getWritableDatabase();
            sqLiteDatabase.execSQL("CREATE TABLE RemindersV4 (ID INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT, Time TEXT, Event TEXT, Status INTEGER DEFAULT 0)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ReadDatabase();
    }

    public void AddReminder(View view) {

        LinearLayout ll_h1 = new LinearLayout(this);
        LinearLayout ll_h2 = new LinearLayout(this);
        LinearLayout ll_v1 = new LinearLayout(this);
        LinearLayout ll_v2 = new LinearLayout(this);

        ll_h1.setOrientation(ll_h1.HORIZONTAL);
        ll_h2.setOrientation(ll_h2.HORIZONTAL);
        ll_v1.setOrientation(ll_v1.VERTICAL);
        ll_v2.setOrientation(ll_v2.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(590, 110);

        EditText et = new EditText(this);
        EditText tm = new EditText(this);
        CheckBox chckbx = new CheckBox(this);
        Button b = new Button(this);
        Button b_1 = new Button(this);

        String query = "SELECT COUNT(*) FROM RemindersV4 WHERE Date=" + selectedDate;

        Integer nid = (int)DatabaseUtils.longForQuery(sqLiteDatabase, query, null);

        Drawable buttonDrawable = b.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, 0xFF6200EE);

        lp.setMargins(40, 0, 0, 0);
        et.setLayoutParams(lp);
        et.setId(nid*6+1);
        et.setHint("Event");

        lp = new LinearLayout.LayoutParams(150, 110);
        lp.setMargins(40, 0, 0, 0);
        tm.setLayoutParams(lp);
        tm.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        Integer maxLength = 5;
        tm.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        tm.setId(nid*6+2);
        tm.setText("08:00");

        lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lp.setMargins(110, 0, 30, 0);

        b.setLayoutParams(lp);
        b.setBackground(buttonDrawable);
        b.setTextColor(getResources().getColor(R.color.white));
        b.setTextSize(12);
        b.setId(nid*6+3);
        b.setText("Save");

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDatabase(v, v.getId());
            }
        });

        b_1.setLayoutParams(lp);
        b_1.setBackground(buttonDrawable);
        b_1.setTextColor(getResources().getColor(R.color.white));
        b_1.setTextSize(12);
        b_1.setId(nid*6+4);
        b_1.setText("Remove");

        b_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRow(v, v.getId());
            }
        });

        chckbx.setText("");
        chckbx.setId(nid*6+6);
        chckbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ChangeStatus(isChecked, buttonView.getId());
            }
        });

        ll_v1.addView(et);
        ll_v1.addView(tm);

        ll_h2.addView(ll_v1);
        ll_h2.addView(chckbx);

        ll_h1.addView(ll_h2);

        ll_v2.addView(b);
        ll_v2.addView(b_1);

        ll_h1.addView(ll_v2);

        GradientDrawable border = new GradientDrawable();

        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, 0xFF000000); //black border with full opacity

        ll_h1.setBackground(border);

        ll_h1.setId(nid*6+5);

        ll.addView(ll_h1);

    }

    public void ChangeStatus(boolean isChecked, Integer Idval) {

        Integer status = 0;

        EditText event = findViewById(Idval-5);

        if(isChecked) {
            status = 1;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put("Status", status);

        Log.wtf("Status value:", status.toString());
        Log.wtf("Event value:", event.toString());

        String WhereQuery = "Event='" + event.getText().toString() + "' AND Date='" + selectedDate + "'";

        sqLiteDatabase.update("RemindersV4", contentValues, WhereQuery, null);
    }

    public void InsertDatabase(View view, int temp) {

        EditText event = findViewById(temp-2);
        EditText time = findViewById(temp-1);
        Button save = findViewById(temp);
        Button remove = findViewById(temp+1);
        LinearLayout ll_h = findViewById(temp+2);

        ContentValues contentValues = new ContentValues();

        contentValues.put("Date", selectedDate);
        contentValues.put("Time", time.getText().toString());
        contentValues.put("Event", event.getText().toString());

        sqLiteDatabase.insert("RemindersV4", null, contentValues);

        String theSeparator = ":";
        String[] SeparatedTime = time.getText().toString().split(Pattern.quote(theSeparator));
        Integer hr = Integer.parseInt(SeparatedTime[0]);
        Integer mn = Integer.parseInt(SeparatedTime[1]);

        Intent intent = new Intent(this, ReminderBroadcast.class);

        String query = "SELECT ID FROM RemindersV4 WHERE Date =" + selectedDate + " AND Event ='" + event.getText().toString() + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();

        Log.wtf("InsertData ID value", Integer.toString(cursor.getInt(cursor.getColumnIndex("ID"))));

        intent.putExtra(ReminderBroadcast.NOTIFICATION_ID, cursor.getInt(cursor.getColumnIndex("ID")));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default") ;
        builder.setContentTitle("Calendar Reminder") ;
        builder.setContentText(event.getText().toString());
        builder.setSmallIcon(R.drawable.ic_launcher_foreground );
        builder.setAutoCancel(true);
        builder.setChannelId("10001");
        Notification notification = builder.build();

        intent.putExtra(ReminderBroadcast.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocalDateTime currTime = LocalDateTime.now().minusHours(12);
        LocalDateTime alertTime = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month) + 1, Integer.parseInt(day), hr, mn);
        Duration difference = Duration.between(currTime, alertTime);
        long diffMillis = difference.toMillis();

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + diffMillis, pendingIntent);
    }

    public void DeleteRow(View view, int temp) {

        EditText event = findViewById(temp-3);
        LinearLayout ll_h = findViewById(temp+1);

        String query = "Date='" + selectedDate + "' AND Event='" + event.getText().toString() + "'";

        sqLiteDatabase.delete("RemindersV4", query, null);

        ll.removeView(ll_h);
    }

    public void ReadDatabase() {

        String query = "SELECT Event, Time, Status FROM RemindersV4 WHERE Date =" + selectedDate;

        try {
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {

                AddReminder(i, cursor.getString(cursor.getColumnIndex("Event")), cursor.getString(cursor.getColumnIndex("Time")), cursor.getInt(cursor.getColumnIndex("Status")));
                Integer statval = cursor.getInt(cursor.getColumnIndex("Status"));
                cursor.moveToNext();
            }

            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AddReminder(Integer RowNumber, String event_txt, String time_txt, Integer status) {

        LinearLayout ll_h1 = new LinearLayout(this);
        LinearLayout ll_h2 = new LinearLayout(this);
        LinearLayout ll_v1 = new LinearLayout(this);
        LinearLayout ll_v2 = new LinearLayout(this);

        ll_h1.setOrientation(ll_h1.HORIZONTAL);
        ll_h2.setOrientation(ll_h2.HORIZONTAL);
        ll_v1.setOrientation(ll_v1.VERTICAL);
        ll_v2.setOrientation(ll_v2.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(590, 110);

        EditText et = new EditText(this);
        EditText tm = new EditText(this);
        CheckBox chckbx = new CheckBox(this);
        Button b = new Button(this);
        Button b_1 = new Button(this);

        Drawable buttonDrawable = b.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, 0xFF6200EE);

        lp.setMargins(40, 0, 0, 0);
        et.setLayoutParams(lp);
        et.setId(RowNumber*6+1);
        et.setText(event_txt);

        lp = new LinearLayout.LayoutParams(150, 110);
        lp.setMargins(40, 0, 0, 0);
        tm.setLayoutParams(lp);
        tm.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        Integer maxLength = 5;
        tm.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        tm.setId(RowNumber*6+2);
        tm.setText(time_txt);

        lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lp.setMargins(110, 0, 30, 0);

        b.setLayoutParams(lp);
        b.setBackground(buttonDrawable);
        b.setTextColor(getResources().getColor(R.color.white));
        b.setTextSize(12);
        b.setId(RowNumber*6+3);
        b.setText("Save");

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDatabase(v, v.getId());
            }
        });

        b_1.setLayoutParams(lp);
        b_1.setBackground(buttonDrawable);
        b_1.setTextColor(getResources().getColor(R.color.white));
        b_1.setTextSize(12);
        b_1.setId(RowNumber*6+4);
        b_1.setText("Remove");

        b_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRow(v, v.getId());
            }
        });

        chckbx.setText("");
        chckbx.setId(RowNumber*6+6);

        if(status == 1) {
            chckbx.setChecked(true);
        }

        chckbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ChangeStatus(isChecked, buttonView.getId());
            }
        });

        ll_v1.addView(et);
        ll_v1.addView(tm);

        ll_h2.addView(ll_v1);
        ll_h2.addView(chckbx);

        ll_h1.addView(ll_h2);

        ll_v2.addView(b);
        ll_v2.addView(b_1);

        ll_h1.addView(ll_v2);

        GradientDrawable border = new GradientDrawable();

        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, 0xFF000000); //black border with full opacity

        ll_h1.setBackground(border);

        ll_h1.setId(RowNumber*6+5);

        ll.addView(ll_h1);
    }
}