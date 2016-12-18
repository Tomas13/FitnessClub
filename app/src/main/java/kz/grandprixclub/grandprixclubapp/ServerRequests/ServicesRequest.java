package kz.grandprixclub.grandprixclubapp.ServerRequests;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ServicesRequest extends StringRequest {

    private static final String SERVICE_ALL_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/services/all";
    private static final String SERVICE_BY_CATEGORY_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/services";
    private Map<String, String> params;

    public ServicesRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public ServicesRequest(String apiToken, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, SERVICE_ALL_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);

    }

    public ServicesRequest(String apiToken, int category_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, SERVICE_BY_CATEGORY_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("category_id", category_id + "");

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
