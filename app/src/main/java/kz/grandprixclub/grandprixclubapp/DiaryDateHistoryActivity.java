package kz.grandprixclub.grandprixclubapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryExercise;
import kz.grandprixclub.grandprixclubapp.Models.Service;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.DiaryDateHistoryRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.ServicesRequest;

public class DiaryDateHistoryActivity extends AppCompatActivity implements DiaryListViewAdapter.DiaryListViewAdapterListener {

    String date;
    private RequestQueue mRequestQueue;
    private int currentExerciseId;
    DiaryExercise[] diaryExercises;

    ListView listView;

    TextView tvExerciseNav, tvDiaryTitleHistory;
    ImageButton imageButtonLeft;
    ImageButton imageButtonRight;
    View viewListHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                date = null;
            } else {
                date = extras.getString("date");
            }
        } else {
            date = (String) savedInstanceState.getSerializable("date");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_date_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("История дневника");
        listView = (ListView) findViewById(R.id.lv_diary_date_history);

        tvExerciseNav = (TextView) findViewById(R.id.tv_exercise_nav);
        tvDiaryTitleHistory = (TextView) findViewById(R.id.tv_diary_title_history);
        imageButtonLeft = (ImageButton) findViewById(R.id.image_button_left);
        imageButtonRight = (ImageButton) findViewById(R.id.image_button_right);
        viewListHeader = getLayoutInflater().inflate(R.layout.diary_table_head, null);
        listView.addHeaderView(viewListHeader);
        listView.setEmptyView(findViewById(R.id.lv_diary_date_history_empty));

        getHistoryDiaryDate();
    }

    private void getHistoryDiaryDate() {
        final ProgressDialog progressDialog = new ProgressDialog(DiaryDateHistoryActivity.this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                    if (!has_error) {
                        JSONArray jExercises = jsonResponse.getJSONObject("result").getJSONArray("exercises");
                        String title = jsonResponse.getJSONObject("result").getString("title");
                        if (title != null && title.length() > 0) {
                            tvDiaryTitleHistory.setText(title);
                        }
                        if (jExercises.length() > 0) {
                            diaryExercises = DiaryExercise.getDiaryExercisesAsArray(jExercises);
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
                                    if (currentExerciseId < diaryExercises.length - 1) {
                                        setExerciseContent(currentExerciseId + 1);
                                    }
                                }
                            });
                            setExerciseContent(0); //first exercise
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

                progressDialog.dismiss();
                Toast.makeText(DiaryDateHistoryActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка");
        progressDialog.show();
        DiaryDateHistoryRequest d = new DiaryDateHistoryRequest(User.getSavedApiToken(DiaryDateHistoryActivity.this),
                date, responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(DiaryDateHistoryActivity.this);
        mRequestQueue.add(d);

    }

    private void setExerciseContent(int exerciseNumber) {
        currentExerciseId = exerciseNumber;
        DiaryExercise diaryExercise = diaryExercises[currentExerciseId];

        tvExerciseNav.setText("Упражнение " + diaryExercise.getExerciseNumber() +
                "/" + diaryExercises.length);
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

    private void setAdapter(ArrayList<HashMap<String, String>> list) {
        DiaryListViewAdapter adapter = new DiaryListViewAdapter(DiaryDateHistoryActivity.this, list, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onApproachCheckboxCheckedChangeListener() {

    }

    @Override
    public boolean isDayDone() {
        return true; //allways true, because its a history activity
    }

    @Override
    public String getDayNumber() {
        return "0";
    }

    @Override
    public DiaryExercise getDiaryCurrentExercise() {
        return diaryExercises[currentExerciseId];
    }

    @Override
    public boolean isForHistory() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
