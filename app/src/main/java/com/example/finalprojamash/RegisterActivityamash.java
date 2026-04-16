package com.example.finalprojamash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalprojamash.model.User;
import com.example.finalprojamash.services.DatabaseService;

public class RegisterActivityamash extends AppCompatActivity implements View.OnClickListener {

    EditText etFname, etLname, etPhone, etEmail, etPassWord;
    String Fname, Lname, Phone, Email, PassWord;

    Button btnGoAct2;
    TextView tvName;

    private static final String TAG = "RegisterActivity";

    DatabaseService databaseService;

    SharedPreferences sharedPreferences;
    public static final String mySharedPref = "myPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_activityamash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        sharedPreferences = getSharedPreferences(mySharedPref, MODE_PRIVATE);

        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLaname);
        etPhone = findViewById(R.id.etphone);
        etEmail = findViewById(R.id.etEmail);
        etPassWord = findViewById(R.id.etpassword);



        btnGoAct2 = findViewById(R.id.btnSubmit);
        btnGoAct2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        boolean check = true;

        Fname = etFname.getText().toString().trim();
        Lname = etLname.getText().toString().trim();
        Phone = etPhone.getText().toString().trim();
        Email = etEmail.getText().toString().trim();
        PassWord = etPassWord.getText().toString().trim();

        if (Fname.length() < 2) {
            check = false;
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
        }

        if (Lname.length() < 2) {
            check = false;
            Toast.makeText(this, "enter Last name", Toast.LENGTH_SHORT).show();
        }

        if (Phone.length() != 10) {
            check = false;
            Toast.makeText(this, "enter valid phone", Toast.LENGTH_SHORT).show();
        }

        if (!Email.contains("@")) {
            check = false;
            Toast.makeText(this, "enter Email", Toast.LENGTH_SHORT).show();
        }

        if (PassWord.length() < 6) {
            check = false;
            Toast.makeText(this, "enter Password", Toast.LENGTH_SHORT).show();
        }

        if (check) {

            registerUser(Fname, Lname, Phone, Email, PassWord);

        } else {
            Toast.makeText(this, "fix the data", Toast.LENGTH_SHORT).show();
            tvName.setText("fix the data");
        }
    }

    private void registerUser(String fname, String lname, String phone, String email, String password) {
        Log.d(TAG, "registerUser: Registering user...");

        // ❗ FIX: אין ID קבוע
        User user = new User(null, fname, lname, email, phone, password);

        createUserInDatabase(user);
    }

    private void createUserInDatabase(User user) {

        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {

                Log.d(TAG, "User created successfully");

                user.setId(uid);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", Email);
                editor.putString("password", PassWord);
                editor.apply();

                Intent intent = new Intent(RegisterActivityamash.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {

                Log.e(TAG, "Failed to create user", e);
                Toast.makeText(RegisterActivityamash.this,
                        "Failed to register user",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}