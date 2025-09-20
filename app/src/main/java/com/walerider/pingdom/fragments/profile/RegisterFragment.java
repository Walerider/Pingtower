package com.walerider.pingdom.fragments.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class RegisterFragment extends Fragment {

    private EditText usernameEditText, emailEditText, passwordEditText,repeatPasswordEditText;
    private Button signupButton, loginButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        signupButton = view.findViewById(R.id.signupButton);
        loginButton = view.findViewById(R.id.loginButton);
        repeatPasswordEditText = view.findViewById(R.id.repeatPasswordEditText);
        signupButton.setOnClickListener(v -> attemptRegistration());

        loginButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);

            // Создаем NavOptions для очистки стека
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.registerFragment, true) // Удаляем текущий фрагмент из стека
                    .build();

            navController.navigate(
                    R.id.loginFragment,
                    null,
                    navOptions
            );
        });

        return view;
    }

    private void attemptRegistration() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirmation = repeatPasswordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("Введите логин");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Введите корректный email");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Пароль должен содержать минимум 6 символов");
            return;
        }
        if(!password.equals(repeatPasswordEditText.getText().toString())){
            passwordEditText.setError("Пароли не совпадают");
            repeatPasswordEditText.setError("Пароли не совпадают");
            return;
        }
        try {
            registerUser(username, email, password,passwordConfirmation);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = ((MainActivity)requireActivity()).getBottomNavigationView();
        if(bottomNavigationView.getSelectedItemId() != R.id.profileFragment){
            bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        }
    }
    private void registerUser(String username, String email, String password,String passwordConfirmation) throws NoSuchAlgorithmException, KeyManagementException {

        API apiService = APIClient.getApi(getContext());
        UserDTO user = new UserDTO(username,email,password,passwordConfirmation);
        Call<UserDTO> call = apiService.register(user);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                    UserDTO result = response.body();
                    UserData.setString("username",result.getUser().getName());
                    UserData.setString("email",result.getUser().getEmail());
                    UserData.setBoolean("isLogin",true);
                    UserData.setLong("id", result.getUser().getId());
                    TokenStorage.saveToken(result.getToken());
                    Log.e("id", String.valueOf(result.getId()));
                    Toast.makeText(getContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireView());
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.profileFragment, true) // Очищаем стек до registerFragment
                            .build();

                    navController.navigate(
                            R.id.profileFragment, // ID вашего фрагмента авторизации
                            null,
                            navOptions
                    );

                } else {
                    Log.e("Retrofit", "Ошибка: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                /*if(!call.isCanceled()){
                    Toast.makeText(getContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireView());
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.registerFragment, true) // Очищаем стек до registerFragment
                            .build();
                    navController.navigate(
                            R.id.profileFragment, // ID вашего фрагмента авторизации
                            null,
                            navOptions
                    );
                }
                Log.e("Retrofit", "Network bug: " + t.getMessage());
                Log.e("Retrofit", "Network bug: " + call.isCanceled());*/
            }
        });

    }
}