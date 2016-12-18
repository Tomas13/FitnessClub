package kz.grandprixclub.grandprixclubapp.ServerRequests;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiaryRequest extends StringRequest {
    private static final String DIARY_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/diary";
    private Map<String, String> params;

    private final static String TAG = DiaryRequest.class.getSimpleName();

    public DiaryRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public DiaryRequest(String apiToken, int dayNumber, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DIARY_REQUEST_URL, listener, errorListener);
        dayNumber = dayNumber + 1;
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("day_number", dayNumber + "");
        //Log.d(TAG, String.format("token = %s, day = %s", apiToken, dayNumber + ""));

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
