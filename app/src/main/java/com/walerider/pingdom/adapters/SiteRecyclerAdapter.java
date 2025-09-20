package com.walerider.pingdom.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walerider.pingdom.R;
import com.walerider.pingdom.api.entitys.SiteDTO;
import com.walerider.pingdom.utils.AnimatedStatusView;

public class SiteRecyclerAdapter extends RecyclerView.Adapter<SiteRecyclerAdapter.SiteViewHolder>{
    private SiteDTO siteDTO;
    public SiteRecyclerAdapter() {
    }

    public SiteRecyclerAdapter(SiteDTO siteDTO) {
        this.siteDTO = siteDTO;
    }

    @NonNull
    @Override
    public SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.site_item,parent,false);
        return new SiteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SiteViewHolder holder, int position) {
        if(position == 0){
            holder.signatureNameTextView.setText("Ссылка");
            holder.infoStatusView.setText(siteDTO.getUrl());
        }
        if(position == 1){
            holder.signatureNameTextView.setText("Статус");
            holder.infoStatusView.setText(siteDTO.getStatus());
        }if(position == 2){
            holder.signatureNameTextView.setText("Отклик");
            holder.infoStatusView.setText((siteDTO.getResponse_time_ms() == null ? 0 : siteDTO.getResponse_time_ms()) + "мс");
        }if(position == 3){
            holder.signatureNameTextView.setText("SSL сертификат");
            holder.infoStatusView.setText(siteDTO.getSsl_valid() ? "Действителен" : "Просрочен");
            holder.infoStatusView.setTextColor(siteDTO.getSsl_valid() ? Color.GREEN : Color.RED);
        }if(position == 4){
            holder.signatureNameTextView.setText("Истекает ");
            holder.infoStatusView.setText(siteDTO.getSsl_expires_at().toString());
        }if(position == 5){
            holder.signatureNameTextView.setText("Дней до окончания");
            holder.infoStatusView.setText(siteDTO.getSsl_days_left() + " дней");
        }if(position == 6 && siteDTO.getSsl_error() != null && !siteDTO.getSsl_error().equals("")){
            holder.signatureNameTextView.setText("Ошибка SSL");
            holder.infoStatusView.setText(siteDTO.getSsl_error());
        }
    }

    @Override
    public int getItemCount() {
        if(siteDTO.getSsl_error() != null && !siteDTO.getSsl_error().equals("")){
            return 7;
        }
        return 6;
    }
    public static class SiteViewHolder extends RecyclerView.ViewHolder {
        public TextView signatureNameTextView;
        TextView infoStatusView;
        SiteViewHolder(View view){
            super(view);
            signatureNameTextView = view.findViewById(R.id.signatureTextView);
            infoStatusView = view.findViewById(R.id.infoTextView);
        }
    }
}
