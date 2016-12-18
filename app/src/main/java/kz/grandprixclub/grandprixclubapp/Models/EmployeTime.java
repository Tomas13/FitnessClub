package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmployeTime {
    private String employeId, full_date, time, date;

    public EmployeTime(String employeId, String full_date, String time, String date) {
        this.employeId = employeId;
        this.full_date = full_date;
        this.time = time;
        this.date = date;
    }

    public static EmployeTime[] getEmployeTimeAsArray(String employeId, String date, JSONArray jTime)
            throws JSONException {
        EmployeTime[] employeTimes = new EmployeTime[jTime.length()];
        for (int i = 0; i < jTime.length(); i++) {
            JSONObject j = jTime.optJSONObject(i);
            employeTimes[i] = new EmployeTime(employeId, j.getString("full_date"), j.getString("time"), date);
        }
        return employeTimes;
    }

    public String getEmployeId() {
        return employeId;
    }

    public String getFull_date() {
        return full_date;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return time;
    }
}
