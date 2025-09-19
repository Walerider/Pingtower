package com.walerider.pingdom.fragments.checking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.R;

public class SitePingCheckFragment extends Fragment {
    EditText urlEditText;
    MaterialButton searchButton;
    public SitePingCheckFragment() {
    }


    public static SitePingCheckFragment newInstance(String param1, String param2) {
        SitePingCheckFragment fragment = new SitePingCheckFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_ping_check, container, false);
        urlEditText = view.findViewById(R.id.urlEditText);
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            NavController navController = ((MainActivity)requireActivity()).getNavController();
            Bundle bundle = new Bundle();
            bundle.putString("url",urlEditText.getText().toString());
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.sitePingCheckFragment, true)
                    .build();
            navController.navigate(R.id.siteFragment,
                    bundle,
                    navOptions);
        });
        return view;
    }
}
