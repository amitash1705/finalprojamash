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

import com.example.finalprojamash.model.User;
import com.example.finalprojamash.services.DatabaseService;

public class RegisterActivityamash extends AppCompatActivity implements View.OnClickListener {

    // שדות קלט להרשמה
    EditText etFname, etLname, etPhone, etEmail, etPassWord;

    // משתנים זמניים לשמירת ערכים מהטופס
    String Fname, Lname, Phone, Email, PassWord;

    // כפתור הרשמה
    Button btnGoAct2;

    // שירות דאטהבייס
    DatabaseService databaseService;

    // שמירת נתונים מקומית במכשיר
    SharedPreferences sharedPreferences;

    public static final String mySharedPref = "myPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_register_activityamash);

        // התאמת padding לסרגלי מערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // יצירת instance של שירות דאטהבייס
        databaseService = DatabaseService.getInstance();

        // אתחול SharedPreferences
        sharedPreferences = getSharedPreferences(mySharedPref, MODE_PRIVATE);

        // חיבור שדות מהמסך
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLaname);
        etPhone = findViewById(R.id.etphone);
        etEmail = findViewById(R.id.etEmail);
        etPassWord = findViewById(R.id.etpassword);

        // חיבור כפתור הרשמה
        btnGoAct2 = findViewById(R.id.btnSubmit);

        // לחיצה על כפתור הרשמה
        btnGoAct2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // קריאת נתונים מהטופס
        Fname = etFname.getText().toString().trim();
        Lname = etLname.getText().toString().trim();
        Phone = etPhone.getText().toString().trim();
        Email = etEmail.getText().toString().trim();
        PassWord = etPassWord.getText().toString().trim();

        // בדיקות תקינות שדות

        if (Fname.length() < 2) {
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Lname.length() < 2) {
            Toast.makeText(this, "enter Last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Phone.length() != 10) {
            Toast.makeText(this, "enter valid phone", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Email.contains("@")) {
            Toast.makeText(this, "enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (PassWord.length() < 6) {
            Toast.makeText(this, "enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        // אם כל הבדיקות עברו → הרשמה
        registerUser(Fname, Lname, Phone, Email, PassWord);
    }

    private void registerUser(String fname, String lname, String phone, String email, String password) {

        // יצירת אובייקט משתמש חדש
        User user = new User(null, fname, lname, email, phone, password);

        // שמירה בדאטהבייס
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {

            @Override
            public void onCompleted(String uid) {

                // עדכון ID למשתמש שנוצר
                user.setId(uid);

                // שמירת פרטי התחברות בזיכרון המכשיר
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                // מעבר למסך ראשי אחרי הרשמה
                Intent intent = new Intent(RegisterActivityamash.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {

                // הודעת שגיאה במקרה של כישלון הרשמה
                Toast.makeText(RegisterActivityamash.this,
                        "Failed to register user",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}