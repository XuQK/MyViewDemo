package com.github.xuqk.myviewdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.github.xuqk.myviewdemo.R;
import com.github.xuqk.myviewdemo.like.ScrollNumberView;
import com.github.xuqk.myviewdemo.like.ThumbButton;

/**
 * ClassName: LikeFragment <br/>
 * PackageName: com.github.xuqk.myviewdemo.fragment <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/19/18 7:29 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class LikeFragment extends Fragment {

    private static final String TAG = "LikeFragment";

    private ThumbButton mThumbButton;
    private ScrollNumberView mScrollNumberView;
    private ConstraintLayout mBtnLike;
    private EditText mEditText;
    private Button mButton;

    public static LikeFragment newInstance() {
        return new LikeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        mThumbButton = view.findViewById(R.id.thumb_like);
        mScrollNumberView = view.findViewById(R.id.number_like);
        mBtnLike = view.findViewById(R.id.btn_like);
        mEditText = view.findViewById(R.id.et_like_number);
        mButton = view.findViewById(R.id.btn_set_like_number);

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

        return view;
    }
}
