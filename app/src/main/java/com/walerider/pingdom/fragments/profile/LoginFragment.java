package com.walerider.pingdom.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.walerider.pingdom.MainActivity;
import com.walerider.pingdom.R;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.UserDTO;
import com.walerider.pingdom.utils.TokenStorage;
import com.walerider.pingdom.utils.UserData;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private Button regButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.signupButton);

        regButton = view.findViewById(R.id.regButton);

        regButton.setOnClickListener(v -> {
            // Получаем NavController
            NavController navController = Navigation.findNavController(view);
            navController.navigate(
                    R.id.registerFragment,
                    null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build(),
                    null
            );
        });

        loginButton.setOnClickListener(v -> {

            try {
                validateInput(new ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid) {
                        if(isValid){
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigate(
                                    R.id.profileFragment,
                                    null,
                                    new NavOptions.Builder()
                                            .setPopUpTo(R.id.loginFragment, true)
                                            .build(),
                                    null
                            );
                        }
                    }
                });
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

    private void validateInput(ValidationCallback callback) throws NoSuchAlgorithmException, KeyManagementException {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            usernameEditText.setError("Введите логин");
            callback.onValidationResult(false);
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Введите пароль");
            callback.onValidationResult(false);
            return;
        }
        API api = APIClient.getApi(getContext());
        Call<UserDTO> call = api.login(new UserDTO(email, password));
        Log.i("call pingdom",email + password);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {

                    UserDTO user = response.body();

                    // 💾 Сохраняем данные пользователя и токен
                    Context context = getContext();
                    if (context != null) {
                        UserData.setString("name", user.getUser().getName());
                        UserData.setString("email", user.getUser().getEmail());
                        UserData.setBoolean("isLogin", true);
                        if (user.getToken() != null) {
                            TokenStorage.saveToken(user.getToken());
                        } else {
                            Log.w("LoginFragment", "Токен отсутствует в ответе API");
                        }
                    }

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(
                            R.id.profileFragment,
                            null,
                            new NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true)
                                    .build(),
                            null
                    );
                    Log.e("id",response.body().toString());
                } else {
                    Log.e("Retrofit", "Ошибка: " + response.code());
                    Toast.makeText(getContext(), "Логин или пароль введены неправильно", Toast.LENGTH_SHORT).show();
                    callback.onValidationResult(false);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

                Log.e("Retrofit", "Ошибка сети: " + t.getMessage());
                Log.e("Retrofit", "Ошибка сети: " + t.getMessage());

            }
        });

    }
    public interface ValidationCallback {
        void onValidationResult(boolean isValid);
    }
    private void writeInShared(int id){
        /*UserData.setString("username",usernameEditText.getText().toString().trim());
        UserData.setString("password",passwordEditText.getText().toString().trim());
        UserData.setBoolean("isLogin",true);
        UserData.setInteger("id",id);*/
    }
    /*private void navigateToProfile() {


        navController.navigate(
                        R.id.profileFragment,
                null,
                new NavOptions.Builder()
                                .setPopUpTo(R.id.loginFragment, true)
                                .build(),
                null
                );
    }*/
}