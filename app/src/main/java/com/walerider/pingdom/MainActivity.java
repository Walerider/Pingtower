package com.walerider.pingdom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.User;
import com.walerider.pingdom.services.PingtowerFirebaseMessagingService;
import com.walerider.pingdom.utils.TokenStorage;
import com.walerider.pingdom.utils.UserData;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;

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
        TokenStorage.init(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("FCM_DEBUG", "Google Play Services недоступны: " + resultCode);
            GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 9000).show();
            return;
        }
        Log.d("FCM_DEBUG", "Google Play Services доступны");
        Log.d("FCM_DEBUG", "Запрашиваем FCM токен...");
        // ... ваш предыдущий код проверки Play Services ...

        Log.d("FCM_DEBUG", "Пытаемся получить FCM токен. Строка ПЕРЕД getInstance().getToken()"); // <<< ДОБАВЬТЕ ЭТО

        bottomNavigationLogic(bottomNavigationView,navController);
        isOnline();
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();
        Log.d("FCM_DEBUG", "Интернет соединение: " + isConnected);
        return isConnected;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    /*private void startFcmService() {
            Intent serviceIntent = new Intent(this, PingtowerFirebaseMessagingService.class);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Для Android 8.0+ используем startForegroundService
                    startForegroundService(serviceIntent);
                } else {
                    // Для старых версий обычный startService
                    startService(serviceIntent);
                }
                Log.d("MainActivity", "FCM service started");
            } catch (Exception e) {
                Log.e("MainActivity", "Failed to start service: ", e);
            }
        }*/
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
    /*private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);

                    try {
                        sendRegistrationToServer(token);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (KeyManagementException e) {
                        throw new RuntimeException(e);
                    }
                });
    }*/
    private void sendRegistrationToServer(String token) throws NoSuchAlgorithmException, KeyManagementException {
        API apiClient = APIClient.getApi(getApplicationContext());
        Call<String> call = apiClient.sendFcmToken(TokenStorage.getToken(),token);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {

                } else {
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }
    public NavController getNavController() {
        return navController;
    }
    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}