package kz.grandprixclub.grandprixclubapp.ServerRequests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class DiaryDateHistoryRequest extends StringRequest {

    private static final String DIARY_DATE_HISTOEY_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/diary/history";
    private Map<String, String> params;

    public DiaryDateHistoryRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public DiaryDateHistoryRequest(String apiToken, String date,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DIARY_DATE_HISTOEY_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("date", date);

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
