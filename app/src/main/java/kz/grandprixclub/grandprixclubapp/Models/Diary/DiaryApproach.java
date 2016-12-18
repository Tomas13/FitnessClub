package kz.grandprixclub.grandprixclubapp.Models.Diary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryApproach {

    public static final String COLUMN_1="1";
    public static final String COLUMN_2="2";
    public static final String COLUMN_3="3";
    public static final String COLUMN_4="4";
    public static final String COLUMN_5="5";
    public static final String COLUMN_6="6";

    private String approachNumber;
    private String approachId;
    private String weight;
    private String repeat;
    private String recovery;
    private String strech;
    private boolean isMaded;


    public DiaryApproach(String approachNumber, String approachId,
                         String weight, String repeat, String recovery,
                         String strech, boolean isMaded) {
        this.approachNumber = approachNumber;
        this.approachId = approachId;
        this.weight = weight;
        this.repeat = repeat;
        this.recovery = recovery;
        this.strech = strech;
        this.isMaded = isMaded;
    }

    public static DiaryApproach[] getApproachesAsArray(JSONArray jAppr) throws JSONException {
        if (jAppr != null) {
            DiaryApproach[] diaryApproaches = new DiaryApproach[jAppr.length()];

            for (int i = 0; i < jAppr.length(); i++) {
                JSONObject j = jAppr.optJSONObject(i);
                diaryApproaches[i] = new DiaryApproach(j.getString("approach_number"), j.getString("approach_id"),
                        j.getString("weight"), j.getString("repeat"), j.getString("recovery"), j.getString("strech"),
                        j.getBoolean("is_maded"));
            }
            return diaryApproaches;
        } else {
            return null;
        }

    }

    public String getApproachNumber() {
        return approachNumber;
    }

    public String getApproachId() {
        return approachId;
    }

    public String getWeight() {
        return weight;
    }

    public String getRepeat() {
        return repeat;
    }

    public String getRecovery() {
        return recovery;
    }

    public String getStrech() {
        return strech;
    }

    public boolean isMaded() {
        return isMaded;
    }
}
