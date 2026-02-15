package com.example.finalprojamash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalprojamash.services.DatabaseService;

public class Loginamash extends AppCompatActivity implements View.OnClickListener {


    EditText etEmail, etPassword;
    Button btnSubmit;

    private static final String TAG = "LoginActivity";

    private DatabaseService databaseService;
    SharedPreferences sharedPreferences;
    public  static  final  String mySharedPref="myPref";

    private String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginamash);


      //  הוספה עכשיו

        sharedPreferences=getSharedPreferences(mySharedPref,MODE_PRIVATE);

        databaseService = DatabaseService.getInstance();




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




       email= sharedPreferences.getString("email","");
       password=  sharedPreferences.getString("password","");

        etEmail = findViewById(R.id.et_loginamash_email);
        etPassword = findViewById(R.id.et_loginamash_password);


        etEmail.setText(email);
        etPassword.setText(password);
        btnSubmit = findViewById(R.id.btn_login_submit);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnSubmit.setOnClickListener(this);


        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(Loginamash.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmit.getId()) {
            Log.d(TAG, "onClick: Login button clicked");

             email = etEmail.getText().toString().trim();
             password = etPassword.getText().toString().trim();

            // Validate input
            if (email.isEmpty()) {
                etEmail.setError("Enter email");
              //  etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter password");
               // etPassword.requestFocus();
                return;
            }

            Log.d(TAG, "Logging in with: " + email);

            loginUser(email, password);
        }
    }


    private void loginUser(String email, String password) {

        Log.d(TAG, "loginUser: Calling Firebase login..."+email+"    "+ password);

        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "User logged in! UID: " + uid);


                SharedPreferences.Editor editor=sharedPreferences.edit();


                editor.putString("email",email);
                editor.putString("password",password);
                editor.commit();


                Intent mainIntent = new Intent(Loginamash.this, AdminActivityamash.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {

                Log.e(TAG, "Firebase login failed", e);

               // etPassword.setError("Invalid email or password");
               // etPassword.requestFocus();
            }
        });
    }
}
