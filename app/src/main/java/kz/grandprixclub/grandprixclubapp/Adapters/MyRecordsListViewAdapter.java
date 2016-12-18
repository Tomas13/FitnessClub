package kz.grandprixclub.grandprixclubapp.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import kz.grandprixclub.grandprixclubapp.R;

public class MyRecordsListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView col1;
    TextView col2;

    public static final String COLUMN_1="1";
    public static final String COLUMN_2="2";

    public MyRecordsListViewAdapter(ArrayList<HashMap<String, String>> list,
                                    Activity activity) {
        this.list = list;
        this.activity = activity;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.my_records_table, null);
            col1 = (TextView) convertView.findViewById(R.id.service_name);
            col2 = (TextView) convertView.findViewById(R.id.service_date);

            RecordsHolder holder = new RecordsHolder();
            holder.col1 = col1;
            holder.col2 = col2;

            HashMap<String, String> map = list.get(position);
            col1.setText(map.get(MyRecordsListViewAdapter.COLUMN_1));
            col1.setTextColor(Color.BLACK);
            col2.setText(map.get(MyRecordsListViewAdapter.COLUMN_2));
            col2.setTextColor(Color.BLACK);

            if (position % 2 != 0) {
                convertView.setBackgroundColor(Color.parseColor("#e7eaf0"));
            }

        }

        return convertView;
    }

    static class RecordsHolder {
        public TextView col1, col2;
    }
}
