package com.swufe.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapterActivity extends ArrayAdapter {

    private static final String TAG = "MyAdapter";

    public MyAdapterActivity(@NonNull Context context, int resource, ArrayList <HashMap<String,String>>List) {
        super(context, resource,List);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_my_list2,parent,false);
        }
        Map<String,String> map = (Map<String, String>)getItem(position);
        TextView title = (TextView)itemView.findViewById(R.id.itemtitle);
        TextView detail = (TextView)itemView.findViewById(R.id.itemDetail);

        title.setText("Title" + map.get("ItemTitle"));
        detail.setText("detail" + map.get("ItemDetail"));

        return itemView;
    }
}
