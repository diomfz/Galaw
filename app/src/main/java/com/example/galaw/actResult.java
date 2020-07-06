package com.example.galaw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class actResult extends AppCompatActivity {

    int type_Question;
    int number_Question;
    TextView total;
    Button simpanData;
    Integer result;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_result);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        total = findViewById(R.id.total);
        simpanData = findViewById(R.id.simpanData);

        type_Question = getIntent().getIntExtra("total",-1);
        number_Question = 0;
        total.setText("" + type_Question);

        simpanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nilai = total.getText().toString();

                sharedPreferences = getApplicationContext().getSharedPreferences("My Pref", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("Point", nilai);
                editor.commit();

                sharedPreferences.getInt("Point", -1);



                Intent intent = new Intent( actResult.this, actAllTest.class );

                startActivity(intent);
            }
        });
    }
}
