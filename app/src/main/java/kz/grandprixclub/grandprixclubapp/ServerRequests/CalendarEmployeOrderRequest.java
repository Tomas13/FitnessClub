package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalendarEmployeOrderRequest extends StringRequest {
    private static final String CALENDAR_EMPLOYE_ORDER_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/records/order";
    private Map<String, String> params;

    public CalendarEmployeOrderRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public CalendarEmployeOrderRequest(String apiToken,
                                       String serviceId,
                                       String employeeId,
                                       String orderDateTime,
                                       Response.Listener<String> listener,
                                       Response.ErrorListener errorListener) {
        super(Method.POST, CALENDAR_EMPLOYE_ORDER_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("employee_id", employeeId);
        params.put("service_id", serviceId);
        params.put("order_date_time", orderDateTime);
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
