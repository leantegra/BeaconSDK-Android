package com.leantegra.wibeat.rangingapidemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Artem Drozd on 19.04.16.
 */
public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView mTextView;

    public TextView mTextView2;

    public TextView mTextView3;

    public TextView mTextView4;

    public ViewHolder(View v) {
        super(v);
        mTextView = (TextView) v.findViewById(R.id.textView);
        mTextView2 = (TextView) v.findViewById(R.id.textView2);
        mTextView3 = (TextView) v.findViewById(R.id.textView3);
        mTextView4 = (TextView) v.findViewById(R.id.textView4);
    }
}
