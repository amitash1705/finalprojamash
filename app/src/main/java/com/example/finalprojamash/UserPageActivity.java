package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openProfile(View view) {
        Intent intent = new Intent(UserPageActivity.this, UserProfileamash.class);
        startActivity(intent);
    }


    public void goNewTravel(View view) {
        Intent intent1 = new Intent(UserPageActivity.this, AddNewTravelamash.class);
        startActivity(intent1);
    }

    public void goMyTravels(View view) {
        Intent intent1 = new Intent(UserPageActivity.this, travelamash.class);
        startActivity(intent1);
    }
}