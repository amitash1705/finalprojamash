package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.finalprojamash.services.DatabaseService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // כפתור מעבר למסך התחברות
        Button btnconect = findViewById(R.id.btnconect);

        // כפתור מעבר למסך הרשמה
        Button btnSign = findViewById(R.id.btnsigh);

        // לחיצה על כפתור הרשמה
        btnSign.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivityamash.class);
            startActivity(intent);
        });

        // כפתור מעבר למסך אודות
        Button btnOdot = findViewById(R.id.btnodot);
        btnOdot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, odotamash.class);
            startActivity(intent);
        });

        // לחיצה על כפתור התחברות (Login)
        btnconect = findViewById(R.id.btnconect);
        btnconect.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Loginamash.class);
            startActivity(intent);
        });

        // התאמת padding למערכת (סטטוס בר + ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}