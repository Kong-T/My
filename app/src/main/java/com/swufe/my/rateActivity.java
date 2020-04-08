package com.swufe.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class rateActivity extends AppCompatActivity {

    //用变量去存储，方便后续修改；
    private final String TAG ="Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;

    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);
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


        }

        super.onActivityResult(requestCode,resultCode,data);
    }
}
