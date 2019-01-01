package com.dokiwa.dokidoki.web;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/** 无数据提示 **/
public class NoDataSuit extends LinearLayout {

    private Context mContext;
    private TextView mTips1Text;
    private TextView mTips2Text;


    public NoDataSuit(Context context) {
        super(context);
        mContext = context;
    }


    public NoDataSuit(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }


    private void initView(AttributeSet attrs) {
        LayoutInflater.from(mContext).inflate(R.layout.nodata_suit, this, true);
        mTips1Text = (TextView) findViewById(R.id.tips1_text);
        mTips2Text = (TextView) findViewById(R.id.tips2_text);
        mTips1Text.setVisibility(View.GONE);
        mTips2Text.setVisibility(View.GONE);
        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.NoData);
            String tips1 = a.getString(R.styleable.NoData_nd_tips1);
            String tips2 = a.getString(R.styleable.NoData_nd_tips2);
            if (!TextUtils.isEmpty(tips1)) {
                mTips1Text.setText(tips1);
                mTips1Text.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(tips2)) {
                mTips2Text.setText(tips2);
                mTips2Text.setVisibility(View.VISIBLE);
            }

            a.recycle();
        }
    }



    public void setTips1(String tips) {
        mTips1Text.setText(tips);
        mTips1Text.setVisibility(View.VISIBLE);
    }

    public void setTips1(int tipsResId) {
        mTips1Text.setText(tipsResId);
        mTips1Text.setVisibility(View.VISIBLE);
    }

    public void setTips2(String tips) {
        mTips2Text.setText(tips);
        mTips2Text.setVisibility(View.VISIBLE);
    }

    public void setTips2(int tipsResId) {
        mTips2Text.setText(tipsResId);
        mTips2Text.setVisibility(View.VISIBLE);
    }

}
