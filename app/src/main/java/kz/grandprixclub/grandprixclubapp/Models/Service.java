package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;

public class Service {
    private String time;

    private String name;

    private String date;

    private String category_name;

    private String service_id;

    private int rate_time;

    private int category_id;

    public Service(String category_name, String time) {
        this.category_name = category_name;
        this.time = time;
    }

    public Service(String time, String name, String date) {
        this.time = time;
        this.name = name;
        this.date = date;
    }

    public Service (String category_name, String service_id, String name, int rate_time) {
        this.category_name = category_name;
        this.service_id = service_id;
        this.name = name;
        this.rate_time = rate_time;
    }

    public static Service[] getServicesAsArray(JSONArray j) throws JSONException {
        Service[] services = new Service[j.length()];
        for (int i = 0; i < j.length(); i++) {
            services[i] = new Service(j.optJSONObject(i).getString("category"), j.optJSONObject(i).getString("time"));
        }
        return services;

    }

    public static Service[] getServicesAsArray(JSONArray jSerices, boolean isMyServices) throws JSONException {
        Service[] services = new Service[jSerices.length()];

        for (int i = 0; i < jSerices.length(); i++) {
            if (isMyServices) {
                services[i] = new Service(jSerices.optJSONObject(i).getString("time"),
                        jSerices.optJSONObject(i).getString("name"),
                        jSerices.optJSONObject(i).getString("date"));
            } else {
                services[i] = new Service(jSerices.optJSONObject(i).getString("category_name"),
                        jSerices.optJSONObject(i).getString("service_id"),
                        jSerices.optJSONObject(i).getString("name"),
                        jSerices.optJSONObject(i).getInt("rate_time"));
            }
        }

        return services;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public int getRate_time() {
        return rate_time;
    }

    public void setRate_time(int rate_time) {
        this.rate_time = rate_time;
    }

    public int getCategory_id() {
        return category_id;
    }


    @Override
    public String toString() {
        return name;
    }
}
