package kz.grandprixclub.grandprixclubapp.ServerRequests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class DiaryCalendarHistoryRequest extends StringRequest {

    private static final String DIARY_CALENDAR_HISTOEY_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/diary/history/calendar";
    private Map<String, String> params;

    public DiaryCalendarHistoryRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public DiaryCalendarHistoryRequest(String apiToken, int year, int month,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DIARY_CALENDAR_HISTOEY_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("year", year + "");
        params.put("month", month + "");

    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }
}
