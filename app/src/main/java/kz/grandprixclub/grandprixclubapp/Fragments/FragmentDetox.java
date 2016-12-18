package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import kz.grandprixclub.grandprixclubapp.Models.Detox;
import kz.grandprixclub.grandprixclubapp.Models.Impedancemetry;
import kz.grandprixclub.grandprixclubapp.Models.Procedure;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.DetoxRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.ImpedancemetryRequest;
import kz.grandprixclub.grandprixclubapp.Models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDetox.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDetox#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDetox extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String TAG = FragmentDetox.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TableLayout tl_detox;

    private RequestQueue mRequestQueue;

    public FragmentDetox() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDetox.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDetox newInstance(String param1, String param2) {
        FragmentDetox fragment = new FragmentDetox();
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
        return inflater.inflate(R.layout.fragment_detox, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout_detox);
        tl_detox = (TableLayout) getView().findViewById(R.id.tl_detox);

        tabLayout.addTab(tabLayout.newTab().setText("Программа"));
        tabLayout.addTab(tabLayout.newTab().setText("Импедансометрия"));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //getServices(category_ids[tab.getPosition()]);
                switch (tab.getPosition()) {
                    case 0:
                        getDetox();
                        break;
                    case 1:
                        getImpedancemetry();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getDetox();

    }

    private void tryToCleanTableLayout() {
        tl_detox.removeAllViews();
    }

    private void buildTableRowsForImpedancemetry(Impedancemetry[] impedancemetries) {
        tryToCleanTableLayout();
        //Log.d(TAG, impedancemetries[0].getInfoCount() + "");

        TableRow[] tr = new TableRow[impedancemetries[0].getInfoCount()];

        for (int i = 0; i < impedancemetries[0].getInfoCount(); i++) {
            List<String> listInfo = impedancemetries[0].getInfo().get(i);
            tr[i] = new TableRow(getActivity());
            if (i == 0) {
                tr[i].setBackgroundResource(R.drawable.row_shape);
            } else if (i % 2 == 0) {
                tr[i].setBackgroundColor(Color.parseColor("#e7eaf0"));
            }
            TextView td = new TextView(getActivity());
            td.setGravity(Gravity.LEFT);
            td.setText(listInfo.get(0));
            td.setTextColor(Color.BLACK);
            td.setMaxWidth(250);
            td.setPadding(16, 16, 16, 16);
            //td.setTextSize(16);
            tr[i].addView(td);

            TextView td1 = new TextView(getActivity());
            td1.setGravity(Gravity.RIGHT);
            td1.setText(listInfo.get(1));
            td1.setTextColor(Color.BLACK);
            td1.setPadding(16, 16, 16, 16);
            //td1.setTextSize(16);
            tr[i].addView(td1);

        }

        for (int n = 1; n < impedancemetries.length; n++) {
            for (int m = 0; m < tr.length; m++) {
                List<String> listInfo1 = impedancemetries[n].getInfo().get(m);

                TextView td2 = new TextView(getActivity());
                td2.setGravity(Gravity.RIGHT);
                td2.setText(listInfo1.get(1));
                td2.setTextColor(Color.BLACK);
                td2.setPadding(16, 16, 16, 16);
                //td2.setTextSize(16);
                tr[m].addView(td2);


            }
        }

        for (TableRow r : tr) {
            tl_detox.addView(r);
        }


    }

    private void buildTableRowsForDetox (Detox[] detoxes) {
        tryToCleanTableLayout();

        TableRow tr = new TableRow(getActivity());
        tr.setPadding(0,0,0,16);
        tr.setBackgroundResource(R.drawable.row_shape);

        TextView th = new TextView(getActivity());
        th.setTypeface(null, Typeface.BOLD);
        th.setTextColor(Color.BLACK);
        th.setGravity(Gravity.CENTER);
        th.setText("Дата\nВремя");
        th.setPadding(8, 8, 8, 16);
        th.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(th);

        TextView th1 = new TextView(getActivity());
        th1.setTextColor(Color.BLACK);
        th1.setGravity(Gravity.CENTER);
        th1.setText("Процедура");
        th1.setTypeface(null, Typeface.BOLD);
        th1.setPadding(8, 8, 8, 16);
        th1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(th1);

        TextView th2 = new TextView(getActivity());
        th2.setPadding(8, 8, 8, 16);
        th2.setTextColor(Color.BLACK);
        th2.setGravity(Gravity.CENTER);
        th2.setText("Кабинет");
        th2.setTypeface(null, Typeface.BOLD);
        th2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(th2);

        TextView th3 = new TextView(getActivity());
        th3.setPadding(8, 8, 8, 16);
        th3.setTextColor(Color.BLACK);
        th3.setGravity(Gravity.CENTER);
        th3.setText("Исполнитель");
        th3.setTypeface(null, Typeface.BOLD);
        th3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(th3);

        /*TextView th4 = new TextView(getActivity());
        th4.setPadding(8, 8, 8, 8);
        th4.setTextColor(Color.BLACK);
        th4.setGravity(Gravity.CENTER);
        th4.setText("Коммен\nтарии");
        th4.setTypeface(null, Typeface.BOLD);
        tr.addView(th4);*/

        tl_detox.addView(tr);

        for (Detox detox : detoxes) {
            TableRow tr1 = new TableRow(getActivity());
            TextView th5 = new TextView(getActivity());
            th5.setTextColor(Color.BLACK);
            th5.setTypeface(null, Typeface.BOLD);
            th5.setText(detox.getDate());
            th5.setPadding(8, 8, 8, 8);
            th5.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr1.addView(th5);

            tl_detox.addView(tr1);

            int r = 1;
            for (Procedure procedure : detox.getProcedures()) {
                TableRow tr2 = new TableRow(getActivity());

                if (r % 2 == 0) {
                    tr2.setBackgroundColor(Color.parseColor("#e7eaf0"));
                }

                TextView th6 = new TextView(getActivity());
                th6.setTextColor(Color.BLACK);
                th6.setGravity(Gravity.CENTER);
                th6.setText(procedure.getProcedureTime());
                th6.setPadding(8, 8, 8, 8);
                th6.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tr2.addView(th6);

                TextView th7 = new TextView(getActivity());
                th7.setTextColor(Color.BLACK);
                th7.setGravity(Gravity.CENTER);
                th7.setText(procedure.getProcedureName());
                th7.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                th7.setPadding(8, 8, 8, 8);

                tr2.addView(th7);

                TextView th8 = new TextView(getActivity());
                th8.setTextColor(Color.BLACK);
                th8.setGravity(Gravity.CENTER);
                th8.setText(procedure.getCabinet());
                th8.setPadding(8, 8, 8, 8);
                th8.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tr2.addView(th8);


                TextView th9 = new TextView(getActivity());
                th9.setTextColor(Color.BLACK);
                th9.setGravity(Gravity.CENTER);
                th9.setText(procedure.getPhysicianName());
                th9.setPadding(8, 8, 8, 8);
                th9.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tr2.addView(th9);

                /*TextView th10 = new TextView(getActivity());
                th10.setTextColor(Color.BLACK);
                th10.setGravity(Gravity.CENTER);
                th10.setText(procedure.getComment());
                th10.setPadding(8, 8, 8, 8);
                tr2.addView(th10);*/

                tl_detox.addView(tr2);
                r++;
            }

        }
    }

    private void getImpedancemetry() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        //Log.d(TAG, jsonResponse.getJSONObject("result").toString());
                        JSONArray jImpedancemetries = jsonResponse.getJSONObject("result").getJSONArray("impedancemetries");

                        if (!has_error && jImpedancemetries.length() > 0) {
                            buildTableRowsForImpedancemetry(Impedancemetry.getImpedancemetryAsArray(jImpedancemetries));
                        } else {
                            tryToCleanTableLayout();

                            TableRow tr = new TableRow(getActivity());
                            TextView th = new TextView(getActivity());
                            th.setGravity(Gravity.CENTER);
                            th.setText("Нет данных");
                            th.setPadding(8, 86, 8, 8);
                            tr.addView(th);
                            tl_detox.addView(tr);
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

                if (getActivity() != null) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка");
        progressDialog.show();
        ImpedancemetryRequest impedancemetryRequest = new ImpedancemetryRequest(User.getSavedApiToken(getActivity()), responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(impedancemetryRequest);
    }

    private void getDetox() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    try {
                        Log.d(TAG, response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            JSONArray jDetoxes = jsonResponse.getJSONObject("result").getJSONArray("detoxes");
                            if (jDetoxes.length() > 0) {
                                buildTableRowsForDetox(Detox.getDetoxesAsArray(jDetoxes));
                            } else {
                                tryToCleanTableLayout();

                                TableRow tr = new TableRow(getActivity());
                                TextView th = new TextView(getActivity());
                                th.setGravity(Gravity.CENTER);
                                th.setText("Нет данных");
                                th.setPadding(8, 86, 8, 8);
                                tr.addView(th);
                                tl_detox.addView(tr);
                            }
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

                if (getActivity() != null) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка");
        progressDialog.show();
        DetoxRequest detoxRequest = new DetoxRequest(User.getSavedApiToken(getActivity()), responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(detoxRequest);
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
