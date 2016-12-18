package kz.grandprixclub.grandprixclubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import kz.grandprixclub.grandprixclubapp.ServerRequests.PasswordResetRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.RegisterRequest;

public class PasswordResetActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PasswordResetActivity.class.getSimpleName();

    EditText etPhone;
    Button bPasswordReset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        etPhone = (EditText) findViewById(R.id.etPhone);
        bPasswordReset = (Button) findViewById(R.id.bPasswordReset);

        bPasswordReset.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPasswordReset:
                doPasswordReset(etPhone.getText().toString());
                break;
        }
    }

    public void doPasswordReset(String phone) {
        final ProgressDialog progressDialog = new ProgressDialog(PasswordResetActivity.this);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.d(TAG, response.toString());
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (has_error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordResetActivity.this);
                        builder.setMessage("Неверно заполнены данные").setNegativeButton("Повторить", null).create().show();
                    } else {
                        PasswordResetActivity.this.finish();
                        startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class).putExtra("send_sms", true));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String message = "";
                if (error instanceof NetworkError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof ServerError) {
                    message = "Сервер не найден. Пожалуйста, повторите попытку позже!";
                } else if (error instanceof AuthFailureError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof ParseError) {
                    message = "Пожалуйста, повторите попытку позже!";
                } else if (error instanceof NoConnectionError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof TimeoutError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                }

                Toast.makeText(PasswordResetActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(phone, responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(PasswordResetActivity.this);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Восстановление пароля");
        progressDialog.show();
        queue.add(passwordResetRequest);
    }
}
