package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.finalprojamash.adapter.TravelAdapter;
import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.model.Travel;
import com.example.finalprojamash.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class travelamash extends AppCompatActivity {

    // תגית ללוגים (לא בשימוש כרגע)
    private static final String TAG = "ReadUserTravel";

    // שירות דאטהבייס
    DatabaseService databaseService;

    // אדפטר להצגת רשימת טיולים
    TravelAdapter adapter;

    // רשימת טיולים להצגה במסך
    List<Travel> travelList = new ArrayList<>();

    // RecyclerView להצגת הנתונים
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_travelamash);

        // התאמת padding למערכת (סטטוס + ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול שירות דאטהבייס
        databaseService = DatabaseService.getInstance();

        // חיבור RecyclerView והגדרת תצוגה אנכית
        recyclerView = findViewById(R.id.rvTravel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // יצירת Adapter לרשימת הטיולים
        adapter = new TravelAdapter(travelamash.this, travelList, new TravelAdapter.OnTravelClickListener() {

            // לחיצה על עריכה → מעבר למסך פרופיל טיול
            @Override
            public void onEditClick(Travel travel, int position) {

                Intent go = new Intent(travelamash.this, Travelprofileamash.class);
                go.putExtra("travelId", travel.getId());
                startActivity(go);
            }

            // לחיצה על מחיקה → הסרה מהרשימה + מהמסך
            @Override
            public void onDeleteClick(Travel travel, int position) {

                travelList.remove(position);
                adapter.notifyItemRemoved(position);

                // מחיקה גם מהדאטהבייס
                deleteTravelFromDatabase(travel.getId());
            }

            // לחיצה על פריט (כרגע לא בשימוש)
            @Override
            public void onItemClick(Travel travel, int position) {
                // ניתן להוסיף בעתיד פתיחת פרטים
            }
        });

        // חיבור adapter ל-RecyclerView
        recyclerView.setAdapter(adapter);

        // בדיקה אם המשתמש אדמין או רגיל
        if (Loginamash.isAdmin) {

            // אדמין רואה את כל הטיולים
            getAllTrvels();

        } else {

            // משתמש רגיל רואה רק את הטיולים שלו
            databaseService.getUserTravelList(new DatabaseService.DatabaseCallback<List<Travel>>() {
                @Override
                public void onCompleted(List<Travel> object) {

                    travelList.addAll(object);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(Exception e) {
                    // טיפול בשגיאה
                }
            });
        }
    }

    // שליפת כל הטיולים (אדמין)
    private void getAllTrvels() {

        databaseService.getTravelList(new DatabaseService.DatabaseCallback<List<Travel>>() {
            @Override
            public void onCompleted(List<Travel> object) {

                travelList.addAll(object);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                // טיפול בשגיאה
            }
        });
    }

    // מחיקת טיול מהדאטהבייס
    private void deleteTravelFromDatabase(String id) {

        databaseService.deleteTravel(id, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

                // ניתן להוסיף Toast להצלחה
            }

            @Override
            public void onFailed(Exception e) {
                // טיפול בשגיאה
            }
        });
    }
}