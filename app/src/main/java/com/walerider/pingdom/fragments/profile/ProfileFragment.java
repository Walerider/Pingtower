package com.walerider.pingdom.fragments.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.R;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.MessageDTO;
import com.walerider.pingdom.api.entitys.UserDTO;
import com.walerider.pingdom.utils.TokenStorage;
import com.walerider.pingdom.utils.UserData;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    Button exitButton;
    public ProfileFragment() {
    }

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
        exitButton = view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v ->{
            try {
                exit();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = ((MainActivity)requireActivity()).getBottomNavigationView();
        if(bottomNavigationView.getSelectedItemId() != R.id.profileFragment){
            bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        }
    }
    private void exit() throws NoSuchAlgorithmException, KeyManagementException {
        API api = APIClient.getApi(getContext());
        Call<MessageDTO> call = api.logout("Bearer " + TokenStorage.getToken());
        Log.e("token",TokenStorage.getToken());
        call.enqueue(new Callback<MessageDTO>() {
            @Override
            public void onResponse(Call<MessageDTO> call, Response<MessageDTO> response) {
                if (response.code() == 200) {
                    String user = response.body().toString();
                    Log.e("body",user);
                    Context context = getContext();
                    if (TokenStorage.getToken() != null) {
                        TokenStorage.removeToken();
                    } else {
                        Log.w("LoginFragment", "Токен отсутствует в ответе API");
                    }
                    UserData.clearAll();
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(
                            R.id.loginFragment,
                            null,
                            new NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true)
                                    .build(),
                            null
                    );
                } else {
                    Toast.makeText(getContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                    Log.e("body",response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<MessageDTO> call, Throwable t) {
                Log.e("Retrofit", "Ошибка сети: " + t.getMessage());
                Log.e("Retrofit", "Ошибка сети: " + t.getMessage());

            }
        });
    }
}