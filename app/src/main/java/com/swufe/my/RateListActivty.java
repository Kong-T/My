package com.swufe.my;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivty extends ListActivity implements Runnable {

    String data[] = {"one", "two", "three"};
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_liat);注释掉布局，因为原来的父类中已经有布局

        List<String> list1 = new ArrayList<String>();
        for (int i = 1; i < 100; i++) {
            list1.add("item" + i);
        }

            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        setListAdapter(adapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    List<String> List2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivty.this, android.R.layout.simple_list_item_1, List2);
                    setListAdapter(adapter);
                    Log.i("handler", "reset list...");
                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void run() {
        //获取网络数据，放入List带回主线程
        Log.i("thread", "run......");
        List<String> rateList = new ArrayList<String>();
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
                rateList.add(tdstr + "=>" + pStr);
                Log.i("td", tdstr + "==>" + pStr);


            }
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(5);

        msg.obj = rateList;
        handler.sendMessage(msg);

        Log.i("thread", "sendMessage......");
    }
}
