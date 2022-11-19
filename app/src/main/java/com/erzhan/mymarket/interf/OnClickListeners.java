package com.erzhan.mymarket.interf;

import com.erzhan.mymarket.data.models.Software;

public class OnClickListeners {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnSoftwareItemClickListener {
        void onItemClick(Software software);
    }

}
