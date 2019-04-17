package com.smartflyer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WaitTimeListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the list of date
    private final List<String> dateArray;

    //to store the list of wait
    private final List<String> waitArray;

    public WaitTimeListAdapter(Activity context, List<String> dateArrayParam, List<String> waitArrayParam) {
        super(context, R.layout.waittime_list, dateArrayParam);
        this.context = context;
        this.dateArray = dateArrayParam;
        this.waitArray = waitArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.waittime_list, null);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = (TextView) rowView.findViewById(R.id.date);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.wait);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(dateArray.get(position));
        infoTextField.setText(waitArray.get(position));
        return rowView;
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }
}
