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
import com.erzhan.mymarket.utils.DownloadHelper;
import com.erzhan.mymarket.ext.ViewExt;
import com.erzhan.mymarket.interf.OnClickListeners;

import java.util.List;
import java.util.Objects;

public class SoftwareAdapter extends RecyclerView.Adapter<SoftwareAdapter.SoftwareViewHolder> {

    private List<Software> softwareList;
    private OnClickListeners.OnSoftwareItemClickListener onItemClickListener;

    public SoftwareAdapter(List<Software> softwareList, OnClickListeners.OnSoftwareItemClickListener onItemClickListener) {
        this.softwareList = softwareList;
        this.onItemClickListener = onItemClickListener;
    }

    public class SoftwareViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo, ivStatus;
        TextView tvTitle, tvDescription, tvStatus;

        public SoftwareViewHolder(@NonNull View itemView) {
            super(itemView);

            ivLogo = itemView.findViewById(R.id.iv_logo);
            ivStatus = itemView.findViewById(R.id.iv_status);
            tvStatus = itemView.findViewById(R.id.tv_status);
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

        if (DownloadHelper.isPackageInstalled(softwareItem.getPackageName(), holder.itemView.getContext().getPackageManager())) {
            if (DownloadHelper.isVersionHigher(softwareItem.getPackageName(), holder.itemView.getContext().getPackageManager(), softwareItem.getAppVersion())) {
                holder.ivStatus.setImageResource(R.drawable.ic_update);
                holder.tvStatus.setText(R.string.update);
            } else {
                holder.ivStatus.setImageResource(R.drawable.ic_installed);
                holder.tvStatus.setText(R.string.installed);
            }
        } else if (DownloadHelper.isFileDownloaded(softwareItem.getLink(), holder.itemView.getContext())) {
            holder.ivStatus.setImageResource(R.drawable.ic_downloaded);
            holder.tvStatus.setText(R.string.downloaded);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_download);
            holder.tvStatus.setText(R.string.download);
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
