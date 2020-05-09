package com.swufe.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class rateActivity extends AppCompatActivity implements Runnable{

    //用变量去存储，方便后续修改；
    private final String TAG ="Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    private String updateDate = "";

    EditText rmb;
    TextView show;
    Handler handler;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);

        //获取SP里的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                //第一个参数是字符串，我们取的文件名；第二个是权限
        dollarRate = sharedPreferences.getFloat("dollar_Rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_Rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_Rate",0.0f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        final String todayStr = sdf.format(today);


        Log.i(TAG,"OnCreate: sp dollarRate = " + dollarRate);
        Log.i(TAG,"OnCreate: sp euroRate = " + euroRate);
        Log.i(TAG,"OnCreate: sp wonRate = " + wonRate);
        Log.i(TAG,"OnCreate: sp updateDate  = "+ updateDate);
        Log.i(TAG,"OnCreate: todayStr = "+ todayStr);

        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG,"onCreate:需要更新");
            //开启子线程
            Thread t = new Thread((Runnable) this);
            t.start();
        }else{
            Log.i(TAG,"onCreate:不需要更新");
        }


        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==5){
                    //5为标识，判断是哪个子线程，5可以自己定义
                    Bundle bdl = (Bundle) msg.obj;//获取msg里的内容
                    // 由于得到的是obj类型，需要进行类型强转，注意要求是可以转换的类型，如果是Bundle或者日期类型，不能被强转，代码不会报错。但是运行时会出错。
                   dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");

                    Log.i(TAG,"handleMessage: dollarRate:"+dollarRate);
                    Log.i(TAG,"handleMessage: euroRate:"+euroRate);
                    Log.i(TAG,"handleMessage: wonRate:"+wonRate);

                    //将新设置的汇率写到SP里
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    //文件名字与上面的一样
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("dollar_Rate",dollarRate);
                    editor.putFloat("euro_Rate",euroRate);
                    editor.putFloat("won_Rate",wonRate);
                    editor.putString("update_date",todayStr);
                    editor.apply();

                    Toast.makeText(rateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();

                    //将消息打印出来，放在show控件
                }
                super.handleMessage(msg);
            }
        };

    }

    public void onClick(View btn){
    //获取用户输入内容
        Log.i(TAG,"onclick:");
        String str = rmb.getText().toString();
        float r =0;
        if(str.length()>0){
            r  = Float.parseFloat(str);
        }else{
            //提示用户输入内容
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG,"onClick: r=" + r);

        if(btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else  if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else{
            show.setText(String.format("%.2f",r*wonRate));
        }

    }

    public void openOne(View btn){
        config();
    }

    private void config() {
        //打开一个新界面
        Intent config = new Intent(this, ConfigActivity.class);
        //我们把Intent传输到config页面时可以把参数放进去然后传到下一个页面
        //putExtra这个方法可以携带附加的数据到下一个页面。
        //这个方法前面是一个标签，后面有很多数据类型，这里我们携带的是float类型的数据。
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        //putExtra不仅能对基本数据类型进行处理，也能对对象进行处理，还有数组，具体的可以等到用的时候再研究

        //为了能知道我们是否把数据成功传输到了下一个页面，我们会加很多输出。
        Log.i(TAG, "openOne:dollar_rate=" + dollarRate);
        Log.i(TAG, "openOne:euro_rate=" + euroRate);
        Log.i(TAG, "openOne:won_rate=" + wonRate);


        //startActivity(config);
        startActivityForResult(config, 1);//1是我们的请求代码
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
        //如果返回值为真，说明当前页面是有菜单项的，如果返回值为假，说明当前页面是没有菜单项的。
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //这里只有一个item，但如果我们有多个，就需要通过id去区分不同的item
        if(item.getItemId()==R.id.menu_set){
            //这里老师演示直接引用之前的打开新窗口的方法。
            // tip:为了避免出现两段相同代码，通过method功能形成了方法，可以直接引用
            config();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表
            Intent list = new Intent(this,MyList2Activity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    protected  void  onActivityResult(int requestCode, int resultCode, Intent data){
        //通过requestcode和resultcode可以确定返回的是我们要的数据
        if(requestCode==1 && resultCode ==2){
//           bdl.putFloat("key_dollar",newDollar);
//          bdl.putFloat("key_euro",newEuro);
//          bdl.putFloat("key_won",newWon);
            //以上为我们对数据的封装方式
            //这里写的是通过Bundle来接受数据，我们也可以通过之前带参数回去的方式来读取数据
//            float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);
//            float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
//            float won2 = intent.getFloatExtra("won_rate_key",0.0f);

            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);

            Log.i(TAG,"openOne:dollar_rate="+dollarRate);
            Log.i(TAG,"openOne:euro_rate="+euroRate);
            Log.i(TAG,"openOne:won_rate="+wonRate);

            //将新设置的汇率写到SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            //文件名字与上面的一样
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_Rate",dollarRate);
            editor.putFloat("euro_Rate",euroRate);
            editor.putFloat("won_Rate",wonRate);
            editor.apply();
            editor.commit();
            Log.i(TAG,"数据已保存到sharedPreferences中");

        }

        super.onActivityResult(requestCode,resultCode,data);
    }


        public void run() {


                //延时效果
            for(int i = 0; i < 3; i++){
                Log.i(TAG, "run:i=" + i);
                try {
                    Thread.sleep(2000);//停止两秒钟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
                //用于保存获取的汇率
                Bundle bundle;

                //将我们定义的message发送到message队列里，之后主线程会有一个方法去检测message

                //获取网络内容
                //        URL url = null;
                //        //在连接网络的过程中，容易出现很多意外，比如网络不好，网络出错等，所以需要异常处理
                //        try {
                //            url = new URL("http://www.usd-cny.com/icbc.htm");
                //            HttpURLConnection http = (HttpURLConnection) url.openConnection();
                //            //获取输入流
                //            InputStream in = http.getInputStream();
                //            //调用输入流转成字符的方法
                //            String html =  inputStream2String(in);
                //            Log.i(TAG,"run:html = " + html);
                //
                //        }catch (MalformedURLException e){
                //            e.printStackTrace();
                //        } catch (IOException e) {
                //            e.printStackTrace();
                //        }

                bundle = getfromBOC();
                //获取Mes对象，用于返回主线程
                Message msg = handler.obtainMessage(5);
                //定义了一个what参数，值为5.
                //        msg.obj = "Hello form run()";
                msg.obj = bundle;
                handler.sendMessage(msg);
            }


//        从bankofchina获取数据

    private Bundle getfromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");
            //通过下面的代码查找我们所需的table的位置
//            int i=0;
//            for(Element table : tables){
//                            Log.i(TAG,"RUN:TABLE[" +i +"]" + table);
//                            i++;
//            }

            //获取TD中的数据
            Element table0 = tables.get(1);
            Log.i(TAG,"run: table1="+table0);
            Elements tds = table0.getElementsByTag("td");
            //提取币种和折算价
            for (int j = 0; j < tds.size(); j += 8) {
                Element td1 = tds.get(j);
                Element td2 = tds.get(j + 5);
                Log.i(TAG, "run:" + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩国元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }
            }
            //            for(Element td: tds){
            //                Log.i(TAG,"run:tds=" + td);
            //                Log.i(TAG,"run:text=" + td.text());
            //                Log.i(TAG,"run:html=" + td.html());
            //
            //            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    //        从bankofchina获取数据

    private Bundle getfromUsdcny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();

            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("Table");
            //通过下面的代码查找我们所需的table的位置
            //            for(Element table : tables){
            //                Log.i(TAG,"RUN:TABLE[" +i +"]" + table);
            //                i++;
            //            }
            Element table0 = tables.get(0);
            //            Log.i(TAG,"run: table6="+table6);
            //获取TD中的数据
            Elements tds = table0.getElementsByTag("td");
            //提取币种和折算价
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG, "run:" + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }
            }
            //            for(Element td: tds){
            //                Log.i(TAG,"run:tds=" + td);
            //                Log.i(TAG,"run:text=" + td.text());
            //                Log.i(TAG,"run:html=" + td.html());
            //
            //            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }



    //我们从网站里获得的是输入流，需要转成字符串形式
    //需抛出异常处理
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer  = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
            return out.toString();
        }

}
