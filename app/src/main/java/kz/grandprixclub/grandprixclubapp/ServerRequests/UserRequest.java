package kz.grandprixclub.grandprixclubapp.ServerRequests;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserRequest extends StringRequest {

    private static final String USER_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/user/info";
    private Map<String, String> params;

    public UserRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public UserRequest(String apiToken, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, USER_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);

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
