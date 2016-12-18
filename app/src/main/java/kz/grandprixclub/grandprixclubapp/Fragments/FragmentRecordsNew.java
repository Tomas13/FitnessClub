package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;

import kz.grandprixclub.grandprixclubapp.Adapters.MyRecordsListViewAdapter;
import kz.grandprixclub.grandprixclubapp.Models.Record;
import kz.grandprixclub.grandprixclubapp.PasswordResetActivity;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.RecordsDetailNewActivity;
import kz.grandprixclub.grandprixclubapp.ServerRequests.RecordsRequest;
import kz.grandprixclub.grandprixclubapp.Models.User;

public class FragmentRecordsNew extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final static String TAG = FragmentRecordsNew.class.getSimpleName();
    private int[] category_ids = {4, 1, 2};
    private int selectedCatId = 4;
    private RequestQueue mRequestQueue;
    Button bRecord;

    private OnFragmentInteractionListener mListener;

    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMyRecords(category_ids[0]);
        }
    };

    public FragmentRecordsNew() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRecordsNew.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRecordsNew newInstance(String param1, String param2) {
        FragmentRecordsNew fragment = new FragmentRecordsNew();
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateUIReceiver, new IntentFilter("update_user_records"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records_new, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout_categories);
        tabLayout.addTab(tabLayout.newTab().setText("Салон"));
        tabLayout.addTab(tabLayout.newTab().setText("Медицина"));
        tabLayout.addTab(tabLayout.newTab().setText("Фитнес"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getMyRecords(category_ids[tab.getPosition()]);
                selectedCatId = category_ids[tab.getPosition()];
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bRecord = (Button) getView().findViewById(R.id.footer_button_record);
        bRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Selected Category ID: " + selectedCatId);
                Intent intent = new Intent(getActivity(), RecordsDetailNewActivity.class);
                intent.putExtra("category_id", selectedCatId);
                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        View listViewHeader = getActivity().getLayoutInflater().inflate(R.layout.my_records_table, null);
        this.getListView().addHeaderView(listViewHeader);

        TextView header1 = (TextView) listViewHeader.findViewById(R.id.service_name);
        TextView header2 = (TextView) listViewHeader.findViewById(R.id.service_date);
        header1.setTypeface(null, Typeface.BOLD);
        header1.setTextColor(Color.BLACK);
        header1.setText("Услуга");
        header2.setTypeface(null, Typeface.BOLD);
        header2.setTextColor(Color.BLACK);
        header2.setText("Время");
        listViewHeader.setBackgroundResource(R.drawable.row_shape);
        getMyRecords(category_ids[0]);
    }

    public void getMyRecords(int categoryId) {
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
                            JSONArray jRecords = jsonResponse.getJSONObject("result").getJSONArray("services");
                            Record[] myRecords = Record.getRecordsAsArray(jRecords);
                            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

                            for (Record record : myRecords) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put(MyRecordsListViewAdapter.COLUMN_1, record.getServiceName());
                                map.put(MyRecordsListViewAdapter.COLUMN_2, record.getDate() + " " + record.getBeginTime());
                                list.add(map);
                            }

                            setAdapter(list);
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
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(new RecordsRequest(User.getSavedApiToken(getActivity()), categoryId, responseListener, responseErrorListener));
    }

    private void setAdapter(ArrayList<HashMap<String, String>> list) {
        MyRecordsListViewAdapter adapter = new MyRecordsListViewAdapter(list, getActivity());
        setListAdapter(adapter);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
