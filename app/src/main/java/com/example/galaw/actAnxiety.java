package com.example.galaw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class actAnxiety extends AppCompatActivity {

    Button stressTest, cancelStress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_anxiety);

        stressTest = findViewById(R.id.stressTest);
        cancelStress = findViewById(R.id.cancelStress);

        stressTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actAnxiety.this, actQuiz.class);
                intent.putExtra("type_Question" , 1);
                startActivity(intent);
            }
        });

        cancelStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actAnxiety.this, actHome.class);

                startActivity(intent);
            }
        });

    }
}
