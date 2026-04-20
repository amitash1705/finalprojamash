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

        // Users List
        Button btnUsers = findViewById(R.id.btnUsers);
        btnUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityamash.this, UserActivityamash.class);
            startActivity(intent);
        });

        // Profile
        Button btnpro = findViewById(R.id.btnpro);
        btnpro.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityamash.this, UserProfileamash.class);
            startActivity(intent);
        });

        // Attractions List
        Button btnAttractions = findViewById(R.id.btnAttractions);
        btnAttractions.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityamash.this, attractionlistamash.class);
            startActivity(intent);
        });

        Button btnTrips = findViewById(R.id.btnTrips);

        btnTrips.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityamash.this, travelamash.class);
            startActivity(intent);
        });
        // מערכת רווחים (לא נוגעים בזה)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_login) {
            startActivity(new Intent(this, Loginamash.class));
            return true;
        }

        if (id == R.id.menu_signup) {
            startActivity(new Intent(this, RegisterActivityamash.class));
            return true;
        }

        if (id == R.id.menu_about) {
            startActivity(new Intent(this, odotamash.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}