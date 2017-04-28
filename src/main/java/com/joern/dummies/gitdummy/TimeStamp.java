package com.joern.dummies.gitdummy;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jsattler on 27.04.2017.
 */
public class TimeStamp {

    public static String stamp(){

        Date date = new Date();
        String dateFormat = "yyyy.MM.dd - HH:mm:ss";
        return stamp(date, dateFormat);
    }

    public static String stamp(Date date, String dateFormat){

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }
}
