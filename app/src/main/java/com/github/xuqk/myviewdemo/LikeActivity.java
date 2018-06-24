package com.github.xuqk.myviewdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.xuqk.myviewdemo.like.ScrollNumberView;
import com.github.xuqk.myviewdemo.like.ThumbButton;

/**
 * ClassName: LikeActivity <br/>
 * PackageName: com.github.xuqk.myviewdemo <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/24/18 3:40 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class LikeActivity extends AppCompatActivity {

    private ThumbButton mThumbButton;
    private ScrollNumberView mScrollNumberView;
    private ConstraintLayout mBtnLike;
    private EditText mEditText;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        mThumbButton = findViewById(R.id.thumb_like);
        mScrollNumberView = findViewById(R.id.number_like);
        mBtnLike = findViewById(R.id.btn_like);
        mEditText = findViewById(R.id.et_like_number);
        mButton = findViewById(R.id.btn_set_like_number);

        mBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mThumbButton.isLiked()) {
                    mThumbButton.setLiked(false, true);
                    mScrollNumberView.changeNumber(-1);
                } else {
                    mThumbButton.setLiked(true, true);
                    mScrollNumberView.changeNumber(1);
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollNumberView.setCurrentNumber(mEditText.getText().toString(), true);
            }
        });
    }
}
