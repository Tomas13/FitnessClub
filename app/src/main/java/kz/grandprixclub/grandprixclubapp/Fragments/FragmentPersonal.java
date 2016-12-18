package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.UserRequest;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.Utils.RoundImage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPersonal.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPersonal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPersonal extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RequestQueue mRequestQueue;

    private User user;
    TextView tvUserName;
    TextView tvUserPhone;
    TextView tvUserBirthday;
    TextView tvUserAddress;
    TextView tvUserDeposit;
    TextView tvUserLastName;
    ImageView ivAvatar;
    RoundImage roundImage;

    private static final String TAG = FragmentPersonal.class.getSimpleName();

    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getUserInfo();
        }
    };

    public FragmentPersonal() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPersonal.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPersonal newInstance(String param1, String param2) {
        FragmentPersonal fragment = new FragmentPersonal();
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateUIReceiver, new IntentFilter("update_profile"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvUserLastName = (TextView) getView().findViewById(R.id.tvUserLastName);
        tvUserName = (TextView) getView().findViewById(R.id.tvUserName);
        tvUserPhone = (TextView) getView().findViewById(R.id.tvUserPhone);
        tvUserAddress = (TextView) getView().findViewById(R.id.tvUserAddress);
        tvUserBirthday = (TextView) getView().findViewById(R.id.tvUserBirthday);
        tvUserDeposit = (TextView) getView().findViewById(R.id.tvUserDeposit);
        ivAvatar = (ImageView) getView().findViewById(R.id.imageView);
        getUserInfo();
        Log.d(TAG, "TOKEN: " + User.getSavedApiToken(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    public void getUserInfo() {
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
                            JSONObject jUser = jsonResponse.getJSONObject("result").getJSONObject("user");
                            user = new User(jUser);

                            ImageRequest ir = new ImageRequest(user.getAvatar(), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    roundImage = new RoundImage(response);
                                    ivAvatar.setImageDrawable(roundImage);
                                }
                            }, 0, 0, null, null);
                            mRequestQueue.add(ir);
                            tvUserLastName.setText(user.getLastname());
                            tvUserName.setText(user.getName());
                            tvUserPhone.setText(user.getPhone());
                            tvUserAddress.setText(user.getAddress());
                            tvUserBirthday.setText(user.getBirthday());
                            tvUserDeposit.setText(String.valueOf(user.getDeposit()));


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
        UserRequest userRequest = new UserRequest(User.getSavedApiToken(getActivity()), responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(userRequest);
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
