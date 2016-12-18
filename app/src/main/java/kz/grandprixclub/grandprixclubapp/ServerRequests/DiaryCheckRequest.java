package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiaryCheckRequest extends StringRequest {
    private static final String DIARY_CHECK_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/diary/check";
    private Map<String, String> params;

    public DiaryCheckRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public DiaryCheckRequest(String apiToken, int dayNumber, String approachId, String weight,
                             String repeat, String recovery, String strech,
                             Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DIARY_CHECK_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("day_number", dayNumber + "");
        params.put("approach_id", approachId);
        params.put("weight", weight);
        params.put("repeat", repeat);
        params.put("recovery", recovery);
        params.put("strech", strech);
        params.put("is_done", "1");

    }

    public DiaryCheckRequest(String apiToken, int dayNumber, String approachId,
                             Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DIARY_CHECK_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("day_number", dayNumber + "");
        params.put("approach_id", approachId);
        params.put("is_done", "0");
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
