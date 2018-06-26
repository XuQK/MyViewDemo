package com.github.xuqk.myviewdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.xuqk.myviewdemo.flipborad.FlipboardView;

/**
 * ClassName: FlipboardActivity <br/>
 * PackageName: com.github.xuqk.myviewdemo <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/24/18 9:28 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class FlipboardActivity extends AppCompatActivity {

    private FlipboardView mFlipboardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flipboard);

        mFlipboardView = findViewById(R.id.flip);
        mFlipboardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipboardView.start();
            }
        });
    }
}
