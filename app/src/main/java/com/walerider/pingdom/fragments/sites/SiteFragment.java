package com.walerider.pingdom.fragments.sites;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.walerider.pingdom.R;
import com.walerider.pingdom.adapters.SiteRecyclerAdapter;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.SiteDTO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiteFragment extends Fragment {
    private String url;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public SiteFragment() {

    }

    public static SiteFragment newInstance(String param1, String param2) {
        SiteFragment fragment = new SiteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site, container, false);
        recyclerView = view.findViewById(R.id.siteRecyclerView);
        progressBar = view.findViewById(R.id.siteProgressBar);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(url != ""){
            getSite();
        }
    }

    private void getSite(){
        new GetSite().getInfo(url);
    }
    private class GetSite{
        int currIndex = 0;
        SiteDTO siteDTO = new SiteDTO();
        public void getInfo(String url) {
            if(currIndex == 1){
                currIndex++;
                recyclerView.setVisibility(View.VISIBLE);
                getInfo(url);
            }
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            API apiService = APIClient.getApi(getContext());

            Call<SiteDTO> call = apiService.getSiteInfo(new SiteDTO(url));
            call.enqueue(new Callback<SiteDTO>() {
                @Override
                public void onResponse(@NonNull Call<SiteDTO> call, @NonNull Response<SiteDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("API", "Success");
                        Log.e("API", response.body().toString());
                        siteDTO.setUrl(response.body().getUrl());
                        siteDTO.setStatus(response.body().getStatus());
                        siteDTO.setResponseTimeMs(response.body().getResponseTimeMs());
                        SiteRecyclerAdapter adapter = new SiteRecyclerAdapter(siteDTO);
                        recyclerView.setAdapter(adapter);
                        currIndex++;
                        getInfo(url);
                    } else {
                        Toast.makeText(getContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<SiteDTO> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Ошибка запроса", t);
                }
            });
        }

    }
}