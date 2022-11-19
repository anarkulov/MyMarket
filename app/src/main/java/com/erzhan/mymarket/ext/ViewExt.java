package com.erzhan.mymarket.ext;

import android.view.View;

import com.bumptech.glide.Glide;

public class ViewExt {

    public static void isVisible(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public static void loadUrl(View view, String url) {
        if (url == null || url.isEmpty()) return;

        Glide.with(view.getContext())
                .load(url)
                .into((android.widget.ImageView) view);
    }
}
