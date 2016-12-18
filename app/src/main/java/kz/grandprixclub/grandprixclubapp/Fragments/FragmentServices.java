package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
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

import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.ServicesRequest;
import kz.grandprixclub.grandprixclubapp.Models.Service;
import kz.grandprixclub.grandprixclubapp.Models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentServices.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentServices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentServices extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RequestQueue mRequestQueue;

    TableLayout tlServices;

    public FragmentServices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentServices.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentServices newInstance(String param1, String param2) {
        FragmentServices fragment = new FragmentServices();
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
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tlServices = (TableLayout) getView().findViewById(R.id.tlServices);
        getServices();
    }

    public void getServices() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            JSONArray jServices = jsonResponse.getJSONObject("result").getJSONArray("services");
                            //Log.d("ASDASDASD", jServices.toString());
                            if (jServices.length() > 0 ) {
                                Service[] services = Service.getServicesAsArray(jServices, true);

                                int i = 1;
                                for (Service service: services) {
                                    TableRow tableRow = new TableRow(getActivity());
                                    //TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                    //tableRow.setLayoutParams(layoutParams);

                                    if (i % 2 == 0) {
                                        tableRow.setBackgroundColor(Color.parseColor("#e7eaf0"));
                                    }

                                    TextView textView1 = new TextView(getActivity());
                                    textView1.setText(service.getDate());
                                    textView1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    textView1.setPadding(14, 16, 8, 16);
                                    //textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                    textView1.setTextColor(Color.BLACK);
                                    tableRow.addView(textView1);

                                    TextView textView2 = new TextView(getActivity());
                                    textView2.setText(service.getTime());
                                    textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    textView2.setPadding(14, 16, 8, 16);
                                    //textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                    textView2.setTextColor(Color.BLACK);
                                    tableRow.addView(textView2);

                                    TextView textView3 = new TextView(getActivity());
                                    textView3.setText(service.getName());
                                    textView3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    textView3.setPadding(14, 16, 8, 16);
                                    //textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                    textView3.setTextColor(Color.BLACK);
                                    tableRow.addView(textView3);

                                    if (tlServices != null) {
                                        tlServices.addView(tableRow, i);
                                    }
                                    i++;
                                }
                            } else  {
                                //TableRow tableRow = new TableRow(getActivity());
                                TextView textView = new TextView(getActivity());
                                textView.setGravity(Gravity.CENTER);
                                textView.setText("Нет данных");
                                textView.setPadding(16, 86, 16, 16);
                                //tableRow.addView(textView);
                                tlServices.addView(textView);
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
        progressDialog.setMessage("Загрузка истории услуг");
        progressDialog.show();
        ServicesRequest servicesRequest = new ServicesRequest(User.getSavedApiToken(getActivity()), responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(servicesRequest);
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
