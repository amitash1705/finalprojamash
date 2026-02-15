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
    Button backButton;
    TextView tvName;

    private static final String TAG = "RegisterActivity";

    DatabaseService databaseService;

    SharedPreferences sharedPreferences;
    public  static  final  String mySharedPref="myPref";




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

        sharedPreferences=getSharedPreferences(mySharedPref,MODE_PRIVATE);

        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLaname);
        etPhone = findViewById(R.id.etphone);
        etEmail = findViewById(R.id.etEmail);
        etPassWord = findViewById(R.id.etpassword);
        tvName = findViewById(R.id.tvName);
        btnGoAct2 = findViewById(R.id.btnSubmit);
        btnGoAct2.setOnClickListener(this);
        backButton = findViewById(R.id.btnBackHome);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivityamash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {

        boolean check = true;

        Fname = etFname.getText().toString();
        Lname = etLname.getText().toString();
        Phone = etPhone.getText().toString();
        Email = etEmail.getText().toString();
        PassWord = etPassWord.getText().toString();

        if (Fname.length() < 2) {
            check = false;
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
        }

        if (Lname.length() < 2) {
            check = false;
            Toast.makeText(this, "enter Last name", Toast.LENGTH_SHORT).show();
        }
        if (Phone.length() < 10 || Phone.length() > 10) {
            check = false;
            Toast.makeText(this, "enter Phone", Toast.LENGTH_SHORT).show();
        }
        if (!Email.contains("@")) {
            check = false;
            Toast.makeText(this, "enter Email", Toast.LENGTH_SHORT).show();
        }
        if (PassWord.length() < 6) {
            check = false;
            Toast.makeText(this, "enter Password", Toast.LENGTH_SHORT).show();
        }


        if (check == true) {


            registerUser(Fname, Lname, Phone, Email, PassWord);

            Intent go = new Intent(this, MainActivity.class);
           // go.putExtra("Fname", Fname);
          //  go.putExtra("Lname", Lname);
          //  go.putExtra("Phone", Phone);
         //   go.putExtra("Email", Email);
         //   go.putExtra("Password", PassWord);
          //  startActivity(go);
        } else {
            Toast.makeText(this, "fix the data", Toast.LENGTH_SHORT).show();
            tvName.setText("fix the data");

        }

    }

    /// Register the user
    private void registerUser(String fname, String lname, String phone, String email, String password) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        User user = new User("44", fname, lname, email, phone, password);



        /// proceed to create the user
        createUserInDatabase(user);

    }


    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                user.setId(uid);

                SharedPreferences.Editor editor=sharedPreferences.edit();

                    editor.putString("email",Email);
                    editor.putString("password",PassWord);
                    editor.commit();


                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterActivityamash.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(RegisterActivityamash.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }
}

