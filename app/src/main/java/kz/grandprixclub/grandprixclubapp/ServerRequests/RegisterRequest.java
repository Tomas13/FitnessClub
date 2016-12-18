package kz.grandprixclub.grandprixclubapp.ServerRequests;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/registration/full";
    private Map<String, String> params;

    public RegisterRequest (String phone, String name, String lastname, String birthday, int sex, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("phone", phone);
        params.put("name", name);
        params.put("lastname", lastname);
        params.put("birthday", birthday);
        params.put("sex", sex + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }
}
