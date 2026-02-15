package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivityamash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_activityamash);


        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnBackHome.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminActivityamash.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // סוגר את דף האודות כדי שלא יחזור אליו בלחיצה על BACK
        });



        Button btnpro = findViewById(R.id.btnpro);

        btnpro.setOnClickListener(v -> {
            Intent intent1 = new Intent(AdminActivityamash.this, UserProfileamash.class);
            startActivity(intent1);
        });


        Button btnattraction = findViewById(R.id.btnattraction);

        btnattraction.setOnClickListener(v -> {
            Intent intent1 = new Intent(AdminActivityamash.this, attractionlistamash.class);
            startActivity(intent1);
        });



        // מוצאים את הכפתור לפי ה-id
        Button btnAddNewAttraction = findViewById(R.id.btnAddNewAttraction);

// מוסיפים מאזין לחיצה
        btnAddNewAttraction.setOnClickListener(v -> {
            // יוצרים Intent לעבור לדף AddAttractionActivityamash
            Intent intent1 = new Intent(AdminActivityamash.this, AddAttractionActivityamash.class);
            startActivity(intent1); // פותח את הדף החדש
        });


        Button btntrip = findViewById(R.id.btntrip);

        btntrip.setOnClickListener(v -> {
            Intent intent1 = new Intent(AdminActivityamash.this, AddNewTravelamash.class);
            startActivity(intent1);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    }
