package com.example.galaw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class actResult extends AppCompatActivity {

    int type_Question;
    int number_Question;
    TextView total;

    Integer result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_result);

        total = findViewById(R.id.total);



        type_Question = getIntent().getIntExtra("total",-1);
        number_Question = 0;
        total.setText(""+type_Question);

    }
}