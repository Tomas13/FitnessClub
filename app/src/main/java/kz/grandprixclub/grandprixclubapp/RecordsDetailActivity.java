package kz.grandprixclub.grandprixclubapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kz.grandprixclub.grandprixclubapp.Models.CalendarEmploye;
import kz.grandprixclub.grandprixclubapp.Models.Employe;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.CalendarEmployeCancelRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.CalendarEmployeOrderRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.CalendarEmployeRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.EmployeesRequest;

public class RecordsDetailActivity extends AppCompatActivity {

    String service_id;
    Spinner spinner;
    Employe[] employes;

    TableLayout tlCalendarEmploye;

    View.OnClickListener onClickListener;

    CalendarEmploye[][] calendarEmployes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                service_id = null;
            } else {
                service_id = extras.getString("service_id");
            }
        } else {
            service_id = (String) savedInstanceState.getSerializable("service_id");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tlCalendarEmploye = (TableLayout) findViewById(R.id.tl_calendar_employe);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt(getString(R.string.employe_name));
        getEmployees();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCalendarEmploye(employes[position].getEmployeeId(), 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarEmploye calendar = getCalendarEmployeObjById(v.getId());
                if (calendar.is_mine()) {
                    if (!calendar.is_past()) {
                        removeRecordFromEmployeCalendar(calendar);
                    }
                } else {
                    setRecordToEmployeCalendar(getCalendarEmployeObjById(v.getId()));
                }
                //aaasss
            }
        };

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void removeRecordFromEmployeCalendar(final CalendarEmploye calendar) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (has_error) {
                        //Toast.makeText(RecordsDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    } else {
                        getCalendarEmploye(calendar.getEmployeId(), 0);
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

                Toast.makeText(RecordsDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        CalendarEmployeCancelRequest calendarEmployeCancelRequest =
                new CalendarEmployeCancelRequest(User.getSavedApiToken(RecordsDetailActivity.this),
                        calendar.getId_document(), calendar.getId_record(), calendar.getEmployeId(),
                        responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(RecordsDetailActivity.this);
        queue.add(calendarEmployeCancelRequest);
    }

    private void setRecordToEmployeCalendar(final CalendarEmploye calendar) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (has_error) {
                        //Toast.makeText(RecordsDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    } else {
                        getCalendarEmploye(calendar.getEmployeId(), 0);
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

                Toast.makeText(RecordsDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        CalendarEmployeOrderRequest calendarEmployeOrderRequest =
                new CalendarEmployeOrderRequest(User.getSavedApiToken(RecordsDetailActivity.this),
                        service_id, calendar.getEmployeId(), calendar.getDate(), responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(RecordsDetailActivity.this);
        queue.add(calendarEmployeOrderRequest);
    }

    private CalendarEmploye getCalendarEmployeObjById(int id) {
        return calendarEmployes[id / 10][id % 10];
    }

    private void tryToCleanTableLayout() {
        int k = tlCalendarEmploye.getChildCount();
        while (k > 2) {
            TableRow row = (TableRow)tlCalendarEmploye.getChildAt(2); // integer k equals 2 because we have 2 header rows and we do not need to remove them
            tlCalendarEmploye.removeView(row);
            k = tlCalendarEmploye.getChildCount();
        }
    }

    private void addEmployeCalendar() {

        tryToCleanTableLayout();

        for (Integer i = 1; i < calendarEmployes.length; i++) {
            TableRow tableRow = new TableRow(RecordsDetailActivity.this);
            for (Integer j = 1; j < 8; j++) {

                if (calendarEmployes != null &&
                        calendarEmployes[i] != null &&
                        calendarEmployes[i][j] != null &&
                        calendarEmployes[i][j].getDate().length() > 0) {
                    CalendarEmploye calenEmpl = calendarEmployes[i][j];

                    if (j == 1) {
                        TextView textView1 = new TextView(this);
                        textView1.setBackgroundResource(R.drawable.cell_shape_white);
                        textView1.setTextColor(Color.BLACK);
                        textView1.setGravity(Gravity.CENTER);
                        textView1.setText(calenEmpl.getHour() + "." + calenEmpl.getMinute());
                        tableRow.addView(textView1);
                    }

                    TextView textView = new TextView(this);

                    if (calenEmpl.isOrdered()) {
                        if (calenEmpl.is_mine()) {
                            textView.setBackgroundResource(R.drawable.cell_my_shape);
                            if (!calenEmpl.is_past()) {
                                //User can cancel record
                                textView.setClickable(true);
                                textView.setId(Integer.parseInt(i.toString() + "" + j.toString()));
                                textView.setOnClickListener(onClickListener);
                            }
                        } else {
                            textView.setBackgroundResource(R.drawable.cell_busy_shape);
                        }
                    } else if (calenEmpl.isNo_order() || calenEmpl.is_past()) {
                        textView.setBackgroundResource(R.drawable.cell_no_order_shape);
                    } else {
                        textView.setBackgroundResource(R.drawable.cell_vacant_shape);
                        textView.setClickable(true);
                        textView.setId(Integer.parseInt(i.toString() + "" + j.toString()));
                        textView.setOnClickListener(onClickListener);

                    }

                    tableRow.addView(textView);
                }

            }
            if (tlCalendarEmploye != null) {
                tlCalendarEmploye.addView(tableRow, i + 1);
            }
        }
    }

    private void getCalendarEmploye(final String employeId, int weekNumber) {

        tryToCleanTableLayout();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONObject jCalendarEmpl = null;
                        try {
                            jCalendarEmpl = jsonResponse.getJSONObject("result").getJSONObject("records");
                        } catch (JSONException i) {}



                        if (jCalendarEmpl != null && jCalendarEmpl.length() > 0) {
                            calendarEmployes = CalendarEmploye.getCalendarEmployeAsArray(jCalendarEmpl, employeId);
                            addEmployeCalendar();
                            //Log.d("ASDQWE", calendarEmployes[10][2].getId_document());
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

                Toast.makeText(RecordsDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        CalendarEmployeRequest calendarEmployeRequest = new CalendarEmployeRequest(User.getSavedApiToken(RecordsDetailActivity.this),
                service_id, employeId, weekNumber, responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(RecordsDetailActivity.this);
        queue.add(calendarEmployeRequest);
    }

    private void getEmployees() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONArray jEmploye = jsonResponse.getJSONObject("result")
                                .getJSONObject("service")
                                .getJSONArray("employees");

                        setAdapter(employes = Employe.getEmployeesAsArray(jEmploye));
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

                Toast.makeText(RecordsDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        EmployeesRequest employeesRequest = new EmployeesRequest(User.getSavedApiToken(RecordsDetailActivity.this), service_id, responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(RecordsDetailActivity.this);
        queue.add(employeesRequest);
    }

    private void setAdapter(Employe[] employes) {
        ArrayAdapter<Employe> adapter = new ArrayAdapter<Employe>(RecordsDetailActivity.this,
                android.R.layout.simple_spinner_item,
                employes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
