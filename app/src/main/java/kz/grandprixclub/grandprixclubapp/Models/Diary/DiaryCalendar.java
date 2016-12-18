package kz.grandprixclub.grandprixclubapp.Models.Diary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryCalendar {
    private int number;
    private String date;
    private Boolean activeMonth;
    private Boolean hasHistory;

    public DiaryCalendar(int number, String date, Boolean activeMonth, Boolean hasHistory) {
        this.number = number;
        this.date = date;
        this.activeMonth = activeMonth;
        this.hasHistory = hasHistory;
    }

    public DiaryCalendar(int number, String date, Boolean activeMonth) {
        this.number = number;
        this.date = date;
        this.activeMonth = activeMonth;
    }

    public static DiaryCalendar[][] getDiaryCalendarsAsArray(JSONObject jCal) throws JSONException {
        DiaryCalendar[][] diaryCalendars = new DiaryCalendar[jCal.length()][];

        for (int i = 0; i < jCal.length(); i++) {
            JSONObject jWeeks = jCal.optJSONObject(i + 1 + "");
            diaryCalendars[i] = new DiaryCalendar[7]; // 7 days in week
            for (int j = 0; j < 7; j++) {
                JSONObject jDays = jWeeks.optJSONObject(j + 1 + "");
                if (jDays.has("has_history")) {
                    diaryCalendars[i][j] = new DiaryCalendar(jDays.getInt("number"),
                            jDays.getString("date"),
                            jDays.getBoolean("active_month"),
                            jDays.getBoolean("has_history"));
                } else {
                    diaryCalendars[i][j] = new DiaryCalendar(jDays.getInt("number"),
                            jDays.getString("date"),
                            jDays.getBoolean("active_month"));
                }
            }
        }

        return diaryCalendars;
    }

    public int getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public Boolean getActiveMonth() {
        return activeMonth;
    }

    public Boolean getHasHistory() {
        return hasHistory;
    }
}
