package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RecordDayTimeRequest extends StringRequest {
    private static final String DAY_RECORDS_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/records/days";
    private static final String TIME_RECORDS_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/records/times";
    private Map<String, String> params;

    public RecordDayTimeRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public RecordDayTimeRequest(String apiToken, String employeeId,
                                Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, DAY_RECORDS_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("employee_id", employeeId);

    }

    public RecordDayTimeRequest(String apiToken, String employeeId, String serviceId, String date,
                                Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, TIME_RECORDS_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("employee_id", employeeId);
        params.put("service_id", serviceId);
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
