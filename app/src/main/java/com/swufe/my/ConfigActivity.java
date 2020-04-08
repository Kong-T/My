package com.swufe.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    public final String TAG = "ConfigActivity";

    //获取三个控件
    EditText dollarText;
    EditText euroText;
    EditText wonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //如何判断页面有无获取到 通过getIntent
        Intent intent = getIntent();
        //之前放的是一个float类型的数据，取出来也是通过这个方法
        //getFloatExtra中第一个值是之前我们赋的标签值，需和之前的命名一样，如果不一样的话，我们取不到值，取不到的话，程序会返回第二个值。
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);

        Log.i(TAG,"onCreate:dolllar2="+dollar2);
        Log.i(TAG,"onCreate:euro2="+euro2);
        Log.i(TAG,"onCreate:won2="+won2);

        dollarText = findViewById(R.id.dollar_rate);
        euroText = findViewById(R.id.euro_rate);
        wonText = findViewById(R.id.won_rate);

        //显示数据到控件
        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));

    }

    public void save(View btn){
        Log.i("cfg","save:");

        //获取新的值
        float newDollar = Float.parseFloat(dollarText.getText().toString());
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());

        Log.i(TAG,"save:获取到新的值");
        Log.i(TAG,"onCreate:newDollar="+newDollar);
        Log.i(TAG,"onCreate:newEuro="+newEuro);
        Log.i(TAG,"onCreate:newWon="+newWon);

        //保存到Bundle或者放入到Extra
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);//2是我们的响应代码

        //返回到调用界面
        finish();

    }
}
