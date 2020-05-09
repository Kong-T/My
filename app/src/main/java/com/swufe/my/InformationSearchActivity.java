package com.swufe.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class InformationSearchActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener, View.OnClickListener {

    //用变量去存储，方便后续修改；
    private final String TAG = "InformationSearch";
    private String text = "";
    private long currentTime = 0;
    private long savetime = 0;
    int k = 0;

    EditText input;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_search);
        final ListView listview = (ListView) findViewById(R.id.search_list);

        //周期
        SharedPreferences sharedPreferences = getSharedPreferences("info", Activity.MODE_PRIVATE);
        savetime = sharedPreferences.getLong("save_time", Long.parseLong("0"));

        //获取当前系统时间
        final long currentTime = System.currentTimeMillis();
        long s = (currentTime - savetime) / (1000 * 60 * 60 * 24);

        Log.i(TAG, "OnCreate: sp updateDate  = " + savetime);
        Log.i(TAG, "OnCreate: sp currentTime  = " + currentTime);
        Log.i(TAG, "OnCreate: s  = " + s);
        Log.i(TAG, "OnCreate: todayStr = " + savetime);

        //判断时间
        if (s > 7) {
            Log.i(TAG, "onCreate:需要更新");
            //开启子线程
            Thread t = new Thread((Runnable) this);
            t.start();
        } else {
            Log.i(TAG, "onCreate:不需要更新");
        }

        Thread t = new Thread((Runnable) this);
        t.start();

                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 5) {
                            List<String> List2 = (List<String>) msg.obj;
                            if (List2.size() == 0) {
                                Toast.makeText(InformationSearchActivity.this, "无匹配结果", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, String.valueOf(List2));
                            } else {
                                Log.i(TAG, String.valueOf(List2));
                            }

                            ListAdapter adapter = new ArrayAdapter<String>(InformationSearchActivity.this, android.R.layout.simple_list_item_1, List2);
                            listview.setAdapter(adapter);
//                            if (listview.getCount() == 0) {
//                                Toast.makeText(InformationSearchActivity.this, "无匹配结果", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.i(TAG, String.valueOf(listview));
//                            }

                            //用SP保存时间
                            SharedPreferences sharedPreferences = getSharedPreferences("info", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putLong("save_time", currentTime);
                            editor.apply();
                        }
                        super.handleMessage(msg);
                    }
                };
        listview.setOnItemClickListener((AdapterView.OnItemClickListener) this);
    }


    @Override
    public void run() {
        //获取网络数据，放入List带回主线程
        Log.i("thread", "run......");
//        final List<String> rateList = new ArrayList<String>();
        try {

            Thread.sleep(3000);
            SharedPreferences sharedPreferences = getSharedPreferences("info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Document doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Elements tbs = doc.getElementsByTag("ul");
            Element table = tbs.get(17);
            //获取网页链接
            Elements links = table.getElementsByTag("a");
            int j = 1;
            String Url;
            for (Element link : links) {
                String linkhref = link.attr("href");
                Url = "https://it.swufe.edu.cn/".concat(linkhref);
                Log.i(TAG, "RUN:url[" + j + "]" + Url);
                j+=2;
                editor.putString(String.valueOf(j), Url);
                editor.commit();
            }

            Elements tds = table.getElementsByTag("span");
            k = tds.size();
            //查找标题
            for (int i = 0; i < k; i += 2) {
                SharedPreferences sp = getSharedPreferences("info", Activity.MODE_PRIVATE);
                SharedPreferences.Editor edi = sp.edit();
                Element td = tds.get(i);
                final String tdstr = td.text();
                Log.i("span", tdstr);
                edi.putString(String.valueOf(i), tdstr);
                edi.commit();
            }
            contain();

        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }
    }

    //包含和传值
    private void contain() {
        final SharedPreferences sp = getSharedPreferences("info", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor edi = sp.edit();
        final List<String> infoList = new ArrayList<String>();
        //判断标题中是否包含关键词
        input = (EditText) findViewById(R.id.search_inp);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                infoList.clear();
                for (int i = 0; i <= k + 1; i++) {
                    String title = sp.getString(String.valueOf(i), "");
                    if (title.contains(s)) {
                        infoList.add(title);
                        Log.i("thread", "包含：" + title);
                    } else {
                        Log.i("thread", "不包含：");
                    }
                }
            }
        });
        Message msg = handler.obtainMessage(5);

        msg.obj = infoList;
        handler.sendMessage(msg);

        Log.i("thread", "sendMessage......");
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick:parents = " + parent);
        Log.i(TAG, "onItemClick:view = " + view);
        Log.i(TAG, "onItemClick:position = " + position);
        Log.i(TAG, "onItemClick:id = " + id);
        SharedPreferences sharedPreferences = getSharedPreferences("info", Activity.MODE_PRIVATE);
        String Position = String.valueOf(position+1);
        String URL = sharedPreferences.getString(Position, "");
        Log.i("TAG", "run=" + URL);
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(web);
    }


    @Override
    public void onClick(View v) {

    }
}

