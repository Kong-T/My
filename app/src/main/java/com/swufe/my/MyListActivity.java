package com.swufe.my;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String TAG ="mylist";
    ArrayAdapter adapter;
    List<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        ListView listview = (ListView) findViewById(R.id.mylist);
        for (int i =0;i<=10;i++){
            data.add("item"+i);
        }

         adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
         listview.setAdapter(adapter);
         listview.setEmptyView(findViewById(R.id.no_data));

         listview.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> listv, View view, int position, long id) {
        Log.i(TAG,"onItemClick:position"+position);
        Log.i(TAG,"onItemClick:parent"+listv);
        //列表是通过方法的参数获得的
        adapter.remove(listv.getItemAtPosition(position));
    }
}
