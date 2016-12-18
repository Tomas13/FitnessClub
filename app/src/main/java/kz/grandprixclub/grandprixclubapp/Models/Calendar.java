package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Calendar {
    private String startDate;
    private String endDate;
    private String monthName;
    private String number;
    private String date;
    private Service[] services;

    public Calendar(String startDate, String endDate, String monthName, String number, String date, Service[] services) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthName = monthName;
        this.number = number;
        this.date = date;
        this.services = services;
    }

    public Calendar(String startDate, String endDate, String monthName, String number, String date) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthName = monthName;
        this.number = number;
        this.date = date;
    }

    public static Calendar[][] getCalendarAsArray(JSONObject jCalendarRows,
                                          String startDate, String endDate, String monthName) throws JSONException {
        Calendar[][] calendars = new Calendar[jCalendarRows.length() + 1][];

        Iterator<String> iterator = jCalendarRows.keys();
        while (iterator.hasNext()) {
            Integer row = Integer.parseInt(iterator.next());
            JSONObject jsonColumn = jCalendarRows.optJSONObject(row.toString());
            calendars[row] = new Calendar[8];
            for (Integer col = 1; col < 8; col++) {
                if (jsonColumn.optJSONObject(col.toString()).has("services")) {
                    calendars[row][col] = new Calendar(startDate, endDate, monthName, jsonColumn.optJSONObject(col.toString()).getString("number"),
                            jsonColumn.optJSONObject(col.toString()).getString("date"),
                            Service.getServicesAsArray(jsonColumn.optJSONObject(col.toString()).optJSONArray("services")));
                } else {
                    calendars[row][col] = new Calendar(startDate, endDate, monthName, jsonColumn.optJSONObject(col.toString()).getString("number"),
                            jsonColumn.optJSONObject(col.toString()).getString("date"));;
                }


            }

        }

        return calendars;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getMonthName() {
        return monthName;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public Service[] getServices() {
        return services;
    }
}
