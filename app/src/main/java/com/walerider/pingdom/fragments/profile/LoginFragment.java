package com.walerider.pingdom.fragments.profile;

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


import com.walerider.pingdom.R;

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
        });

        return view;
    }

    private void validateInput(ValidationCallback callback) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("Введите логин");
            callback.onValidationResult(false);
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Введите пароль");
            callback.onValidationResult(false);
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Пароль должен содержать минимум 6 символов");
            callback.onValidationResult(false);
            return;
        }
        /*API api = APIClient.getApi();
        Call<String> call = api.loginUser(username, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    callback.onValidationResult(true);
                    writeInShared(Integer.parseInt(result));
                    Log.e("id",result);
                } else {
                    Log.e("Retrofit", "Ошибка: " + response.code());
                    Toast.makeText(getContext(), "Логин или пароль введены неправильно", Toast.LENGTH_SHORT).show();
                    callback.onValidationResult(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Retrofit", "Ошибка сети: " + t.getMessage());
                if(!call.isCanceled()){
                    callback.onValidationResult(true);
                }else{
                    callback.onValidationResult(false);
                }
            }
        });*/

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
    private void navigateToProfile() {


        /*navController.navigate(
                        R.id.action_loginFragment_to_profileFragment,
                null,
                new NavOptions.Builder()
                                .setPopUpTo(R.id.loginFragment, true)
                                .build(),
                null
                );*/
    }
}