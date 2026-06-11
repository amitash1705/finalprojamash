package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.adapter.AttractionAdapter;
import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.model.Travel;
import com.example.finalprojamash.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class AddNewTravelamash extends AppCompatActivity implements View.OnClickListener {

    DatabaseService databaseService;

    // רשימה מלאה של אטרקציות מהדאטהבייס
    ArrayList<Attraction> attractionsList = new ArrayList<>();

    // רשימה שמוצגת בפועל (אחרי פילטר)
    ArrayList<Attraction> displayList = new ArrayList<>();

    // רשימת אטרקציות שנבחרו ע"י המשתמש
    ArrayList<Attraction> selectedAttractions = new ArrayList<>();

    RecyclerView rcAttraction;
    AttractionAdapter adapter;

    EditText etTravelDetails;
    Spinner spNameCountry;
    TextView tvSelectedAttraction;
    Button btnAddNewTravel;

    // המדינה שנבחרה בפילטר
    String country = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // הפעלת מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_new_travelamash);

        // התאמת padding לסרגלי מערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול כל ה-Views
        initViews();

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

    private void initViews() {

        // יצירת instance של שירות הדאטהבייס
        databaseService = DatabaseService.getInstance();

        // חיבור RecyclerView והגדרת layout אנכי
        rcAttraction = findViewById(R.id.rvAttractions);
        rcAttraction.setLayoutManager(new LinearLayoutManager(this));

        // חיבור שדות מהמסך
        etTravelDetails = findViewById(R.id.etTravelDetails);
        spNameCountry = findViewById(R.id.spCountry2);
        tvSelectedAttraction = findViewById(R.id.tvSelectedAttraction);
        btnAddNewTravel = findViewById(R.id.btnAddTravel);

        // לחיצה על כפתור יצירת טיול
        btnAddNewTravel.setOnClickListener(this);

        // יצירת Adapter לרשימת האטרקציות
        adapter = new AttractionAdapter(displayList, new AttractionAdapter.OnAttrctionClickListener() {

            // לחיצה רגילה על אטרקציה = הוספה לרשימת נבחרות
            @Override
            public void onAttractionClick(Attraction attraction) {

                // מניעת כפילות בבחירה
                if (selectedAttractions.contains(attraction)) {

                    Toast.makeText(AddNewTravelamash.this,
                            "You already picked this attraction",
                            Toast.LENGTH_SHORT).show();

                } else {
                    selectedAttractions.add(attraction);
                }

                // עדכון טקסט של אטרקציות שנבחרו
                updateSelectedText();
            }

            // לחיצה ארוכה = הסרה מהרשימה הנבחרת
            @Override
            public void onLongAttractionClick(Attraction attraction) {

                if (selectedAttractions.contains(attraction)) {
                    selectedAttractions.remove(attraction);
                    updateSelectedText();
                }
            }
        });

        // חיבור adapter ל-RecyclerView
        rcAttraction.setAdapter(adapter);

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
    }

    // עדכון טקסט של אטרקציות שנבחרו
    private void updateSelectedText() {

        StringBuilder st = new StringBuilder();

        // מעבר על כל האטרקציות שנבחרו
        for (Attraction a : selectedAttractions) {
            st.append(a.getName()).append(", ");
        }

        tvSelectedAttraction.setText(st.toString());
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

    @Override
    public void onClick(View v) {

        // יצירת מזהה ייחודי לטיול
        String travelId = databaseService.generateTravelId();

        // פרטי הטיול מהמשתמש
        String details = etTravelDetails.getText().toString().trim();

        // בדיקה שהמשתמש הזין פרטים
        if (details.isEmpty()) {
            etTravelDetails.setError("Please enter travel details");
            return;
        }

        // בדיקה שנבחרו אטרקציות
        if (selectedAttractions.isEmpty()) {
            Toast.makeText(this, "Please pick attractions", Toast.LENGTH_SHORT).show();
            return;
        }

        // יצירת אובייקט טיול חדש
        Travel newTravel = new Travel(travelId, country, selectedAttractions, details);

        // שמירה בדאטהבייס
        databaseService.createNewTravel(newTravel, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

                // מעבר למסך משתמש אחרי יצירה מוצלחת
                startActivity(new Intent(AddNewTravelamash.this, UserPageActivity.class));
                finish();
            }

            @Override
            public void onFailed(Exception e) {

                // הודעת שגיאה אם משהו נכשל
                Toast.makeText(AddNewTravelamash.this, "Error creating travel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}