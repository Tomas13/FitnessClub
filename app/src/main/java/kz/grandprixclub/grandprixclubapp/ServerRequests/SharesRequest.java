package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class SharesRequest extends StringRequest {

    private static final String SHARES_REQUEST_URL = "http://grandprixclub.kz/api/actions";
    private Map<String, String> params;

    public SharesRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public SharesRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, SHARES_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();

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
