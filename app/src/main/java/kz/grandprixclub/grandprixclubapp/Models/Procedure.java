package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Procedure {

    private int id;
    private String procedureTime;
    private String procedureName;
    private String cabinet;
    private String physicianName;
    private String detoxId;
    private String date;
    private String comment;

    public Procedure(int id, String procedureTime, String procedureName, String cabinet,
                     String physicianName, String detoxId, String date, String comment) {
        this.id = id;
        this.procedureTime = procedureTime;
        this.procedureName = procedureName;
        this.cabinet = cabinet;
        this.physicianName = physicianName;
        this.detoxId = detoxId;
        this.date = date;
        this.comment = comment;
    }

    public static Procedure[] getProceduresAsArray(JSONArray jProcedures, String detoxId, String date) throws JSONException {
        Procedure[] procedures = new Procedure[jProcedures.length()];

        for (int i = 0; i < jProcedures.length(); i++) {
            JSONObject jProceure = jProcedures.optJSONObject(i);
            procedures[i] = new Procedure(i, jProceure.getString("procedure_time"), jProceure.getString("procedure_name"),
                    jProceure.getString("cabinet"), jProceure.getString("physician_name"), detoxId, date,
                    jProceure.getString("comment"));
        }

        return procedures;
    }




    public String getProcedureTime() {
        return procedureTime;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public String getCabinet() {
        return cabinet;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public String getDetoxId() {
        return detoxId;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }


    public int getId() {
        return id;
    }
}
