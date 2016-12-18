package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Impedancemetry {

    private int impedancemetryId;
    private String date;
    private List<List<String>> info;
    private int infoCount;


    public Impedancemetry(int impedancemetryId, String date, List<List<String>> info, int infoCount) {
        this.impedancemetryId = impedancemetryId;
        this.date = date;
        this.info = info;
        this.infoCount = infoCount;
    }

    public static Impedancemetry[] getImpedancemetryAsArray (JSONArray j) throws JSONException {
        Impedancemetry[] impedancemetries = new Impedancemetry[j.length()];
        for (int i = 0; i < j.length(); i++) {
            JSONObject jImpedancemetry = j.optJSONObject(i);
            JSONObject jImpedancemetryInfo = jImpedancemetry.getJSONArray("info").optJSONObject(0);
            Iterator<String> iter = jImpedancemetryInfo.keys();

            List<List<String>> impedancemetrieyInfoList = new ArrayList<>();

            List<String> infoDateList = new ArrayList<>();
            infoDateList.add(0, "Дата");
            infoDateList.add(1, jImpedancemetry.getString("date"));
            impedancemetrieyInfoList.add(0, infoDateList);

            int k = 1;
            while (iter.hasNext()) {
                String key = iter.next();
                String value = jImpedancemetryInfo.getString(key);
                List<String> list = new ArrayList<>();
                list.add(0, key);
                list.add(1, value);
                impedancemetrieyInfoList.add(k, list);
                k++;
            }

            impedancemetries[i] = new Impedancemetry(jImpedancemetry.getInt("impedancemetry_id"), jImpedancemetry.getString("date"),
                    impedancemetrieyInfoList, k);

        }
        return impedancemetries;
    }

    public int getImpedancemetryId() {
        return impedancemetryId;
    }

    public String getDate() {
        return date;
    }

    public List<List<String>> getInfo() {
        return info;
    }

    public int getInfoCount() {
        return infoCount;
    }
}
