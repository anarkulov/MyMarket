package com.erzhan.mymarket.ext;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.erzhan.mymarket.R;

public class ViewExt {

    public static void visible(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public static void loadUrl(View view, String url) {
        if (url == null || url.isEmpty()) {
            Glide.with(view.getContext())
                    .load(R.drawable.ic_image_placeholder)
                    .into((android.widget.ImageView) view);
            return;
        }

        Glide.with(view.getContext())
                .load(url)
                .into((android.widget.ImageView) view);
    }
}
