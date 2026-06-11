package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.adapter.AttractionAdapter;
import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class attractionlistamash extends AppCompatActivity {

    // תגית ללוגים (לבדיקות ודיבאג)
    private static final String TAG = "ReadAttraction";

    DatabaseService databaseService;

    // רשימת אטרקציות להצגה במסך
    ArrayList<Attraction> attractionsList;

    RecyclerView rcAttraction;
    AttractionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // הפעלת UI במסך מלא (EdgeToEdge)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_attractionlistamash);

        // התאמת padding לסרגלי מערכת (סטטוס/ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול כל הרכיבים במסך
        initViews();

        // טעינת נתונים מהדאטהבייס
        loadData();
    }

    private void initViews() {

        // קבלת instance של שירות הדאטהבייס
        databaseService = DatabaseService.getInstance();

        // חיבור RecyclerView והגדרת תצוגה אנכית
        rcAttraction = findViewById(R.id.rcAttraction);
        rcAttraction.setLayoutManager(new LinearLayoutManager(this));

        // יצירת רשימה ריקה לאטרקציות
        attractionsList = new ArrayList<>();

        // יצירת Adapter לרשימה
        adapter = new AttractionAdapter(
                attractionsList,
                new AttractionAdapter.OnAttrctionClickListener() {

                    // לחיצה על אטרקציה → מעבר למסך עריכה
                    @Override
                    public void onAttractionClick(Attraction attraction) {

                        Intent go = new Intent(
                                attractionlistamash.this,
                                Editattraction.class
                        );

                        // שליחת ID של האטרקציה למסך הבא
                        go.putExtra("attId", attraction.getId());

                        startActivity(go);
                    }

                    // לחיצה ארוכה (כרגע לא בשימוש)
                    @Override
                    public void onLongAttractionClick(Attraction attraction) {
                        // אפשר להוסיף מחיקה בעתיד
                    }
                }
        );

        // חיבור adapter לרשימה
        rcAttraction.setAdapter(adapter);
    }

    private void loadData() {

        // שליפת כל האטרקציות מהדאטהבייס
        databaseService.getAttractionList(new DatabaseService.DatabaseCallback<List<Attraction>>() {

            @Override
            public void onCompleted(List<Attraction> object) {

                // לוג לבדיקה (כמה אטרקציות התקבלו)
                Log.d(TAG, "onCompleted: " + object);

                // ניקוי רשימה ישנה
                attractionsList.clear();

                // טעינת נתונים חדשים
                attractionsList.addAll(object);

                // עדכון המסך
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {

                // לוג שגיאה
                Log.e(TAG, "onFailed: ", e);
            }
        });
    }
}