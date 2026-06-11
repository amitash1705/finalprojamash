package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

public class odotamash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_odotamash);

        // התאמת padding לסרגלי מערכת (סטטוס + ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // מסך אודות – כרגע אין לוגיקה נוספת
        // ניתן להוסיף כאן מידע על האפליקציה בעתיד
    }
}