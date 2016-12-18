package kz.grandprixclub.grandprixclubapp.Models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Detox {

    private String id;
    private String date;
    private Procedure[] procedures;


    public Detox(String detoxId, String date) {
        this.id = detoxId;
        this.date = date;
    }

    public Detox(String detoxId, String date, Procedure[] procedures) {
        this.id = detoxId;
        this.date = date;
        this.procedures = procedures;
    }

    public static Detox[] getDetoxesAsArray(JSONArray jDetoxes) throws JSONException {
        Detox[] detoxes = new Detox[jDetoxes.length()];

        for (int i = 0; i < jDetoxes.length(); i++) {
            JSONObject jDetox = jDetoxes.optJSONObject(i);
            String detoxId = jDetox.getString("detox_id");
            String date = jDetox.getString("date");
            detoxes[i] = new Detox(detoxId, date,
                    Procedure.getProceduresAsArray(jDetox.getJSONArray("procedures"), detoxId, date));

        }
        return detoxes;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public Procedure[] getProcedures() {
        return procedures;
    }
}
