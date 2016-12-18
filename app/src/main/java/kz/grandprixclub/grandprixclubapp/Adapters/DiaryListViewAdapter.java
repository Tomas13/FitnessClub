package kz.grandprixclub.grandprixclubapp.Adapters;


import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryApproach;
import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryExercise;
import kz.grandprixclub.grandprixclubapp.R;
import kz.grandprixclub.grandprixclubapp.ServerRequests.DiaryCheckRequest;
import kz.grandprixclub.grandprixclubapp.Models.User;

public class DiaryListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView col1;
    EditText col2, col3, col4, col5;
    CheckBox col6;
    DiaryListViewAdapterListener mListener;

    public DiaryListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list,
                                DiaryListViewAdapterListener mListener) {
        super();
        this.activity = activity;
        this.list = list;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final ApproachHolder holder = (ApproachHolder) buttonView.getTag();
            //int positionApproach = (int) buttonView.getTag();
            //DiaryApproach diaryApproach = mListener.getDiaryCurrentExercise().getDiaryApproaches()[positionApproach];
            final ProgressDialog progressDialog = new ProgressDialog(activity);

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            if (mListener != null) {
                                mListener.onApproachCheckboxCheckedChangeListener();
                            }
                        } else {
                            String message = jsonResponse.getJSONObject("result").getString("message");
                            if (message != null && message.length() > 0) {
                                if (holder.col6.isChecked()) {
                                    holder.col6.setChecked(false);
                                }
                                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
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
                    if (holder.col6.isChecked()) {
                        holder.col6.setChecked(false);
                    }
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }
            };


            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Пожалуйста подождите");
            progressDialog.show();
            if (isChecked) {
                Volley.newRequestQueue(activity).add(new DiaryCheckRequest(User.getSavedApiToken(activity),
                        Integer.parseInt(mListener.getDayNumber()), holder.diaryApproach.getApproachId(), holder.col2.getText().toString(), holder.col3.getText().toString(),
                        holder.col4.getText().toString(), holder.col5.getText().toString(),
                        responseListener, responseErrorListener));
            } else {
                Volley.newRequestQueue(activity).add(new DiaryCheckRequest(User.getSavedApiToken(activity),
                        Integer.parseInt(mListener.getDayNumber()), holder.diaryApproach.getApproachId(), responseListener, responseErrorListener));
            }


        }
    };

    static class ApproachHolder {
        public EditText col2;
        public EditText col3;
        public EditText col4;
        public EditText col5;
        public CheckBox col6;
        public DiaryApproach diaryApproach;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.diary_table, null);
            col1 = (TextView) convertView.findViewById(R.id.approache);
            col2 = (EditText) convertView.findViewById(R.id.ap_weight);
            col3 = (EditText) convertView.findViewById(R.id.repeat);
            col4 = (EditText) convertView.findViewById(R.id.recovery);
            col5 = (EditText) convertView.findViewById(R.id.strech);
            col6 = (CheckBox) convertView.findViewById(R.id.is_maded);

            ApproachHolder holder = new ApproachHolder();
            holder.col2 = col2;
            holder.col3 = col3;
            holder.col4 = col4;
            holder.col5 = col5;
            holder.col6 = col6;
            holder.diaryApproach = mListener.getDiaryCurrentExercise().getDiaryApproaches()[position];;

            HashMap<String, String> map = list.get(position);
            col1.setText(map.get(DiaryApproach.COLUMN_1));
            col2.setText(map.get(DiaryApproach.COLUMN_2));
            col3.setText(map.get(DiaryApproach.COLUMN_3));
            col4.setText(map.get(DiaryApproach.COLUMN_4));
            col5.setText(map.get(DiaryApproach.COLUMN_5));
            col6.setTag(holder);

            if (mListener.isDayDone()) {
                col6.setEnabled(false);
                if (!mListener.isForHistory()) {
                    col2.setEnabled(false);
                    col3.setEnabled(false);
                    col4.setEnabled(false);
                    col5.setEnabled(false);
                } else {
                    col2.setFocusable(false);
                    col3.setFocusable(false);
                    col4.setFocusable(false);
                    col5.setFocusable(false);
                }

            }
            if (map.get(DiaryApproach.COLUMN_6).equals("true")) {
                col6.setChecked(true);
                if (!mListener.isForHistory()) {
                    col2.setEnabled(false);
                    col3.setEnabled(false);
                    col4.setEnabled(false);
                    col5.setEnabled(false);
                    convertView.setBackgroundResource(R.drawable.cell_vacant_shape);
                } else {
                    col6.setFocusable(false);
                    col2.setFocusable(false);
                    col3.setFocusable(false);
                    col4.setFocusable(false);
                    col5.setFocusable(false);
                }
            } else {
                //col6.setChecked(false);
            }
            col6.setOnCheckedChangeListener(listener);
        }



        return convertView;
    }

    public interface DiaryListViewAdapterListener {
        void onApproachCheckboxCheckedChangeListener();
        boolean isDayDone();
        String getDayNumber();
        DiaryExercise getDiaryCurrentExercise();
        boolean isForHistory();
    }
}
