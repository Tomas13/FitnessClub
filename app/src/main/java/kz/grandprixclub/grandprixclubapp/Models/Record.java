package kz.grandprixclub.grandprixclubapp.Models;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Record {
    private String documentId;
    private String serviceId;
    private String employeeId;
    private String serviceName;
    private String date;
    private String beginTime;
    private String endTime;
    private int recordId;

    public Record(String documentId, String serviceId, String employeeId, String serviceName,
                  String date, String beginTime, String endTime, int recordId) {
        this.documentId = documentId;
        this.serviceId = serviceId;
        this.employeeId = employeeId;
        this.serviceName = serviceName;
        this.date = date;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.recordId = recordId;
    }

    public static Record[] getRecordsAsArray (JSONArray jRecords) throws JSONException {

        Record[] myRecords = new Record[jRecords.length()];
        for (int i = 0; i < jRecords.length(); i++) {
            JSONObject j = jRecords.optJSONObject(i);
            myRecords[i] = new Record(j.getString("document_id"), j.getString("service_id"),
                    j.getString("employee_id"), j.getString("service_name"), j.getString("date"),
                    j.getString("begin_time"), j.getString("end_time"), j.getInt("record_id"));
        }
        return myRecords;

    }

    public String getDocumentId() {
        return documentId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDate() {
        return date;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getRecordId() {
        return recordId;
    }
}
