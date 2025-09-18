package com.walerider.pingdom.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.R;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = ((MainActivity)requireActivity()).getBottomNavigationView();
        bottomNavigationView.setSelectedItemId(R.id.profileFragment);
    }
}