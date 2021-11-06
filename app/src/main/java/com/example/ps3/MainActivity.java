package com.example.ps3;


import static java.lang.String.valueOf;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    EditText loginField, passwordField;
    Button loginButton;

    static DataBaseHelper baza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        loginField = findViewById(R.id.login);
        passwordField = findViewById(R.id.password);
        DataBaseHelper dbHelper = new DataBaseHelper(MainActivity.this);
        loginButton.setOnClickListener(v -> {

            String loginValue =  valueOf(loginField.getText());
            String passwordValue = valueOf(passwordField.getText());


///////////////////// przeniosłem dodawanie urzytkowników do Main
            baza = new DataBaseHelper(this);

            if(baza.checkUserIsExist("Admin")==false) {
                try {
                    baza.createUser("Admin", "cisco", true);
                    baza.createUser("user", "12345", false);
                    baza.createUser("monitor", "1qaz", false);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Log.d("Users", "dodano admina");
            }else Log.d("Users", " nie dodano");
//////////////////////



            if (loginValue.isEmpty() || passwordValue.isEmpty()) {
                new AlertDialog.Builder(this).setMessage("Wpisz login i hasło!").show();
            } else {

                try {
                    Map<String, Boolean> userVerificationResult = dbHelper.findUser(loginValue, passwordValue);

                    if (Objects.equals(userVerificationResult.get("Authorised"), true) && Objects.equals(userVerificationResult.get("isAdmin"), false)) {
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("login", loginValue);
                        startActivity(intent);
                    } else
                        if (Objects.equals(userVerificationResult.get("Authorised"), true) && Objects.equals(userVerificationResult.get("isAdmin"), true)) {
                            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("login", loginValue + " zalogowales sie na konto admina!");
                            startActivity(intent);
                        } else {
                            new AlertDialog.Builder(this).setMessage("Nieprawidłowy login lub hasło!").show();
                        }
                    } catch(NoSuchAlgorithmException e){
                        e.printStackTrace();
                    }

            }
        });
    }
}