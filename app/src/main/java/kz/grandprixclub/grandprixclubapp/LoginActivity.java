package kz.grandprixclub.grandprixclubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.LoginRequest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLogin;
    EditText etPhone;
    EditText etPassword;
    TextView tvSignupLink;
    TextView tvPasswordResetLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Boolean sendSms = extras.getBoolean("send_sms");
            if (sendSms) {
                Toast.makeText(LoginActivity.this, "Пароль выслан по SMS", Toast.LENGTH_LONG).show();
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = (EditText) findViewById(R.id.etPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        tvSignupLink = (TextView) findViewById(R.id.link_signup);
        tvPasswordResetLink = (TextView) findViewById(R.id.link_pass_reset);

        bLogin.setOnClickListener(this);
        tvSignupLink.setOnClickListener(this);
        tvPasswordResetLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogin:
                final String phone = etPhone.getText().toString();
                final String password = etPassword.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //Log.d("LOGIN", response.toString());
                            boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                            if (has_error) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Неверный логин или пароль").setNegativeButton("Повторить", null).create().show();
                            } else {
                                JSONObject JUser = jsonResponse.getJSONObject("result").getJSONObject("user");
                                User user = new User(JUser);
                                user.saveApiToken(LoginActivity.this, user.getApiToken());
                                LoginActivity.this.finish();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                };

                LoginRequest loginRequest = new LoginRequest(phone, password, responseListener, responseErrorListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Авторизация");
                progressDialog.show();
                queue.add(loginRequest);

                break;

            case R.id.link_signup:
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                break;

            case R.id.link_pass_reset:
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
                break;
        }
    }
}
