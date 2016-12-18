package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kz.grandprixclub.grandprixclubapp.Models.Card;
import kz.grandprixclub.grandprixclubapp.Models.CardFitness;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.CardFitnessRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.MedicalCardDetailRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.MedicalCardRequest;
import kz.grandprixclub.grandprixclubapp.Models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TableLayout tableLayout;

    View.OnClickListener onRowClickListener;

    private RequestQueue mRequestQueue;

    public FragmentCard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCard.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCard newInstance(String param1, String param2) {
        FragmentCard fragment = new FragmentCard();
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
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout_card);
        tableLayout = (TableLayout) getView().findViewById(R.id.tl_card);

        tabLayout.addTab(tabLayout.newTab().setText("Медицинский журнал"));
        tabLayout.addTab(tabLayout.newTab().setText("Фитнес"));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //getServices(category_ids[tab.getPosition()]);
                switch (tab.getPosition()) {
                    case 0:
                        getCards();
                        break;
                    case 1:
                        getCardsFitness();
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

        onRowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCardDetail(v.getId());
            }
        };

        getCards();
    }

    private void cardsFitnessTableRowAdd(CardFitness[] cardFitnesses) {
        tryToCleanTableLayout();

        TableRow[] tr = new TableRow[cardFitnesses[0].getInfoCount()];

        for (int i = 0; i < cardFitnesses[0].getInfoCount(); i++) {
            List<String> listInfo = cardFitnesses[0].getFitnessCardInfo().get(i);
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

        for (int n = 1; n < cardFitnesses.length; n++) {
            for (int m = 0; m < tr.length; m++) {
                List<String> listInfo1 = cardFitnesses[n].getFitnessCardInfo().get(m);

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
            tableLayout.addView(r);
        }

    }

    private void cardsTableRowAdd(Card[] cards) {
        tryToCleanTableLayout();

        TableRow tr = new TableRow(getActivity());
        tr.setBackgroundResource(R.drawable.row_shape);

        TextView th = new TextView(getActivity());
        th.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        th.setTypeface(null, Typeface.BOLD);
        th.setTextColor(Color.BLACK);
        th.setGravity(Gravity.CENTER_VERTICAL);
        th.setText("Дата");
        //th.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        th.setPadding(8, 16, 8, 16);
        tr.addView(th);

        TextView th1 = new TextView(getActivity());
        th1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        th1.setTypeface(null, Typeface.BOLD);
        th1.setTextColor(Color.BLACK);
        th1.setGravity(Gravity.CENTER_VERTICAL);
        th1.setText("Врач");
        //th1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        th1.setPadding(8, 16, 8, 16);
        tr.addView(th1);

        TextView th2 = new TextView(getActivity());
        th2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        th2.setTypeface(null, Typeface.BOLD);
        th2.setTextColor(Color.BLACK);
        th2.setGravity(Gravity.CENTER_VERTICAL);
        th2.setText("Жалоба");
        //th2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        th2.setPadding(8, 16, 8, 16);
        tr.addView(th2);

        TextView th3 = new TextView(getActivity());
        th3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(th3);

        tableLayout.addView(tr);

        int i = 0;
        for (Card card : cards) {
            TableRow tr2 = new TableRow(getActivity());
            if (i % 2 != 0) {
                tr2.setBackgroundColor(Color.parseColor("#e7eaf0"));
            }
            tr2.setClickable(true);
            tr2.setId(card.getCardId());
            tr2.setOnClickListener(onRowClickListener);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
            lp.gravity = Gravity.CENTER;

            TextView td = new TextView(getActivity());
            td.setLayoutParams(lp);
            td.setText(card.getDate());
            td.setTextColor(Color.BLACK);
            td.setPadding(8, 16, 8, 16);
            //td.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tr2.addView(td);

            TextView td1 = new TextView(getActivity());
            td1.setLayoutParams(lp);
            td1.setText(card.getPhysicianName());
            td1.setTextColor(Color.BLACK);
            td1.setPadding(8, 16, 8, 16);
            //td1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tr2.addView(td1);

            TextView td2 = new TextView(getActivity());
            td2.setLayoutParams(lp);
            td2.setText(card.getComplaint());
            td2.setTextColor(Color.BLACK);
            td2.setPadding(8, 16, 8, 16);
            //td2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tr2.addView(td2);

            /*TextView td3 = new TextView(getActivity());
            td3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            td3.setBackgroundResource(R.drawable.ic_right);
            tr2.addView(td3);*/

            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.ic_str);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            imageView.setPadding(8,16,8,16);
            tr2.addView(imageView);


            tableLayout.addView(tr2);
            i++;
        }


    }

    private void cardsDetailTableRowAdd(Card card) {
        tryToCleanTableLayout();

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setBackgroundResource(R.drawable.row_shape);

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.ic_str_left);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(layoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCards();
            }
        });

        TextView th = new TextView(getActivity());
        //th.setTypeface(null, Typeface.BOLD);
        th.setTextColor(Color.BLACK);
        th.setGravity(Gravity.LEFT);
        th.setTextColor(getResources().getColor(R.color.colorPrimaryPurple));
        th.setText("Назад к списку");
        th.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        th.setPadding(0, 16, 16, 30);
        th.setClickable(true);
        th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCards();
            }
        });

        linearLayout.addView(imageView);
        linearLayout.addView(th);
        tableLayout.addView(linearLayout);

        TableRow tr1 = new TableRow(getActivity());
        TextView td = new TextView(getActivity());
        td.setTypeface(null, Typeface.BOLD);
        td.setTextColor(Color.BLACK);
        td.setGravity(Gravity.LEFT);
        td.setText("Дата");
        //td.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td.setPadding(16, 16, 16, 16);
        TextView td1 = new TextView(getActivity());
        td1.setTextColor(Color.BLACK);
        td1.setGravity(Gravity.LEFT);
        td1.setText(card.getDate());
        //td1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td1.setPadding(16, 16, 16, 16);
        tr1.addView(td);
        tr1.addView(td1);
        tableLayout.addView(tr1);

        TableRow tr2 = new TableRow(getActivity());
        tr2.setBackgroundColor(Color.parseColor("#e7eaf0"));
        TextView td2 = new TextView(getActivity());
        td2.setTypeface(null, Typeface.BOLD);
        td2.setTextColor(Color.BLACK);
        td2.setGravity(Gravity.LEFT);
        td2.setText("Врач");
        //td2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td2.setPadding(16, 16, 16, 16);
        TextView td3 = new TextView(getActivity());
        td3.setTextColor(Color.BLACK);
        td3.setGravity(Gravity.LEFT);
        td3.setText(card.getPhysicianName());
        //td3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td3.setPadding(16, 16, 16, 16);
        tr2.addView(td2);
        tr2.addView(td3);
        tableLayout.addView(tr2);

        TableRow tr3 = new TableRow(getActivity());
        TextView td4 = new TextView(getActivity());
        td4.setTypeface(null, Typeface.BOLD);
        td4.setTextColor(Color.BLACK);
        td4.setGravity(Gravity.LEFT);
        td4.setText("Жалоба");
        //td4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td4.setPadding(16, 16, 16, 16);
        TextView td5 = new TextView(getActivity());
        td5.setTextColor(Color.BLACK);
        td5.setGravity(Gravity.LEFT);
        td5.setText(card.getComplaint());
        //td5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td5.setPadding(16, 16, 16, 16);
        tr3.addView(td4);
        tr3.addView(td5);
        tableLayout.addView(tr3);

        TableRow tr4 = new TableRow(getActivity());
        tr4.setBackgroundColor(Color.parseColor("#e7eaf0"));
        TextView td6 = new TextView(getActivity());
        td6.setTypeface(null, Typeface.BOLD);
        td6.setTextColor(Color.BLACK);
        td6.setGravity(Gravity.LEFT);
        td6.setText("Диагноз");
        //td6.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td6.setPadding(16, 16, 16, 16);
        TextView td7 = new TextView(getActivity());
        td7.setTextColor(Color.BLACK);
        td7.setGravity(Gravity.LEFT);
        td7.setText(card.getDiagnosis());
        //td7.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td7.setPadding(16, 16, 16, 16);
        tr4.addView(td6);
        tr4.addView(td7);
        tableLayout.addView(tr4);

        TableRow tr5 = new TableRow(getActivity());
        TextView td8 = new TextView(getActivity());
        td8.setTypeface(null, Typeface.BOLD);
        td8.setTextColor(Color.BLACK);
        td8.setGravity(Gravity.LEFT);
        td8.setText("Заключение");
        //td8.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td8.setPadding(16, 16, 16, 16);
        TextView td9 = new TextView(getActivity());
        td9.setTextColor(Color.BLACK);
        td9.setGravity(Gravity.LEFT);
        td9.setText(card.getConclusion());
        //td9.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td9.setPadding(16, 16, 16, 16);
        tr5.addView(td8);
        tr5.addView(td9);
        tableLayout.addView(tr5);

        TableRow tr6 = new TableRow(getActivity());
        tr6.setBackgroundColor(Color.parseColor("#e7eaf0"));
        TextView td10 = new TextView(getActivity());
        td10.setTypeface(null, Typeface.BOLD);
        td10.setTextColor(Color.BLACK);
        td10.setGravity(Gravity.LEFT);
        td10.setText("Рекомендация");
        //td10.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td10.setPadding(16, 16, 16, 16);
        TextView td11 = new TextView(getActivity());
        td11.setTextColor(Color.BLACK);
        td11.setGravity(Gravity.LEFT);
        td11.setText(card.getRecommendation());
        //td11.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td11.setPadding(16, 16, 16, 16);
        tr6.addView(td10);
        tr6.addView(td11);
        tableLayout.addView(tr6);

        TableRow tr7 = new TableRow(getActivity());
        TextView td12 = new TextView(getActivity());
        td12.setTypeface(null, Typeface.BOLD);
        td12.setTextColor(Color.BLACK);
        td12.setGravity(Gravity.LEFT);
        td12.setText("Дневник");
        //td12.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td12.setPadding(16, 16, 16, 16);
        TextView td13 = new TextView(getActivity());
        td13.setTextColor(Color.BLACK);
        td13.setGravity(Gravity.LEFT);
        td13.setText(card.getDiary());
        //td13.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        td13.setPadding(16, 16, 16, 16);
        tr7.addView(td12);
        tr7.addView(td13);
        tableLayout.addView(tr7);

    }

    private void getCardDetail(int cardId) {
        tryToCleanTableLayout();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            cardsDetailTableRowAdd(Card.getCard(jsonResponse.getJSONObject("result").getJSONObject("card")));
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

        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(new MedicalCardDetailRequest(User.getSavedApiToken(getActivity()),
                cardId, responseListener, responseErrorListener));
    }

    private void getCardsFitness() {
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
                        JSONArray jCards = jsonResponse.getJSONObject("result").getJSONArray("cards");
                        if (!has_error && jCards.length() > 0) {
                            cardsFitnessTableRowAdd(CardFitness.getCardsFitnessAsArray(jCards));
                        } else {
                            tryToCleanTableLayout();

                            TableRow tr = new TableRow(getActivity());

                            TextView th = new TextView(getActivity());
                            //th.setTypeface(null, Typeface.BOLD);
                            th.setGravity(Gravity.CENTER);
                            th.setText("Нет данных");
                            //th.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            th.setPadding(16, 86, 16, 16);
                            tr.addView(th);
                            tableLayout.addView(tr);
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
        progressDialog.setMessage("Загрузка фитнесс карты");
        progressDialog.show();
        Volley.newRequestQueue(getActivity()).add(new CardFitnessRequest(User.getSavedApiToken(getActivity()),
                responseListener, responseErrorListener));
    }

    private void getCards() {
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
                        JSONArray jCards = jsonResponse.getJSONObject("result").getJSONArray("cards");
                        if (!has_error && jCards.length() > 0) {
                            cardsTableRowAdd(Card.getCardsAsArray(jCards));
                        } else {
                            tryToCleanTableLayout();

                            TableRow tr = new TableRow(getActivity());

                            TextView th = new TextView(getActivity());
                            //th.setTypeface(null, Typeface.BOLD);
                            th.setGravity(Gravity.CENTER);
                            th.setText("Нет данных");
                            //th.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            th.setPadding(16, 86, 16, 16);
                            tr.addView(th);
                            tableLayout.addView(tr);
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
        progressDialog.setMessage("Загрузка медицинской карты");
        progressDialog.show();
        Volley.newRequestQueue(getActivity()).add(new MedicalCardRequest(User.getSavedApiToken(getActivity()),
                responseListener, responseErrorListener));
    }

    private void tryToCleanTableLayout() {
        tableLayout.removeAllViews();
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
