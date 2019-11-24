package com.example.pinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime {
    String getdatetime() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");

// Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
        String reportDate = df.format(today);
        return reportDate;
    }

}
