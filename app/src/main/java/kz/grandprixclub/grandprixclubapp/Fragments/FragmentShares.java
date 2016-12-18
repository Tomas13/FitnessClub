package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import kz.grandprixclub.grandprixclubapp.Adapters.SharesListViewAdapter;
import kz.grandprixclub.grandprixclubapp.Models.Record;
import kz.grandprixclub.grandprixclubapp.Models.Shares;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.RecordsRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.SharesRequest;
import kz.grandprixclub.grandprixclubapp.SharesDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentShares.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentShares#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentShares extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final static String TAG = FragmentShares.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private Shares[] shareses;

    public FragmentShares() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentShares.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentShares newInstance(String param1, String param2) {
        FragmentShares fragment = new FragmentShares();
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
        return inflater.inflate(R.layout.fragment_shares, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getShares();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
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

    private void getShares() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray result = jsonResponse.getJSONArray("result");
                        if (result.length() > 0) {
                            shareses = Shares.getSharesAsArray(result);
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();
                            for (Shares shares : shareses) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(SharesListViewAdapter.ANNONCE, shares.getTitle());
                                map.put(SharesListViewAdapter.IMAGE_URL, shares.getMedia());
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
        progressDialog.setMessage("Загрузка акций");
        progressDialog.show();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(new SharesRequest(responseListener, responseErrorListener));
    }

    private void setAdapter(ArrayList<HashMap<String,String>> list) {
        SharesListViewAdapter adapter = new SharesListViewAdapter(list, getActivity(), mRequestQueue);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Shares shares = shareses[position];
        Intent intent = new Intent(getActivity(), SharesDetailActivity.class);
        intent.putExtra("share", shares);
        startActivity(intent);
    }
}
