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

    //to store the list of wait
    private final List<String> waitArray;

    public WaitTimeListAdapter(Activity context, List<String> waitArrayParam) {
        super(context, R.layout.waittime_list, waitArrayParam);
        this.context = context;
        this.waitArray = waitArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.waittime_list, null);

        //this code gets references to objects in the listview_row.xml file
        TextView infoTextField = (TextView) rowView.findViewById(R.id.wait);

        //this code sets the values of the objects to values from the arrays
        infoTextField.setText(waitArray.get(position));
        return rowView;
    }

    @Override
    public int getCount() {
        return waitArray.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }
}
