package com.walerider.pingdom.fragments.sites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.R;
import com.walerider.pingdom.adapters.SitesRecyclerAdapter;
import com.walerider.pingdom.api.entitys.SiteDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
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
        List<SiteDTO> sitesList = new ArrayList<>();
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setUrl("noodlemagazine.com");
        siteDTO.setStatus("online");
        SiteDTO siteDTO2 = new SiteDTO();
        siteDTO2.setUrl("noodlemagazine.com");
        siteDTO2.setStatus("offline");
        sitesList.add(siteDTO);
        sitesList.add(siteDTO2);
        Log.i("sitesList", Arrays.toString(sitesList.toArray()));
        recyclerView = view.findViewById(R.id.sitesRecyclerView);
        SitesRecyclerAdapter adapter = new SitesRecyclerAdapter(getContext(),sitesList);
        recyclerView.setAdapter(adapter);
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
}