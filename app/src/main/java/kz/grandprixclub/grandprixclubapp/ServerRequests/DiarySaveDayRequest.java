package kz.grandprixclub.grandprixclubapp.ServerRequests;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiarySaveDayRequest extends StringRequest {
    private static final String DIARY_SAVE_DAY_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/diary/save/day";
    private Map<String, String> params;

    private final static String TAG = DiaryRequest.class.getSimpleName();

    public DiarySaveDayRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public DiarySaveDayRequest(String apiToken, int dayNumber, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DIARY_SAVE_DAY_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("day_number", dayNumber + "");
        Log.d(TAG, "day_number" + dayNumber);
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
