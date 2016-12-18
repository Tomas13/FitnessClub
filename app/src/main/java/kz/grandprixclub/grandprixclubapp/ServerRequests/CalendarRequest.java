package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalendarRequest extends StringRequest {
    private static final String CALENDAR_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/calendar";
    private Map<String, String> params;

    public CalendarRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public CalendarRequest(String apiToken, String year, String month, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, CALENDAR_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("year", year);
        params.put("month", month);

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
