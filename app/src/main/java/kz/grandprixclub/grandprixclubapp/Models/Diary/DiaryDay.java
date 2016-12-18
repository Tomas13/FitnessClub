package kz.grandprixclub.grandprixclubapp.Models.Diary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryDay {
    private String dayNumber;
    private String dayName;
    private boolean isActive;
    private boolean isDone;
    private DiaryExercise[] diaryExercises;


    public DiaryDay(String dayNumber, String dayName, boolean isActive,
                    boolean isDone, DiaryExercise[] diaryExercises) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.isActive = isActive;
        this.isDone = isDone;
        this.diaryExercises = diaryExercises;
    }

    public static DiaryDay[] getDiaryDaysAsArray(JSONArray jDiaryDays) throws JSONException {
        DiaryDay[] diaryDays = new DiaryDay[jDiaryDays.length()];

        for (int i = 0; i < jDiaryDays.length(); i++) {
            JSONObject jDay = jDiaryDays.optJSONObject(i);
            diaryDays[i] = new DiaryDay(jDay.getString("day_number"), jDay.getString("day_name"),
                    jDay.getBoolean("is_active"), jDay.getBoolean("is_done"),
                    DiaryExercise.getDiaryExercisesAsArray(jDay.optJSONArray("exercises")));
        }
        return diaryDays;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getDayName() {
        return dayName;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDone() {
        return isDone;
    }

    public DiaryExercise[] getDiaryExercises() {
        return diaryExercises;
    }
}
