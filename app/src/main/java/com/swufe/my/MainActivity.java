package com.swufe.my;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView out;
    EditText inp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        out  = (TextView) findViewById(R.id.showtext);
        inp = (EditText)findViewById(R.id.inpText);

        Button btn = (Button)findViewById(R.id.button);

    }

    public void btnClick(View btn){
        showText();
    }

    private void showText() {
        Log.i("show","temperature="+inp.getText().toString());
        String t1 = (String) inp.getText().toString();
        double t2 = Double.parseDouble(t1)*1.8+32;
        out.setText("" + t2);
    }
}
