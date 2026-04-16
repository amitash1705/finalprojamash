package com.example.finalprojamash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalprojamash.services.DatabaseService;

public class Loginamash extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail, etPassword;
    Button btnSubmit, btnBackHome;

    private static final String TAG = "LoginActivity";

    private DatabaseService databaseService;
    SharedPreferences sharedPreferences;
    public static final String mySharedPref = "myPref";

    private String email, password;
    public  static boolean isAdmin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginamash);

        databaseService = DatabaseService.getInstance();

        sharedPreferences = getSharedPreferences(mySharedPref, MODE_PRIVATE);

        etEmail = findViewById(R.id.et_loginamash_email);
        etPassword = findViewById(R.id.et_loginamash_password);

        btnSubmit = findViewById(R.id.btn_login_submit);


        btnSubmit.setOnClickListener(this);



        // load saved data safely
        email = sharedPreferences.getString("email", "");
        password = sharedPreferences.getString("password", "");

        etEmail.setText(email);
        etPassword.setText(password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == btnSubmit.getId()) {

            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter email");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter password");
                return;
            }

            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {

        Log.d(TAG, "loginUser: " + email);

        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                // admin check
                if (email.equals("amit@gmail.com") && password.equals("123456")) {

                    isAdmin=true;

                    Intent intent = new Intent(Loginamash.this, AdminActivityamash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(Loginamash.this, UserPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailed(Exception e) {

                Log.e(TAG, "Login failed", e);
                Toast.makeText(Loginamash.this,
                        "Login failed. Check email/password",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}