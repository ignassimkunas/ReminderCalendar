@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics1);

        PieChartView pieChartView = findViewById(R.id.chart);

        List<SliceValue> pieData = new ArrayList<>();

        currDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)) + Integer.toString(Calendar.getInstance().get(Calendar.MONTH)) + Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        try {
            dbHandler = new mySQLLiteDBHandler(this, "DataBaseReminders", null, 3);
            sqLiteDatabase = dbHandler.getWritableDatabase();
            sqLiteDatabase.execSQL("CREATE TABLE RemindersV4 (ID INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT, Time TEXT, Event TEXT, Status INTEGER DEFAULT 0)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String query = "SELECT Date, Status FROM RemindersV4";

        String gotDate;
        Integer ReminderStatus;

        Integer CountOnTime = 0;
        Integer CountPending = 0;
        Integer CountNotOnTime = 0;

        
        try {
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {

                gotDate = cursor.getString(cursor.getColumnIndex("Date"));
                ReminderStatus = cursor.getInt(cursor.getColumnIndex("Status"));
                Log.wtf("Got Date", gotDate);
                Log.wtf("Current Date", currDate);
                Log.wtf("Got Status", Integer.toString(ReminderStatus));

                if(Integer.parseInt(gotDate) >= Integer.parseInt(currDate) && ReminderStatus == 1) {
                    CountOnTime += 1;
                    Log.wtf("Status", "Saved as on time");
                }
                else if(Integer.parseInt(gotDate) < Integer.parseInt(currDate) && ReminderStatus == 1) {
                    CountOnTime += 1;
                    Log.wtf("Status", "Saved as on time, but older date");
                }
                else if(Integer.parseInt(gotDate) < Integer.parseInt(currDate) && ReminderStatus == 0) {
                    CountNotOnTime += 1;
                    Log.wtf("Status", "Saved as not on time");
                }
                else {
                    CountPending += 1;
                    Log.wtf("Status", "Saved as pending");
                }
                cursor.moveToNext();
            }

            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        pieData.add(new SliceValue(CountOnTime, Color.GREEN).setLabel("Done on time"));
        pieData.add(new SliceValue(CountNotOnTime, Color.RED).setLabel("Late"));
        pieData.add(new SliceValue(CountPending, Color.GRAY).setLabel("Pending"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Reminder Statistics").setCenterText1FontSize(20);
        pieChartView.setPieChartData(pieChartData);

    }