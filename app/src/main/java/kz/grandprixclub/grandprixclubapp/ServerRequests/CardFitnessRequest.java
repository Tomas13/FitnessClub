package kz.grandprixclub.grandprixclubapp.ServerRequests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CardFitnessRequest extends StringRequest {
    private static final String CARD_FITNESS_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/card/fitness";
    private Map<String, String> params;

    public CardFitnessRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public CardFitnessRequest(String apiToken, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, CARD_FITNESS_REQUEST_URL, listener, errorListener);
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
