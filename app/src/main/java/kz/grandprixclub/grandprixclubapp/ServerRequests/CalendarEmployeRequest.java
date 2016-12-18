package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalendarEmployeRequest extends StringRequest {
    private static final String CALENDAR_EMPLOYE_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/records/calendar";
    private Map<String, String> params;

    public CalendarEmployeRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public CalendarEmployeRequest(String apiToken, String serviceId, String employeeId, int weekNumber, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, CALENDAR_EMPLOYE_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("employee_id", employeeId);
        params.put("service_id", serviceId);
        params.put("week_number", weekNumber + "");


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
