package kz.grandprixclub.grandprixclubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kz.grandprixclub.grandprixclubapp.Models.CalendarEmploye;
import kz.grandprixclub.grandprixclubapp.Models.Employe;
import kz.grandprixclub.grandprixclubapp.Models.EmployeDateTime;
import kz.grandprixclub.grandprixclubapp.Models.EmployeTime;
import kz.grandprixclub.grandprixclubapp.Models.Service;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.CalendarEmployeOrderRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.EmployeesRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.RecordDayTimeRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.ServicesRequest;

public class RecordsDetailNewActivity extends AppCompatActivity {

    private final static String TAG = RecordsDetailNewActivity.class.getSimpleName();

    private int categoryId;
    Spinner spService, spEmployee, spDate, spTime;
    Button bRecord;
    private RequestQueue mRequestQueue;
    private Service[] services;
    private String selectedServiceId;
    private Employe[] employes;
    private EmployeDateTime[] employeDateTimes;
    private EmployeTime[] employeTimes;
    private EmployeTime employeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                categoryId = 4; //salon is default, but never happens i guess
            } else {
                categoryId = extras.getInt("category_id");
            }
        } else {
            categoryId = (int) savedInstanceState.getSerializable("category_id");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_detail_new);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Записаться");

        spService = (Spinner) findViewById(R.id.spinner_service);
        spEmployee = (Spinner) findViewById(R.id.spinner_employee);
        spDate = (Spinner) findViewById(R.id.spinner_date);
        spTime = (Spinner) findViewById(R.id.spinner_time);
        bRecord = (Button) findViewById(R.id.bRecord);
        bRecord.setVisibility(View.GONE);

        spService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerAdapter(spEmployee, new Object[0]);
                setSpinnerAdapter(spDate, new Object[0]);
                setSpinnerAdapter(spTime, new Object[0]);
                getEmployees(selectedServiceId = services[position].getService_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerAdapter(spDate, new Object[0]);
                setSpinnerAdapter(spTime, new Object[0]);
                getDate(employes[position].getEmployeeId());
                /*Log.d(TAG, "EMPLOYE ID: " + employes[position].getEmployeeId() + ", NAME: " +
                        employes[position].getFullname() + ", SERVICE ID: " + selectedServiceId);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EmployeDateTime employeDateTime = employeDateTimes[position];
                setSpinnerAdapter(spTime, new Object[0]);
                if (employeDateTime.isActive()) {

                    getTimes(employeDateTime.getEmployeId(), employeDateTime.getDate());
                } else {
                    Toast.makeText(RecordsDetailNewActivity.this, "В этот день нет записей", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                employeTime = employeTimes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordToEmployeCalendar();
            }
        });

        getServices();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getServices() {
        final ProgressDialog progressDialog = new ProgressDialog(RecordsDetailNewActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONArray jServices = jsonResponse.getJSONObject("result").getJSONArray("services");
                        if (jServices.length() > 0) {
                            services = Service.getServicesAsArray(jServices, false);
                            setSpinnerAdapter(spService, services);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

                progressDialog.dismiss();
                Toast.makeText(RecordsDetailNewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка услуг");
        progressDialog.show();
        ServicesRequest servicesRequest = new ServicesRequest(User.getSavedApiToken(RecordsDetailNewActivity.this), categoryId, responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(RecordsDetailNewActivity.this);
        mRequestQueue.add(servicesRequest);

    }

    private void getEmployees(String serviceId) {
        final ProgressDialog progressDialog = new ProgressDialog(RecordsDetailNewActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONArray jEmploye = jsonResponse.getJSONObject("result")
                                .getJSONObject("service")
                                .getJSONArray("employees");

                        if (jEmploye.length() > 0) {
                            employes = Employe.getEmployeesAsArray(jEmploye);
                            setSpinnerAdapter(spEmployee, employes);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

                progressDialog.dismiss();
                Toast.makeText(RecordsDetailNewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка мастеров");
        progressDialog.show();
        EmployeesRequest employeesRequest = new EmployeesRequest(User.getSavedApiToken(RecordsDetailNewActivity.this), serviceId, responseListener, responseErrorListener);
        mRequestQueue= Volley.newRequestQueue(RecordsDetailNewActivity.this);
        mRequestQueue.add(employeesRequest);
    }

    private void getDate(final String employeId) {
        final ProgressDialog progressDialog = new ProgressDialog(RecordsDetailNewActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONArray jDates = jsonResponse.getJSONObject("result").getJSONArray("days");
                        if (jDates.length() > 0) {
                            employeDateTimes = EmployeDateTime.getEmployeDateTimeAsArray(employeId, jDates);
                            setSpinnerAdapter(spDate, employeDateTimes);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                progressDialog.dismiss();
                Toast.makeText(RecordsDetailNewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка дат");
        progressDialog.show();
        RecordDayTimeRequest recordDayTimeRequest = new RecordDayTimeRequest(User.getSavedApiToken(RecordsDetailNewActivity.this),
                employeId, responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(RecordsDetailNewActivity.this);
        mRequestQueue.add(recordDayTimeRequest);

    }

    private void getTimes(final String employeId, final String date) {
        final ProgressDialog progressDialog = new ProgressDialog(RecordsDetailNewActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONArray jTimes = jsonResponse.getJSONObject("result").getJSONArray("times");
                        if (jTimes.length() > 0) {
                            employeTimes = EmployeTime.getEmployeTimeAsArray(employeId, date, jTimes);
                            setSpinnerAdapter(spTime, employeTimes);
                            bRecord.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

                progressDialog.dismiss();
                Toast.makeText(RecordsDetailNewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка дат");
        progressDialog.show();
        RecordDayTimeRequest recordDayTimeRequest = new RecordDayTimeRequest(User.getSavedApiToken(RecordsDetailNewActivity.this),
                employeId, selectedServiceId, date, responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(RecordsDetailNewActivity.this);
        mRequestQueue.add(recordDayTimeRequest);

    }

    public void setSpinnerAdapter(Spinner spinner, Object[] items) {
        if (items.length < 1) {
            bRecord.setVisibility(View.GONE);
        }
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(RecordsDetailNewActivity.this,
                R.layout.my_simple_spinner_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
        super.onDestroy();
    }

    private void setRecordToEmployeCalendar() {
        final ProgressDialog progressDialog = new ProgressDialog(RecordsDetailNewActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (has_error) {
                        Toast.makeText(RecordsDetailNewActivity.this, "Невозможно записать. Выберите другое время", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "need update");
                        Intent intent = new Intent();
                        intent.putExtra("is_update_needed", true);
                        intent.putExtra("action", "records");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

                progressDialog.dismiss();
                Toast.makeText(RecordsDetailNewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Запись на приём");
        progressDialog.show();

        CalendarEmployeOrderRequest calendarEmployeOrderRequest =
                new CalendarEmployeOrderRequest(User.getSavedApiToken(RecordsDetailNewActivity.this),
                        selectedServiceId, employeTime.getEmployeId(), employeTime.getFull_date(), responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(RecordsDetailNewActivity.this);
        mRequestQueue.add(calendarEmployeOrderRequest);
    }
}
