package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CalendarEmploye {
    private String date;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private boolean no_order;
    private boolean ordered;
    private boolean is_mine;
    private String id_document;
    private String id_record;
    private boolean is_past;
    private String employeId;


    public CalendarEmploye(String date, String year, String month, String day,
                           String hour, String minute, boolean no_order, boolean ordered,
                           boolean is_mine, String id_document, String id_record, boolean is_past, String employeId) {
        this.date = date;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.no_order = no_order;
        this.ordered = ordered;
        this.is_mine = is_mine;
        this.id_document = id_document;
        this.id_record = id_record;
        this.is_past = is_past;
        this.employeId = employeId;
    }

    public static CalendarEmploye[][] getCalendarEmployeAsArray(JSONObject jsonRows, String employeId) throws JSONException {
        CalendarEmploye[][] calendarEmployes = new CalendarEmploye[57][];

        Iterator<String> iterator = jsonRows.keys();
        while (iterator.hasNext()) {
            Integer row = Integer.parseInt(iterator.next());
            JSONObject jsonColumn = jsonRows.optJSONObject(row.toString());
            calendarEmployes[row] = new CalendarEmploye[8];

            for (Integer col = 1; col < 8; col++) {
                calendarEmployes[row][col] = new CalendarEmploye(jsonColumn.optJSONObject(col.toString()).getString("date"),
                        jsonColumn.optJSONObject(col.toString()).getString("year"), jsonColumn.optJSONObject(col.toString()).getString("month"),
                        jsonColumn.optJSONObject(col.toString()).getString("day"), jsonColumn.optJSONObject(col.toString()).getString("hour"),
                        jsonColumn.optJSONObject(col.toString()).getString("minute"), jsonColumn.optJSONObject(col.toString()).getBoolean("no_order"),
                        jsonColumn.optJSONObject(col.toString()).getBoolean("ordered"), jsonColumn.optJSONObject(col.toString()).getBoolean("is_mine"),
                        jsonColumn.optJSONObject(col.toString()).getString("id_document"), jsonColumn.optJSONObject(col.toString()).getString("id_record"),
                        jsonColumn.optJSONObject(col.toString()).getBoolean("is_past"), employeId);
            }

        }

        return calendarEmployes;
    }

    public String getDate() {
        return date;
    }

    public String getYear() {
        return year;
    }


    public boolean isNo_order() {
        return no_order;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public boolean is_mine() {
        return is_mine;
    }

    public String getId_document() {
        return id_document;
    }


    public boolean is_past() {
        return is_past;
    }

    public String getId_record() {
        return id_record;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getEmployeId() {
        return employeId;
    }
}
