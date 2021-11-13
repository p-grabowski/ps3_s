package com.example.ps3;


import static java.lang.String.valueOf;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    EditText loginField, passwordField;
    Button loginButton;
    DataBaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        loginField = findViewById(R.id.login);
        passwordField = findViewById(R.id.password);
        dbHelper = new DataBaseHelper(this);

///////////////////// przeniosłem dodawanie urzytkowników do Main
        if(dbHelper.checkUserIsExist("admin")) Log.d("Users", " nie dodano");
        else{
            try {
                dbHelper.createUser("admin", "cisco", 1);
                dbHelper.createUser("user", "12345", 0);
                dbHelper.createUser("monitor", "1qaz", 0);
                Log.d("Users", "dodano admina");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
//////////////////////


        loginButton.setOnClickListener(v -> {

            String loginValue =  valueOf(loginField.getText());
            String passwordValue = valueOf(passwordField.getText());

            if (loginValue.isEmpty() || passwordValue.isEmpty()) {
                new AlertDialog.Builder(this).setMessage("Wpisz login i hasło!").show();
            } else {
                    Log.d("Users", "" + loginValue + " - " + passwordValue);
                    try {
                        if (dbHelper.login(loginValue, passwordValue)) {
                            Toast.makeText(MainActivity.this, "zalogowano", Toast.LENGTH_SHORT).show();
                            Log.d("Users", "zalogowano " + loginValue);

                            //if(baza.checkDefaultPass(Global.id)){
                            //    Intent intent = new Intent(MainActivity.this, ChangePassActivity.class);
                            //    intent.putExtra("defaultPass", 1);
                            //   startActivity(intent);
                            //    finish();
                            // }else {


                            if(dbHelper.checkIsAdmin(loginValue)){
                                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                intent.putExtra("login", loginValue + " - Administrator");
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                intent.putExtra("login", loginValue);
                                startActivity(intent);
                                finish();
                            }
                        }
                            else{
                                Toast.makeText(MainActivity.this, "Bledny username lub password", Toast.LENGTH_SHORT).show();
                            passwordField.setText(null);
                            }
                        } catch(NoSuchAlgorithmException e){
                            e.printStackTrace();
                        }
                    }


                /*               try {
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
                        Log.d("Users", loginValue + "  " + passwordValue);
                        new AlertDialog.Builder(this).setMessage("Nieprawidłowy login lub hasło!").show();
                    }
                } catch(NoSuchAlgorithmException e){
                    e.printStackTrace();
                }*/
        });
    }
}