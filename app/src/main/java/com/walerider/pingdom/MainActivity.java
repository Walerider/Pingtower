package com.walerider.pingdom;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.walerider.pingdom.utils.UserData;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    protected BottomNavigationView bottomNavigationView;
    protected FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        UserData.init(this);
        bottomNavigationLogic(bottomNavigationView,navController);
    }

    private void bottomNavigationLogic(BottomNavigationView bottomNavigationView,NavController navController){

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.sitePingCheckFragment){
                navController.navigate(R.id.sitePingCheckFragment);
                return true;
            }
            if (item.getItemId() == R.id.profileFragment){
                if(UserData.getBoolean("isLogin")){

                    navController.navigate(R.id.profileFragment);
                    return true;
                }
                navController.navigate(R.id.registerFragment);
                Log.e("navigation", "profile");
                return true;
            }
            if(item.getItemId() == R.id.homeFragment){
                navController.navigate(R.id.homeFragment);
                return true;
            }
            return false;
        });
    }
    public NavController getNavController() {
        return navController;
    }
    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}