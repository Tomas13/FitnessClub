package kz.grandprixclub.grandprixclubapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kz.grandprixclub.grandprixclubapp.ServerRequests.LoginRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.RegisterRequest;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    TextView tvSigninLink;
    EditText etBirthday;
    EditText etPhone;
    EditText etName;
    EditText etLastname;
    Spinner spinner;
    Button bRegister;

    int selectedSex;
    String selectedBirthday;

    private Calendar myCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditTextBirthday();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        tvSigninLink = (TextView) findViewById(R.id.link_signin);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etName = (EditText) findViewById(R.id.etName);
        etLastname = (EditText) findViewById(R.id.etLastname);
        bRegister = (Button) findViewById(R.id.bRegister);
        spinner = (Spinner) findViewById(R.id.spinner_sex);

        tvSigninLink.setOnClickListener(this);
        etBirthday.setOnClickListener(this);
        bRegister.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegistrationActivity.this,
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateEditTextBirthday() {
        String myFormat = "dd.MM.yyyy";
        String serverFormat = "yyyy-MM-dd";
        Date birthday = myCalendar.getTime();

        etBirthday.setText(new SimpleDateFormat(myFormat, Locale.US).format(birthday));
        selectedBirthday = (String) new SimpleDateFormat(serverFormat, Locale.US).format(birthday);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_signin:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                break;
            case R.id.etBirthday:
                new DatePickerDialog(RegistrationActivity.this, onDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.bRegister:
                //Log.d(TAG, String.format("Phone: %s, Name: %s, Lastname: %s, Birdthday: %s, Sex: %s",
                //        etPhone.getText(), etName.getText(), etLastname.getText(), selectedBirthday, selectedSex + ""));
                doRegister(etPhone.getText().toString(), etName.getText().toString(), etLastname.getText().toString(), selectedBirthday, selectedSex);

                break;
        }
    }

    public void doRegister(String phone, String name, String lastname, String birthday, int sex) {
        if (phone == null
                || name == null
                || lastname == null
                || birthday == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setMessage("Неверно заполнены данные").setNegativeButton("Повторить", null).create().show();
            return;
        } else if (phone.length() < 1
                || name.length() < 1
                || lastname.length() < 1
                || birthday.length() < 1)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setMessage("Неверно заполнены данные").setNegativeButton("Повторить", null).create().show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.d(TAG, response.toString());
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (has_error) {
                        Log.d(TAG, response);
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setMessage("Неверно заполнены данные").setNegativeButton("Повторить", null).create().show();
                    } else {
                        RegistrationActivity.this.finish();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class).putExtra("send_sms", true));
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

                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        RegisterRequest registerRequest = new RegisterRequest(phone, name, lastname, birthday, sex, responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Регистрация");
        progressDialog.show();
        queue.add(registerRequest);
    }
}
