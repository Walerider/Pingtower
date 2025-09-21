package com.walerider.pingdom.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.api.entitys.SiteDTO;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.RecyclerView;

import com.walerider.pingdom.R;
import com.walerider.pingdom.utils.AnimatedStatusView;

import java.util.List;

public class SitesRecyclerAdapter extends RecyclerView.Adapter<SitesRecyclerAdapter.SitesViewHolder>{
    LayoutInflater inflater;
    List<SiteDTO> siteList;
    NavController navController;
    public SitesRecyclerAdapter(Context context,List<SiteDTO> siteList,NavController navController) {
        this.inflater = LayoutInflater.from(context);
        this.siteList = siteList;
        this.navController = navController;
    }

    @NonNull
    @Override
    public SitesRecyclerAdapter.SitesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.servers_item,parent,false);
        return new SitesRecyclerAdapter.SitesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SitesRecyclerAdapter.SitesViewHolder holder, int position) {
        SitesRecyclerAdapter.SitesViewHolder sitesViewHolder = (SitesRecyclerAdapter.SitesViewHolder)holder;
        sitesViewHolder.siteNameTextView.setText(siteList.get(position).getUrl());
        Log.i("SitesAdapter","working");
        Log.i("site",siteList.get(position).getStatus());
        sitesViewHolder.itemView.setOnClickListener(v ->{
            Bundle bundle = new Bundle();
            bundle.putString("url",siteList.get(position).getUrl());
            bundle.putLong("id",siteList.get(position).getId());
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.sitePingCheckFragment, true)
                    .build();
            navController.navigate(R.id.siteFragment,
                    bundle,
                    navOptions);
        });
        sitesViewHolder.animatedStatusView.setOnlineWithAnimation(siteList.get(position).getStatus().equals("online"));

    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }
    public static class SitesViewHolder extends RecyclerView.ViewHolder {
        public TextView siteNameTextView;
        AnimatedStatusView animatedStatusView;
        SitesViewHolder(View view){
            super(view);
            siteNameTextView = view.findViewById(R.id.siteNameTextView);
            animatedStatusView = view.findViewById(R.id.statusView);
        }
    }
}

