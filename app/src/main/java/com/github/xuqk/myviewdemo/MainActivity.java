package com.github.xuqk.myviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mButtonLike;
    private Button mButtonRuler;
    private Button mButtonFlip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonLike = findViewById(R.id.btn_like);
        mButtonRuler = findViewById(R.id.btn_ruler);
        mButtonFlip = findViewById(R.id.btn_flip);

        mButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LikeActivity.class));
            }
        });

        mButtonRuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RulerActivity.class));
            }
        });

        mButtonFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FlipboardActivity.class));
            }
        });
        
    }
}
