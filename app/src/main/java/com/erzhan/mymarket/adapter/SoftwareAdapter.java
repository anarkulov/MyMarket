package com.erzhan.mymarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erzhan.mymarket.R;
import com.erzhan.mymarket.data.models.Software;
import com.erzhan.mymarket.ext.CommonExt;
import com.erzhan.mymarket.ext.ViewExt;
import com.erzhan.mymarket.interf.OnClickListeners;

import java.util.List;

public class SoftwareAdapter extends RecyclerView.Adapter<SoftwareAdapter.SoftwareViewHolder> {

    private List<Software> softwareList;
    private OnClickListeners.OnSoftwareItemClickListener onItemClickListener;

    public SoftwareAdapter(List<Software> softwareList, OnClickListeners.OnSoftwareItemClickListener onItemClickListener) {
        this.softwareList = softwareList;
        this.onItemClickListener = onItemClickListener;
    }

    public class SoftwareViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo, ivStatus;
        TextView tvTitle, tvDescription;

        public SoftwareViewHolder(@NonNull View itemView) {
            super(itemView);

            ivLogo = itemView.findViewById(R.id.iv_logo);
            ivStatus = itemView.findViewById(R.id.iv_status);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }

    @NonNull
    @Override
    public SoftwareAdapter.SoftwareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.software_item, parent, false);
        return new SoftwareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoftwareAdapter.SoftwareViewHolder holder, int position) {
        Software softwareItem = softwareList.get(holder.getAdapterPosition());

        if (CommonExt.isPackageInstalled(softwareItem.getType(), holder.itemView.getContext().getPackageManager())) {
            holder.ivStatus.setImageResource(R.drawable.ic_installed);
        } else if (CommonExt.isFileDownloaded(softwareItem.getLink())) {
            holder.ivStatus.setImageResource(R.drawable.ic_downloaded);
        } else if (CommonExt.isVersionHigher(softwareItem.getType(), holder.itemView.getContext().getPackageManager(), softwareItem.getAppVersion())) {
            holder.ivStatus.setImageResource(R.drawable.ic_update);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_download);
        }

        ViewExt.loadUrl(holder.ivLogo, softwareItem.getLogo50Link());
        holder.tvTitle.setText(softwareItem.getTitle());
        holder.tvDescription.setText(softwareItem.getDescription());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(softwareItem.getType()));
    }

    @Override
    public int getItemCount() {
        return softwareList != null ? softwareList.size() : 0;
    }
}
