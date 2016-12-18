package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmployeDateTime {
    private String employeId, date, dateName;
    private Boolean isActive;

    public EmployeDateTime(String employeId, String date, String dateName, Boolean isActive) {
        this.employeId = employeId;
        this.date = date;
        this.dateName = dateName;
        this.isActive = isActive;
    }

    public static EmployeDateTime[] getEmployeDateTimeAsArray(String employeId, JSONArray j) throws JSONException {

        EmployeDateTime[] employeDateTimes = new EmployeDateTime[j.length()];

        for (int i = 0; i < j.length(); i++) {
            JSONObject jO = j.optJSONObject(i);
            employeDateTimes[i] = new EmployeDateTime(employeId, jO.getString("date"),
                    jO.getString("date_name"), jO.getBoolean("is_active"));
        }
        return employeDateTimes;
    }

    public String getEmployeId() {
        return employeId;
    }

    public String getDate() {
        return date;
    }

    public String getDateName() {
        return dateName;
    }

    public Boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return dateName;
    }
}
