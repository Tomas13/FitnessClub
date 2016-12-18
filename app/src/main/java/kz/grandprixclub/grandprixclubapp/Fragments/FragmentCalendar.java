package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import kz.grandprixclub.grandprixclubapp.Models.Calendar;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.CalendarRequest;
import kz.grandprixclub.grandprixclubapp.Models.Service;
import kz.grandprixclub.grandprixclubapp.Models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCalendar.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCalendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCalendar extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TableLayout tableLayout;
    TextView tvLeftMonth;
    TextView tvRightMonth;
    int currentMonth;

    private RequestQueue mRequestQueue;

    public FragmentCalendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCalendar.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCalendar newInstance(String param1, String param2) {
        FragmentCalendar fragment = new FragmentCalendar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final java.util.Calendar c = java.util.Calendar.getInstance();
        currentMonth = c.get(java.util.Calendar.MONTH) + 1;

        tableLayout = (TableLayout) getView().findViewById(R.id.tl_calendar);
        tvLeftMonth = (TextView) getView().findViewById(R.id.tv_left_month);
        tvRightMonth = (TextView) getView().findViewById(R.id.tv_right_month);

        tvLeftMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth--;
                tvRightMonth.setVisibility(View.VISIBLE);
                tvLeftMonth.setVisibility(View.VISIBLE);
                getCalendar(currentMonth);
            }
        });

        tvRightMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth++;
                tvLeftMonth.setVisibility(View.VISIBLE);
                if (currentMonth == c.get(java.util.Calendar.MONTH) + 1) {
                    tvRightMonth.setVisibility(View.INVISIBLE);
                }
                getCalendar(currentMonth);
            }
        });

        tvLeftMonth.setVisibility(View.VISIBLE);
        tvRightMonth.setVisibility(View.INVISIBLE);
        getCalendar(currentMonth); //текущий месяц
    }

    private void addRowsToTable (Calendar[][] calendars) {
        tryToCleanTableLayout();
        TextView tv_month = (TextView) getView().findViewById(R.id.tv_month);
        tv_month.setText(calendars[1][7].getMonthName());
        if (calendars.length > 0) {
            for (Integer i = 1; i < calendars.length; i++) {
                TableRow tableRow = new TableRow(getActivity());
                for (Integer j = 1; j < 8; j++) {

                    if (calendars != null &&
                            calendars[i] != null &&
                            calendars[i][j] != null &&
                            calendars[i][j].getDate().length() > 0) {
                        Calendar calendar = calendars[i][j];

                        if (calendar.getServices() != null) {
                            LinearLayout linearLayout = new LinearLayout(getActivity());
                            linearLayout.setBackgroundResource(R.drawable.cell_vacant_shape);
                            linearLayout.setMinimumWidth(16);
                            linearLayout.setMinimumHeight(46);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.setPadding(2, 2, 2, 2);

                            TextView textView = new TextView(getActivity());
                            textView.setTypeface(null, Typeface.BOLD);
                            textView.setText(calendar.getNumber());
                            textView.setGravity(Gravity.RIGHT);
                            textView.setMinHeight(46);
                            linearLayout.addView(textView);

                            int count = 0;
                            for (Service service : calendar.getServices()) {
                                if (count > 2) {
                                    continue;
                                }
                                count++;
                                LinearLayout linearLayout2 = new LinearLayout(getActivity());
                                linearLayout2.setMinimumWidth(16);
                                linearLayout2.setMinimumHeight(33);
                                linearLayout2.setOrientation(LinearLayout.HORIZONTAL);

                                TextView textView2 = new TextView(getActivity());
                                TextView textView3 = new TextView(getActivity());
                                switch (service.getCategory_name()) {
                                    case "salon":
                                        textView2.setBackgroundResource(R.color.colorPrimaryPurple);
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimaryPurple));
                                        textView3.setTextColor(getResources().getColor(R.color.colorPrimaryPurple));
                                        break;
                                    case "fitness":
                                        textView2.setBackgroundResource(R.color.ColorPrimaryGreen);
                                        textView2.setTextColor(getResources().getColor(R.color.ColorPrimaryGreen));
                                        textView3.setTextColor(getResources().getColor(R.color.ColorPrimaryGreen));
                                        break;
                                    case "bath":
                                        textView2.setBackgroundResource(R.color.ColorPrimaryOrange);
                                        textView2.setTextColor(getResources().getColor(R.color.ColorPrimaryOrange));
                                        textView3.setTextColor(getResources().getColor(R.color.ColorPrimaryOrange));
                                        break;
                                    case "pool":
                                        textView2.setBackgroundResource(R.color.ColorPrimaryBlue);
                                        textView2.setTextColor(getResources().getColor(R.color.ColorPrimaryBlue));
                                        textView3.setTextColor(getResources().getColor(R.color.ColorPrimaryBlue));
                                        break;
                                }
                                textView2.setGravity(Gravity.LEFT);
                                textView2.setMinHeight(6);
                                textView2.setMinWidth(6);
                                textView2.setPadding(6, 0, 0, 0);
                                //textView2.setText(".");
                                linearLayout2.addView(textView2);

                                textView3.setTextSize(10);
                                textView3.setText(service.getTime());
                                textView3.setGravity(Gravity.LEFT);
                                textView3.setMinHeight(33);
                                linearLayout2.addView(textView3);

                                linearLayout.addView(linearLayout2);
                            }


                            for (int z = 0; z < 3-count; z++) {
                                TextView textViewEmpty = new TextView(getActivity());
                                textViewEmpty.setMinHeight(33);

                                linearLayout.addView(textViewEmpty);
                            }

                            tableRow.addView(linearLayout);
                        } else {
                            LinearLayout linearLayout = new LinearLayout(getActivity());
                            linearLayout.setBackgroundResource(R.drawable.cell_vacant_shape);
                            linearLayout.setMinimumWidth(16);
                            linearLayout.setMinimumHeight(46);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.setGravity(Gravity.RIGHT);
                            linearLayout.setPadding(2, 2, 2, 2);

                            TextView textView = new TextView(getActivity());
                            textView.setTypeface(null, Typeface.BOLD);
                            textView.setText(calendar.getNumber());
                            textView.setGravity(Gravity.RIGHT);
                            textView.setMinHeight(46);
                            linearLayout.addView(textView);

                            TextView textView2 = new TextView(getActivity());
                            textView2.setMinHeight(33);
                            linearLayout.addView(textView2);

                            TextView textView3 = new TextView(getActivity());
                            textView3.setMinHeight(33);
                            linearLayout.addView(textView3);

                            TextView textView4 = new TextView(getActivity());
                            textView4.setMinHeight(33);
                            linearLayout.addView(textView4);

                            tableRow.addView(linearLayout);
                        }

                    }

                }

                if (tableLayout != null) {
                    tableLayout.addView(tableRow);
                }


            }
        } else {
            tvRightMonth.setVisibility(View.INVISIBLE);
            tvLeftMonth.setVisibility(View.INVISIBLE);
            TableRow tableRow = new TableRow(getActivity());

            TextView textView = new TextView(getActivity());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText("Календарь пуст");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setMinHeight(46);
            tableRow.addView(textView);

            if (tableLayout != null) {
                tableLayout.addView(tableRow);
            }
        }


    }

    private void tryToCleanTableLayout() {
        int k = tableLayout.getChildCount();
        while (k > 2) {
            TableRow row = (TableRow)tableLayout.getChildAt(2); // integer k equals 2 because we have 2 header rows and we do not need to remove them
            tableLayout.removeView(row);
            k = tableLayout.getChildCount();
        }
    }

    private void getCalendar(int month) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        tryToCleanTableLayout();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        Log.d("AAAAAAAAAAA", response);
                        if (!has_error) {
                            Calendar[][] calendars = Calendar.getCalendarAsArray(jsonResponse.getJSONObject("result").getJSONObject("days"),
                                    jsonResponse.getJSONObject("result").getString("start_date"), jsonResponse.getJSONObject("result").getString("end_date"),
                                    jsonResponse.getJSONObject("result").getString("month_name"));
                            addRowsToTable(calendars);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка");
        progressDialog.show();
        java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        Log.d("DATE", year+" - "+month);
        CalendarRequest calendarRequest = new CalendarRequest(User.getSavedApiToken(getActivity()), year + "", month + "", responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(calendarRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
