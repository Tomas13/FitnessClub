package kz.grandprixclub.grandprixclubapp.ServerRequests;


import android.graphics.Bitmap;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UserEditRequest extends StringRequest {

    private static final String USER_EDIT_REQUEST_URL = "http://cabinet.grandprixclub.kz/api/user/edit";
    private Map<String, String> params;

    public UserEditRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public UserEditRequest(String apiToken, String name, String lastname, String birthday, String address, Bitmap bitmap,
                           Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, USER_EDIT_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("name", name);
        params.put("lastname", lastname);
        params.put("birthday", birthday);
        params.put("address", address);
        if (bitmap != null) {
            params.put("media", getStringImage(bitmap));
        }

    }

    public UserEditRequest(String apiToken, String name, String lastname, String birthday, String address, Bitmap bitmap, String password, String passwordConfirm,
                           Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, USER_EDIT_REQUEST_URL, listener, errorListener);
        params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("name", name);
        params.put("lastname", lastname);
        params.put("birthday", birthday);
        params.put("address", address);
        params.put("password", password);
        params.put("password_confirmation", passwordConfirm);
        if (bitmap != null) {
            params.put("media", getStringImage(bitmap));
        }

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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
