package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.adapter.AttractionAdapter;
import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.model.Travel;
import com.example.finalprojamash.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Travelprofileamash extends AppCompatActivity {

    // כפתור שמירה – מעדכן את פרטי הטיול בדאטהבייס
    Button btnSave, btnEditAttractions;

    // שדות טקסט לעריכת שם ותיאור הטיול
    EditText etName, etDetails;

    // שירות שמדבר עם הדאטהבייס (Firebase / DB אחר)
    DatabaseService databaseService;

    // רשימת אטרקציות שמוצגות במסך
    ArrayList<Attraction> attractionsList;

    // RecyclerView = רשימה גרפית במסך (UI של רשימות)
    RecyclerView rcAttraction;

    // Adapter מחבר בין הנתונים (List) לבין ה-RecyclerView
    AttractionAdapter adapter;

    // האובייקט של הטיול הנוכחי שאנחנו עורכים
    Travel travel;

    // ID של הטיול שמגיע מהמסך הקודם
    String travelId;

    // שמירת ערכים מקוריים כדי לבדוק אם המשתמש שינה משהו
    private String originalName;
    private String originalDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מאפשר UI מודרני (מסך מלא, בלי שוליים של מערכת)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_travelprofileamash);

        // מקבל את מזהה הטיול מהמסך הקודם דרך Intent
        travelId = getIntent().getStringExtra("travelId");

        // יצירת instance של שירות הדאטהבייס
        databaseService = DatabaseService.getInstance();

        // אתחול כל ה-Views והלוגיקה
        initViews();

        // שליפת הטיול מהדאטהבייס לפי ID
        databaseService.getTravel(travelId, new DatabaseService.DatabaseCallback<Travel>() {
            @Override
            public void onCompleted(Travel t) {

                // שומרים את הטיול שהתקבל
                travel = t;

                // מילוי UI לפי הנתונים מהדאטהבייס
                etName.setText(t.getName());
                etDetails.setText(t.getDetails());

                // שומרים ערכים מקוריים להשוואה אחר כך
                originalName = t.getName();
                originalDetails = t.getDetails();

                // טוענים את רשימת האטרקציות של הטיול
                attractionsList.clear();
                attractionsList.addAll(t.getAttractionList());

                // מעדכנים את ה-RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                // אם משהו נכשל – רושמים בלוג (debug)
                Log.e("Travel", "error", e);
            }
        });
    }

    private void initViews() {

        // חיבור בין הקוד ל-XML (UI elements)
        etName = findViewById(R.id.ettravelnameprofile);
        etDetails = findViewById(R.id.ettraveldetailsprofile);

        btnSave = findViewById(R.id.btnsaveedittravelprofile);
        btnEditAttractions = findViewById(R.id.btneditAttractions);

        // לחיצה על כפתור שמירה
        btnSave.setOnClickListener(v -> saveTravel());

        // פתיחת דיאלוג לבחירת אטרקציות
        btnEditAttractions.setOnClickListener(v -> showAttractionsDialog());

        // הגדרת RecyclerView כתצוגה אנכית (רשימה)
        rcAttraction = findViewById(R.id.rvtravelAttractionProfile2);
        rcAttraction.setLayoutManager(new LinearLayoutManager(this));

        // יצירת רשימה ריקה שתתמלא מהשרת
        attractionsList = new ArrayList<>();

        // יצירת Adapter שמגדיר איך כל אטרקציה נראית
        adapter = new AttractionAdapter(attractionsList,
                new AttractionAdapter.OnAttrctionClickListener() {

                    // לחיצה רגילה → הוספת אטרקציה לטיול
                    @Override
                    public void onAttractionClick(Attraction attraction) {

                        travel.getAttractionList().add(attraction);
                        adapter.notifyDataSetChanged();

                        // עדכון הדאטהבייס אחרי שינוי
                        databaseService.updateTravel(travel,
                                new DatabaseService.DatabaseCallback<Void>() {
                                    @Override
                                    public void onCompleted(Void object) {}

                                    @Override
                                    public void onFailed(Exception e) {
                                        Log.e("Update", "error", e);
                                    }
                                });
                    }

                    // לחיצה ארוכה → מחיקת אטרקציה מהטיול
                    @Override
                    public void onLongAttractionClick(Attraction attraction) {

                        Toast.makeText(Travelprofileamash.this,
                                " delete attraction",
                                Toast.LENGTH_SHORT).show();

                        travel.getAttractionList().remove(attraction);
                        adapter.notifyDataSetChanged();

                        // עדכון בדאטהבייס אחרי מחיקה
                        databaseService.updateTravel(travel,
                                new DatabaseService.DatabaseCallback<Void>() {
                                    @Override
                                    public void onCompleted(Void object) {}

                                    @Override
                                    public void onFailed(Exception e) {
                                        Log.e("Update", "error", e);
                                    }
                                });
                    }
                });

        // חיבור האדפטר לרשימה
        rcAttraction.setAdapter(adapter);
    }

    private void showAttractionsDialog() {

        // אם אין טיול לא פותחים דיאלוג
        if (travel == null) return;

        // יצירת חלון קופץ (Dialog)
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Select Attraction");

        // טעינת UI של הדיאלוג מקובץ XML
        View view = getLayoutInflater().inflate(R.layout.dialog_attractions, null);
        RecyclerView rv = view.findViewById(R.id.rvDialogAttractions);
        rv.setLayoutManager(new LinearLayoutManager(this));

        builder.setView(view);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // שליפת כל האטרקציות מהדאטהבייס
        databaseService.getAttractionList(new DatabaseService.DatabaseCallback<List<Attraction>>() {
            @Override
            public void onCompleted(List<Attraction> list) {

                ArrayList<Attraction> filtered = new ArrayList<>();

                // סינון אטרקציות לפי קריטריון (כאן לפי שם טיול - אולי לוגיקה לא מדויקת)
                for (Attraction a : list) {
                    if (a.getCountry().equals(travel.getName())) {
                        filtered.add(a);
                    }
                }

                // Adapter לדיאלוג
                AttractionAdapter dialogAdapter =
                        new AttractionAdapter(filtered,
                                new AttractionAdapter.OnAttrctionClickListener() {

                                    // הוספה מהדיאלוג
                                    @Override
                                    public void onAttractionClick(Attraction attraction) {

                                        travel.getAttractionList().add(attraction);
                                        adapter.notifyDataSetChanged();

                                        databaseService.updateTravel(travel,
                                                new DatabaseService.DatabaseCallback<Void>() {
                                                    @Override
                                                    public void onCompleted(Void object) {}

                                                    @Override
                                                    public void onFailed(Exception e) {
                                                        Log.e("Update", "error", e);
                                                    }
                                                });

                                        // סוגר את החלון אחרי פעולה
                                        dialog.dismiss();
                                    }

                                    // מחיקה מהדיאלוג
                                    @Override
                                    public void onLongAttractionClick(Attraction attraction) {

                                        travel.getAttractionList().remove(attraction);
                                        adapter.notifyDataSetChanged();

                                        databaseService.updateTravel(travel,
                                                new DatabaseService.DatabaseCallback<Void>() {
                                                    @Override
                                                    public void onCompleted(Void object) {}

                                                    @Override
                                                    public void onFailed(Exception e) {
                                                        Log.e("Update", "error", e);
                                                    }
                                                });

                                        dialog.dismiss();
                                    }
                                });

                rv.setLayoutManager(new LinearLayoutManager(Travelprofileamash.this));
                rv.setAdapter(dialogAdapter);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("Dialog", "error", e);
            }
        });

        // הצגת הדיאלוג על המסך
        dialog.show();
    }

    private void saveTravel() {

        // קריאת ערכים חדשים שהמשתמש שינה
        String currentName = etName.getText().toString().trim();
        String currentDetails = etDetails.getText().toString().trim();

        // בדיקה אם לא היה שינוי בכלל
        if (currentName.equals(originalName)
                && currentDetails.equals(originalDetails)) {

            Toast.makeText(this,
                    "No changes were made",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // עדכון האובייקט בזיכרון
        travel.setName(currentName);
        travel.setDetails(currentDetails);

        // שמירה סופית בדאטהבייס
        databaseService.updateTravel(travel,
                new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void v) {
                        // חוזר למסך הקודם
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("Save", "error", e);
                    }
                });
    }
}