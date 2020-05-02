package com.swufe.my;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private final String TAG = "second";

    TextView score;
    TextView score2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i(TAG,"onCreate:");

        score  = findViewById(R.id.score);
        score2  = findViewById(R.id.score_b);

    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        String scorea =  score.getText().toString();
        String scoreb = score2.getText().toString();

        Log.i(TAG,"onSaveInstanceState:");
        outState.putString("teama_score",scorea);
        outState.putString("teamb_score",scoreb);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String scorea  = savedInstanceState.getString("teama_score");
        String scoreb  = savedInstanceState.getString("teamb_score");

        Log.i(TAG,"onRestoreInstanceState:");
        score.setText(scorea);
        score2.setText(scoreb);

    }

    protected void onStart(){
        super.onStart();
        Log.i(TAG,"onStart:");
    }

    protected void onResume(){
        super.onResume();
        Log.i(TAG,"onResume:");
    }

    protected void onRestart(){
        super.onRestart();
        Log.i(TAG,"onRestart:");
    }

    protected void onPause(){
        super.onPause();
        Log.i(TAG,"onPause:");
    }

    protected void onStop(){
        super.onStop();
        Log.i(TAG,"onStop:");
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG,"onDestroy:");
    }

    public void btnadd1(View btn){
        if(btn.getId()==R.id.btm_1) {
            showScore(1);
        }else{
            showScore2(1);
        }
    }

    public void btnadd2(View btn){
        if (btn.getId()==R.id.btn_2) {
            showScore(2);
        }else{
            showScore2(2);
        }
    }

    public void btnadd3(View btn){
        if(btn.getId()==R.id.btn_3) {
            showScore(3);
        }else{
            showScore2(3);
        }
    }

    public void btnReset(View btn){
        score.setText("0");
        score2.setText("0");
    }

    private void showScore(int inc) {
        String oldScore = (String) score.getText();
        int newScore = Integer.parseInt(oldScore) + inc;
        score.setText("" + newScore);
    }
    private void showScore2(int inc) {
        Log.i("show","inc="+inc);
        String oldScore = (String) score2.getText();
        int newScore = Integer.parseInt(oldScore) + inc;
        score2.setText("" + newScore);
    }
}
