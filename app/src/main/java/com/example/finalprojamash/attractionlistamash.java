package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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
    // רשימה שמוצגת בפועל (אחרי פילטר)
    ArrayList<Attraction> displayList = new ArrayList<>();

    // רשימת אטרקציות שנבחרו ע"י המשתמש
    ArrayList<Attraction> selectedAttractions = new ArrayList<>();

    Spinner spNameCountry;

    String country;

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
        spNameCountry=findViewById(R.id.spCountry3);


        // שינוי בחירה ב-Spinner (מדינה)
        spNameCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // עדכון מדינה נבחרת
                country = (String) parent.getItemAtPosition(position);

                // סינון לפי מדינה
                filter(country);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // יצירת רשימה ריקה לאטרקציות
        attractionsList = new ArrayList<>();

        // יצירת Adapter לרשימה
        adapter = new AttractionAdapter(
                displayList,
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






// עדכון טקסט של אטרקציות שנבחרו
//private void updateSelectedText() {

 //   StringBuilder st = new StringBuilder();

    // מעבר על כל האטרקציות שנבחרו
 //   for (Attraction a : selectedAttractions) {
 //       st.append(a.getName()).append(", ");
  //  }

  //  tvSelectedAttraction.setText(st.toString());
//}


private void loadData() {


        // שליפת כל האטרקציות מהדאטהבייס
        databaseService.getAttractionList(new DatabaseService.DatabaseCallback<List<Attraction>>() {
            @Override
            public void onCompleted(List<Attraction> object) {

                // ניקוי רשימות לפני טעינה מחדש
                attractionsList.clear();
                displayList.clear();

                // טעינת נתונים מהרשימה
                attractionsList.addAll(object);
                displayList.addAll(object);

                // עדכון RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("AddTravel", "Error loading attractions", e);
            }
        });


    }

    // סינון אטרקציות לפי מדינה
    public void filter(String text) {

        displayList.clear();

        if (text.isEmpty()) {
            displayList.addAll(attractionsList);
        } else {
            for (Attraction item : attractionsList) {
                if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                    displayList.add(item);
                }
            }
        }


        // עדכון רשימה במסך
        adapter.notifyDataSetChanged();
    }

}