package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnPro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnPro = findViewById(R.id.btnpro);
        Button btnconect = findViewById(R.id.btnconect);
        Button btnSign = findViewById(R.id.btnsigh);

        // הכפתור של פרופיל
        btnPro = findViewById(R.id.btnpro);
        btnPro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileamash.class);
            startActivity(intent);
        });


        // כפתור הרשמה
        btnSign.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivityamash.class);
            startActivity(intent);
        });

        // כפתור אודות
        Button btnOdot = findViewById(R.id.btnodot);
        btnOdot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, odotamash.class);
            startActivity(intent);
        });


        // הכפתור של פרופיל
        btnconect = findViewById(R.id.btnconect);
        btnconect.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Loginamash.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}