package com.walerider.pingdom.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.walerider.pingdom.api.entitys.SiteDTO;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walerider.pingdom.R;

import java.util.List;

public class SitesRecyclerAdapter extends RecyclerView.Adapter<SitesRecyclerAdapter.SitesViewHolder>{
    LayoutInflater inflater;
    List<SiteDTO> siteList;
    public SitesRecyclerAdapter(Context context,List<SiteDTO> siteList) {
        this.inflater = LayoutInflater.from(context);
        this.siteList = siteList;
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
        /*if(components.get(position).getImage() != null){
            Log.e("image source",components.get(position).getImage());
            Glide.with(catalogViewHolder.itemView)
                    .load(components.get(position).getImage())
                    .into(catalogViewHolder.imageView);
        }else{
            catalogViewHolder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        catalogViewHolder.productNameTextView.setText(components.get(position).getName());
        Log.e("component",components.get(position).getName());
        catalogViewHolder.productDescriptionTextView.setText(components.get(position).getDescription());
        catalogViewHolder.priceTextView.setText("От " + components.get(position).getPrice() + "р");*/
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }
    public static class SitesViewHolder extends RecyclerView.ViewHolder {
        public TextView siteNameTextView;
        SitesViewHolder(View view){
            super(view);
            siteNameTextView = view.findViewById(R.id.siteNameTextView);
        }
    }
}

