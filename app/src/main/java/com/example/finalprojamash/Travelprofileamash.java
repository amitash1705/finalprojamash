package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

    Button btnSave, btnEditAttractions;
    EditText etName, etDetails;

    DatabaseService databaseService;
    ArrayList<Attraction> attractionsList;
    RecyclerView rcAttraction;
    AttractionAdapter adapter;

    Travel travel;
    String travelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_travelprofileamash);

        travelId = getIntent().getStringExtra("travelId");
        databaseService = DatabaseService.getInstance();

        initViews();

        databaseService.getTravel(travelId, new DatabaseService.DatabaseCallback<Travel>() {
            @Override
            public void onCompleted(Travel t) {

                travel = t;

                etName.setText(t.getName());
                etDetails.setText(t.getDetails());

                attractionsList.clear();
                attractionsList.addAll(t.getAttractionList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("Travel", "error", e);
            }
        });
    }

    private void initViews() {

        etName = findViewById(R.id.ettravelnameprofile);
        etDetails = findViewById(R.id.ettraveldetailsprofile);

        btnSave = findViewById(R.id.btnsaveedittravelprofile);
        btnEditAttractions = findViewById(R.id.btneditAttractions);

        btnSave.setOnClickListener(v -> saveTravel());

        btnEditAttractions.setOnClickListener(v -> {
            showAttractionsDialog();
        });

        rcAttraction = findViewById(R.id.rvtravelAttractionProfile2);
        rcAttraction.setLayoutManager(new LinearLayoutManager(this));

        attractionsList = new ArrayList<>();

        adapter = new AttractionAdapter(attractionsList,
                new AttractionAdapter.OnAttrctionClickListener() {

                    @Override
                    public void onAttractionClick(Attraction attraction) {
                        // אפשר להשאיר ריק או למחוק
                    }

                    @Override
                    public void onLongAttractionClick(Attraction attraction) {
                        // מחיקה אם צריך
                    }
                });

        rcAttraction.setAdapter(adapter);
    }

    // =========================
    // 🔥 DIALOG
    // =========================
    private void showAttractionsDialog() {

        if (travel == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Attraction");

        View view = getLayoutInflater().inflate(R.layout.dialog_attractions, null);
        RecyclerView rv = view.findViewById(R.id.rvDialogAttractions);
        rv.setLayoutManager(new LinearLayoutManager(this));


        builder.setView(view);

        AlertDialog dialog = builder.create();

        databaseService.getAttractionList(new DatabaseService.DatabaseCallback<List<Attraction>>() {
            @Override
            public void onCompleted(List<Attraction> list) {

                ArrayList<Attraction> filtered = new ArrayList<>();

                for (Attraction a : list) {
                    if (a.getCountry().equals(travel.getName())) {
                        filtered.add(a);
                    }
                }

                AttractionAdapter dialogAdapter =
                        new AttractionAdapter(filtered,
                                new AttractionAdapter.OnAttrctionClickListener() {

                                    @Override
                                    public void onAttractionClick(Attraction attraction) {

                                       travel.getAttractionList().add(attraction);
                                       adapter.notifyDataSetChanged();

                                       databaseService.updateTravel(travel, new DatabaseService.DatabaseCallback<Void>() {
                                           @Override
                                           public void onCompleted(Void object) {

                                           }

                                           @Override
                                           public void onFailed(Exception e) {

                                           }
                                       });
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onLongAttractionClick(Attraction attraction) {
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

        dialog.show();
    }

    private void saveTravel() {

        travel.setName(etName.getText().toString());
        travel.setDetails(etDetails.getText().toString());

        databaseService.updateTravel(travel, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("Save", "error", e);
            }
        });
    }
}