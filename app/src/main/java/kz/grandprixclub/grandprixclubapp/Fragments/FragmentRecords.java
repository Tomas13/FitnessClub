package kz.grandprixclub.grandprixclubapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.RecordsDetailActivity;
import kz.grandprixclub.grandprixclubapp.ServerRequests.ServicesRequest;
import kz.grandprixclub.grandprixclubapp.Models.Service;
import kz.grandprixclub.grandprixclubapp.Models.User;

public class FragmentRecords extends ListFragment {

    private int[] category_ids = {4, 1, 2};
    private Service[] services;

    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout_categories);
        tabLayout.addTab(tabLayout.newTab().setText("Салон"));
        tabLayout.addTab(tabLayout.newTab().setText("Медицина"));
        tabLayout.addTab(tabLayout.newTab().setText("Фитнес"));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getServices(category_ids[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getServices(4); // get salon services first


    }

    private void getServices(int category_id) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            JSONArray jServices = jsonResponse.getJSONObject("result").getJSONArray("services");

                            setAdapter(services = Service.getServicesAsArray(jServices, false));
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

                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        };

        ServicesRequest servicesRequest = new ServicesRequest(User.getSavedApiToken(getActivity()), category_id, responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(servicesRequest);

    }

    private void setAdapter(Service[] services_names) {
        ArrayAdapter<Service> adapter = new ArrayAdapter<Service>(getActivity(),
                R.layout.simple_list_item_custom, services_names);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), RecordsDetailActivity.class);
        intent.putExtra("service_id", services[position].getService_id());
        //startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDetach() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Boolean isUpdateNeeded = data.getBooleanExtra("is_update_needed", true);

        if (isUpdateNeeded) {
            getServices(4);
        }
    }
}