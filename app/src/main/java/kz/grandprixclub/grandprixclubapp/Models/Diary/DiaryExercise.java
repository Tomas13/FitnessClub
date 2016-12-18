package kz.grandprixclub.grandprixclubapp.Models.Diary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryExercise {
    private String exerciseNumber;
    private String trainerId;
    private String trainerName;
    private String exerciseid;
    private String exerciseName;
    private DiaryApproach[] diaryApproaches;


    public DiaryExercise(String exerciseNumber, String trainerId,
                         String trainerName, String exerciseid, String exerciseName, DiaryApproach[] diaryApproaches) {
        this.exerciseNumber = exerciseNumber;
        this.trainerId = trainerId;
        this.trainerName = trainerName;
        this.exerciseid = exerciseid;
        this.exerciseName = exerciseName;
        this.diaryApproaches = diaryApproaches;
    }

    public static DiaryExercise[] getDiaryExercisesAsArray(JSONArray jExercises) throws JSONException {
        if (jExercises != null) {
            DiaryExercise[] diaryExercises = new DiaryExercise[jExercises.length()];

            for (int i = 0; i < jExercises.length(); i++) {
                JSONObject j = jExercises.optJSONObject(i);

                diaryExercises[i] = new DiaryExercise(j.getString("exercise_number"), j.getString("trainer_id"),
                        j.getString("trainer_name"), j.getString("exercise_id"), j.getString("exercise_name"),
                        DiaryApproach.getApproachesAsArray(j.optJSONArray("approaches")));
            }
            return diaryExercises;
        } else {
            return null;
        }
    }

    public String getExerciseNumber() {
        return exerciseNumber;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getExerciseid() {
        return exerciseid;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public DiaryApproach[] getDiaryApproaches() {
        return diaryApproaches;
    }
}
