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
        //registerUser(username, email, password);
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = ((MainActivity)requireActivity()).getBottomNavigationView();
        if(bottomNavigationView.getSelectedItemId() != R.id.profileFragment){
            bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        }
    }
    /*private void registerUser(String username, String email, String password) {

        API apiService = APIClient.getApi();
        UserPOJO user = new UserPOJO(username,email,password);
        Call<String> call = apiService.registerUser(user);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    String result = response.body();
                    UserData.setString("username",username);
                    UserData.setString("password",password);
                    UserData.setBoolean("isLogin",true);
                    UserData.setInteger("id", Integer.parseInt(result));
                    Log.e("id",result);
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

                } else {
                    Log.e("Retrofit", "Ошибка: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(!call.isCanceled()){
                    Toast.makeText(getContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireView());
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.registerFragment, true) // Очищаем стек до registerFragment
                            .build();
                    UserData.setString("username",username);
                    UserData.setString("password",password);
                    navController.navigate(
                            R.id.profileFragment, // ID вашего фрагмента авторизации
                            null,
                            navOptions
                    );
                }
                Log.e("Retrofit", "Network bug: " + t.getMessage());
                Log.e("Retrofit", "Network bug: " + call.isCanceled());
            }
        });

    }*/
}