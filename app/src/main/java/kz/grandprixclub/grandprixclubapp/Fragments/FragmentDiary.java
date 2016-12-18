package kz.grandprixclub.grandprixclubapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

import kz.grandprixclub.grandprixclubapp.Adapters.DiaryListViewAdapter;
import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryApproach;
import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryDay;
import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryExercise;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.DiaryRequest;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.DiarySaveDayRequest;

public class FragmentDiary extends ListFragment implements DiaryListViewAdapter.DiaryListViewAdapterListener {

    private static final String TAG = FragmentDiary.class.getSimpleName();

    TabLayout tabLayout;
    DiaryDay diaryDay;
    TextView tvExerciseNav;
    private boolean firstTime;
    View viewListHeader;
    ImageButton imageButtonLeft;
    ImageButton imageButtonRight;
    Button bSaveDay;
    private int currentExerciseId;

    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout_diary);
        tvExerciseNav = (TextView) getView().findViewById(R.id.tv_exercise_nav);
        imageButtonLeft = (ImageButton) getView().findViewById(R.id.image_button_left);
        imageButtonRight = (ImageButton) getView().findViewById(R.id.image_button_right);
        viewListHeader = getActivity().getLayoutInflater().inflate(R.layout.diary_table_head, null);
        bSaveDay = (Button) getView().findViewById(R.id.footer_button_save_day);
        this.getListView().addHeaderView(viewListHeader);
        this.getListView().setItemsCanFocus(true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getDiary(tab.getPosition(), 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bSaveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDay();
            }
        });

        firstTime = true;
        getDiary(0, 0); //get first day and first inner exercise


    }

    private void setAdapter(ArrayList<HashMap<String, String>> list) {
        DiaryListViewAdapter adapter = new DiaryListViewAdapter(getActivity(), list, this);
        setListAdapter(adapter);
    }

    private void setExerciseContent(int exerciseNumber) {
        currentExerciseId = exerciseNumber;
        DiaryExercise diaryExercise = diaryDay.getDiaryExercises()[currentExerciseId];

        tvExerciseNav.setText("Упражнение " + diaryExercise.getExerciseNumber() +
                "/" + diaryDay.getDiaryExercises().length);
        imageButtonLeft.setBackgroundResource(R.drawable.ic_keyboard_arrow_left_black_36dp);
        imageButtonRight.setBackgroundResource(R.drawable.ic_keyboard_arrow_right_black_36dp);

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (DiaryApproach diaryApproach : diaryExercise.getDiaryApproaches()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(DiaryApproach.COLUMN_1, diaryApproach.getApproachNumber());
            map.put(DiaryApproach.COLUMN_2, diaryApproach.getWeight());
            map.put(DiaryApproach.COLUMN_3, diaryApproach.getRepeat());
            map.put(DiaryApproach.COLUMN_4, diaryApproach.getRecovery());
            map.put(DiaryApproach.COLUMN_5, diaryApproach.getStrech());
            map.put(DiaryApproach.COLUMN_6, diaryApproach.isMaded() ? "true" : "false");

            list.add(map);
        }
        setAdapter(list);

        ((TextView) viewListHeader.findViewById(R.id.tv_exercise_name)).setText(diaryExercise.getExerciseName());
        ((TextView) viewListHeader.findViewById(R.id.tv_trainer_name)).setText(diaryExercise.getTrainerName());
    }

    private void onDiaryResponce(int position, final DiaryDay[] diaryDays, int exerciseId) {

        if (firstTime) {
            for (DiaryDay diaryDay : diaryDays) {
                tabLayout.addTab(tabLayout.newTab().setText(diaryDay.getDayName()));
            }
        }
        firstTime = false;

        diaryDay = diaryDays[position]; //set global Day Object
        if (isDayDone()) {
            bSaveDay.setVisibility(View.GONE);
        } else {
            bSaveDay.setVisibility(View.VISIBLE);
        }
        imageButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentExerciseId != 0) {
                    setExerciseContent(currentExerciseId - 1);
                }
            }
        });
        imageButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentExerciseId < diaryDay.getDiaryExercises().length - 1) {
                    setExerciseContent(currentExerciseId + 1);
                }
            }
        });

        setExerciseContent(exerciseId); //show first exercise
    }

    private void saveDay() {
        //Log.d(TAG, "Day number - " + diaryDay.getDayNumber());
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        Log.d(TAG, response);
                        if (!has_error) {
                            getDiary(Integer.parseInt(diaryDay.getDayNumber()) - 1, 0);
                        } else {
                            String message = jsonResponse.getJSONObject("result").getString("message");
                            if (message != null && message.length() > 0) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
        progressDialog.setMessage("Сохранение дня");
        progressDialog.show();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(new DiarySaveDayRequest(User.getSavedApiToken(getActivity()), Integer.parseInt(diaryDay.getDayNumber()),
                responseListener, responseErrorListener));
    }

    private void getDiary(final int dayNumber, final int exerciseId) {
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
                            JSONArray jDiaryDays = jsonResponse.getJSONObject("result").getJSONArray("days");
                            if (jDiaryDays.length() > 0) {
                                onDiaryResponce(dayNumber, DiaryDay.getDiaryDaysAsArray(jDiaryDays), exerciseId);
                            } else {
                                bSaveDay.setVisibility(View.GONE);
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
        progressDialog.setMessage("Загрузка дневника");
        progressDialog.show();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(new DiaryRequest(User.getSavedApiToken(getActivity()), dayNumber,
                responseListener, responseErrorListener));
    }

    @Override
    public void onDetach() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onApproachCheckboxCheckedChangeListener() {
        getDiary(Integer.parseInt(diaryDay.getDayNumber()) - 1, currentExerciseId);
    }

    @Override
    public boolean isDayDone() {
        return diaryDay.isDone();
    }

    @Override
    public String getDayNumber() {
        return diaryDay.getDayNumber();
    }

    @Override
    public DiaryExercise getDiaryCurrentExercise() {
        return diaryDay.getDiaryExercises()[currentExerciseId];
    }

    @Override
    public boolean isForHistory() {
        return false;
    }
}
