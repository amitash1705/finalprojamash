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

    // שדות קלט: אימייל וסיסמה
    EditText etEmail, etPassword;

    // כפתורי התחברות וחזרה
    Button btnSubmit, btnBackHome;

    // תגית ללוגים (debug)
    private static final String TAG = "LoginActivity";

    // שירות דאטהבייס
    private DatabaseService databaseService;

    // שמירת נתונים מקומית (זיכרון מכשיר)
    SharedPreferences sharedPreferences;

    // שם קובץ ה-SharedPreferences
    public static final String mySharedPref = "myPref";

    private String email, password;

    // משתנה שמזהה אם המשתמש אדמין
    public static boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_loginamash);

        // אתחול שירות דאטהבייס
        databaseService = DatabaseService.getInstance();

        // טעינת SharedPreferences
        sharedPreferences = getSharedPreferences(mySharedPref, MODE_PRIVATE);

        // חיבור שדות מהמסך
        etEmail = findViewById(R.id.et_loginamash_email);
        etPassword = findViewById(R.id.et_loginamash_password);

        btnSubmit = findViewById(R.id.btn_login_submit);

        // לחיצה על כפתור התחברות
        btnSubmit.setOnClickListener(this);

        // טעינת נתונים שמורים (אם קיימים)
        email = sharedPreferences.getString("email", "");
        password = sharedPreferences.getString("password", "");

        etEmail.setText(email);
        etPassword.setText(password);

        // התאמת padding למערכת (סטטוס בר + ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {

        // בדיקה אם לחצו על כפתור התחברות
        if (v.getId() == btnSubmit.getId()) {

            // קריאת נתוני משתמש מהטופס
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            // בדיקת שדות חובה
            if (email.isEmpty()) {
                etEmail.setError("Enter email");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter password");
                return;
            }

            // ניסיון התחברות
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {

        // לוג לבדיקה
        Log.d(TAG, "loginUser: " + email);

        // קריאה לשירות התחברות בדאטהבייס
        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {

                // שמירת נתונים בזיכרון המכשיר
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                // בדיקת משתמש אדמין לפי אימייל וסיסמה קבועים
                if (email.equals("amit@gmail.com") && password.equals("123456")) {

                    isAdmin = true;

                    // מעבר למסך אדמין
                    Intent intent = new Intent(Loginamash.this, AdminActivityamash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {

                    isAdmin = false;

                    // מעבר למסך משתמש רגיל
                    Intent intent = new Intent(Loginamash.this, UserPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailed(Exception e) {

                // טיפול בשגיאת התחברות
                Log.e(TAG, "Login failed", e);

                Toast.makeText(Loginamash.this,
                        "Login failed. Check email/password",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}