package com.walerider.pingdom.fragments.sites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.R;
import com.walerider.pingdom.adapters.SiteRecyclerAdapter;
import com.walerider.pingdom.adapters.SitesRecyclerAdapter;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.SiteDTO;
import com.walerider.pingdom.utils.TokenStorage;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    TextView infoLibraryTextView;
    ProgressBar progressBar;
    List<SiteDTO> sitesList;
    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sitesList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.sitesRecyclerView);
        infoLibraryTextView = view.findViewById(R.id.infoLibraryTextView);
        progressBar = view.findViewById(R.id.progressBar);
        try {
            getLibrarySites();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = ((MainActivity)requireActivity()).getBottomNavigationView();
        if(bottomNavigationView.getSelectedItemId() != R.id.homeFragment){
            bottomNavigationView.setSelectedItemId(R.id.homeFragment);
        }
    }
    private void getLibrarySites() throws NoSuchAlgorithmException, KeyManagementException {
        if(TokenStorage.getToken() != null){
            new GetSites().getLibrary();
            Log.e("token",TokenStorage.getToken());
        }else{

            infoLibraryTextView.setVisibility(View.VISIBLE);
        }
    }
    private class GetSites{
        int currIndex = 0;
        public void getLibrary() throws NoSuchAlgorithmException, KeyManagementException {
            if(currIndex >= 1){
                progressBar.setVisibility(View.GONE);
                if(sitesList.isEmpty()){
                    infoLibraryTextView.setVisibility(View.VISIBLE);
                }
                recyclerView.setVisibility(View.VISIBLE);
                SitesRecyclerAdapter adapter = new SitesRecyclerAdapter(getContext(),sitesList,((MainActivity)requireActivity()).getNavController());
                recyclerView.setAdapter(adapter);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            API apiService = APIClient.getApi(getContext());
            Call<List<SiteDTO>> call = apiService.getUserSites("Bearer " + TokenStorage.getToken());
            call.enqueue(new Callback<List<SiteDTO>>() {
                @Override
                public void onResponse(@NonNull Call<List<SiteDTO>> call, @NonNull Response<List<SiteDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            Log.e("API", "Success");
                            Log.e("API", response.body().toString());
                            Log.e("API", Integer.toString(currIndex));
                            currIndex++;
                            sitesList.addAll(response.body());
                            getLibrary();
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (KeyManagementException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<SiteDTO>> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Ошибка запроса", t);
                }
            });
        }

    }
}