package kz.grandprixclub.grandprixclubapp.ServerRequests;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalendarEmployeCancelRequest extends StringRequest {
    private static final String CALENDAR_EMPLOYE_CANSEL_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/records/order/cancel";
    private Map<String, String> params;

    public CalendarEmployeCancelRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public CalendarEmployeCancelRequest(String apiToken,
                                       String documentId,
                                       String recordId,
                                       String employeeId,
                                       Response.Listener<String> listener,
                                       Response.ErrorListener errorListener) {
        super(Method.POST, CALENDAR_EMPLOYE_CANSEL_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("document_id", documentId);
        params.put("record_id", recordId);
        params.put("employee_id", employeeId);


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
