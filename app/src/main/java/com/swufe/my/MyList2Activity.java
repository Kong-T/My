package com.swufe.my;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, View.OnClickListener {

    String TAG = "mylist2";
    Handler handler;
    private ArrayList<HashMap<String,String>> listItems;//存放文字、图片信息；
    private SimpleAdapter listItemAdapter;//适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();//调用方法
        this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    List<HashMap<String, String>> List2 = (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this,List2,//listItems 数据源
                            R.layout.activity_my_list2,// ListItem的xml布局实现
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[] {R.id.itemtitle,R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                    Log.i("handler", "reset list...");
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
    }

    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "Rate: = " + i);//标题文字
            map.put("ItemDetail", "detail: = " + i);//详情描述
            listItems.add(map);
        }
        //生成适配器的 Item 和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this,listItems,//listItems 数据源
                R.layout.activity_my_list2,// ListItem的xml布局实现
                new String[]{"ItemTitle","ItemDetail"},
                new int[] {R.id.itemtitle,R.id.itemDetail}
                );
    }

    @Override
    public void run() {
        List<HashMap<String,String >> retList = new ArrayList<HashMap<String, String>>();
        try {
            Thread.sleep(3000);
            Document doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();

            Elements tbs = doc.getElementsByTag("table");

            Element table = tbs.get(1);

            Elements tds = table.getElementsByTag("td");
            //提取币种和折算价
            for (int i = 0; i < tds.size(); i += 8) {
                Element td = tds.get(i);
                Element td2 = tds.get(i + 5);
                String tdstr = td.text();
                String pStr = td2.text();
                Log.i("td", tdstr + "==>" + pStr);
                HashMap<String,String> map  = new HashMap<String, String>();
                map.put("ItemTitle", tdstr);//标题文字
                map.put("ItemDetail", pStr);//详情描述
                retList.add(map);

            }
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(5);

        msg.obj = retList;
        handler.sendMessage(msg);

        Log.i("thread", "sendMessage......");
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"onItemClick:parents = " +parent);
        Log.i(TAG,"onItemClick:view = " + view);
        Log.i(TAG,"onItemClick:position = " +position);
        Log.i(TAG,"onItemClick:id = " + id);
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG,"onItemClick:titleStr = " + titleStr);
        Log.i(TAG,"onItemClick:detailStr = " + detailStr);

        TextView title  = (TextView) view.findViewById(R.id.itemtitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2  = String.valueOf(title.getText());
        String detail2  = String.valueOf(detail.getText());
        Log.i(TAG,"onItemClick:title2 = " + title2);
        Log.i(TAG,"onItemClick:detail2 = " + detail2);

        //打开新的界面，传输数据
        Intent rateCalc = new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);

    }


    @Override
    public void onClick(View v) {

    }
}
