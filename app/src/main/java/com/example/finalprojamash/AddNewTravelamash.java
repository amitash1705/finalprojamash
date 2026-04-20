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

    ArrayList<Attraction> attractionsList = new ArrayList<>();
    ArrayList<Attraction> displayList = new ArrayList<>();
    ArrayList<Attraction> selectedAttractions = new ArrayList<>();

    RecyclerView rcAttraction;
    AttractionAdapter adapter;

    EditText etTravelDetails;
    Spinner spNameCountry;
    TextView tvSelectedAttraction;
    Button btnAddNewTravel;

    String country = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_travelamash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        databaseService.getAttractionList(new DatabaseService.DatabaseCallback<List<Attraction>>() {
            @Override
            public void onCompleted(List<Attraction> object) {

                attractionsList.clear();
                displayList.clear();

                attractionsList.addAll(object);
                displayList.addAll(object);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("AddTravel", "Error loading attractions", e);
            }
        });
    }

    private void initViews() {

        databaseService = DatabaseService.getInstance();

        rcAttraction = findViewById(R.id.rvAttractions);
        rcAttraction.setLayoutManager(new LinearLayoutManager(this));

        etTravelDetails = findViewById(R.id.etTravelDetails);
        spNameCountry = findViewById(R.id.spCountry2);
        tvSelectedAttraction = findViewById(R.id.tvSelectedAttraction);
        btnAddNewTravel = findViewById(R.id.btnAddTravel);

        btnAddNewTravel.setOnClickListener(this);

        adapter = new AttractionAdapter(displayList, new AttractionAdapter.OnAttrctionClickListener() {

            // 🟢 לחיצה רגילה = הוספה + בדיקה
            @Override
            public void onAttractionClick(Attraction attraction) {

                if (selectedAttractions.contains(attraction)) {

                    Toast.makeText(AddNewTravelamash.this,
                            "You already picked this attraction",
                            Toast.LENGTH_SHORT).show();

                } else {

                    selectedAttractions.add(attraction);
                }

                updateSelectedText();
            }

            // 🔴 לחיצה ארוכה = ביטול בחירה
            @Override
            public void onLongAttractionClick(Attraction attraction) {

                if (selectedAttractions.contains(attraction)) {
                    selectedAttractions.remove(attraction);
                    updateSelectedText();
                }
            }
        });

        rcAttraction.setAdapter(adapter);

        spNameCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                country = (String) parent.getItemAtPosition(position);
                filter(country);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void updateSelectedText() {

        StringBuilder st = new StringBuilder();

        for (Attraction a : selectedAttractions) {
            st.append(a.getName()).append(", ");
        }

        tvSelectedAttraction.setText(st.toString());
    }


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

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        String travelId = databaseService.generateTravelId();
        String details = etTravelDetails.getText().toString().trim();

        if (details.isEmpty()) {
            etTravelDetails.setError("Please enter travel details");
            return;
        }

        if (selectedAttractions.isEmpty()) {
            Toast.makeText(this, "Please pick attractions", Toast.LENGTH_SHORT).show();
            return;
        }

        Travel newTravel = new Travel(travelId, country, selectedAttractions, details);

        databaseService.createNewTravel(newTravel, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

                startActivity(new Intent(AddNewTravelamash.this, UserPageActivity.class));
                finish();
            }

            @Override
            public void onFailed(Exception e) {

                Toast.makeText(AddNewTravelamash.this, "Error creating travel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}