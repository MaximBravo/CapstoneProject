package com.maximbravo.chongo3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Maxim Bravo on 8/4/2017.
 */

public class HistoryAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private Map<String, String> history;
    private ArrayList<String> dates;
    private ArrayList<String> values;
    private Context context;

    public HistoryAdapter(Context context, Map<String, String> history) {
        this.context = context;
        this.history = history;
        splitIntoDatesAndValues();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void splitIntoDatesAndValues() {
        dates = new ArrayList<>();
        values = new ArrayList<>();
        for(String key : history.keySet()) {

            dates.add(proccessed(key));
            values.add(history.get(key));
        }
    }

    private String proccessed(String key) {
        String processedKey = key;
        processedKey = processedKey.replace('-', '/');
        String minute = processedKey.substring(processedKey.lastIndexOf('/') + 1, processedKey.length());
        processedKey = processedKey.substring(0, processedKey.lastIndexOf('/'));
        String hour = processedKey.substring(processedKey.lastIndexOf('/') + 1, processedKey.length());
        processedKey = processedKey.substring(0, processedKey.lastIndexOf('/'));
        String date = processedKey;
        int minuteInt = Integer.parseInt(minute);
        if(minuteInt < 10) {
            minute = "0"+minute;
        }
        return date + " @"+hour+":"+minute;
    }

    @Override
    public int getCount() {
        return history.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.history_list_item, null);

        TextView keyText = (TextView) view.findViewById(R.id.key);
        keyText.setText(dates.get(position));

        TextView valueText = (TextView) view.findViewById(R.id.value);
        valueText.setText(values.get(position));

        return view;
    }
}
