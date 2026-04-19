package com.example.finalprojamash;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class Travelprofileamash extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EditTravel";


    Button btnSave, btnBack;
    EditText etName, etDetails;

    DatabaseService databaseService;
    ArrayList<Attraction> attractionsList;
    RecyclerView rcAttraction;
    AttractionAdapter adapter;

    String travelId;
    Travel travel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_travelprofileamash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        travelId = getIntent().getStringExtra("travelId");
        databaseService = DatabaseService.getInstance();

        initViews();


        databaseService.getTravel(travelId, new DatabaseService.DatabaseCallback<Travel>() {
            @Override
            public void onCompleted(Travel travel2) {
                travel = travel2;

                etName.setText(travel.getName());
                etDetails.setText(travel.getDetails());

                attractionsList.addAll(travel2.getAttractionList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });


    }


    private void initViews() {

        etName = findViewById(R.id.ettravelnameprofile);
        etDetails = findViewById(R.id.ettraveldetailsprofile);

        btnSave = findViewById(R.id.btnsaveedittravelprofile);

        btnSave.setOnClickListener(this);
        btnBack = findViewById(R.id.btnbacktravelprofile);

        btnBack.setOnClickListener(this);

        rcAttraction = findViewById(R.id.rvtravelAttractionProfile2);
        rcAttraction.setLayoutManager(new LinearLayoutManager(this));


        attractionsList = new ArrayList<>();

        adapter = new AttractionAdapter(attractionsList, new AttractionAdapter.OnAttrctionClickListener() {
            @Override
            public void onAttractionClick(Attraction attraction) {

                //  attractionsList.add(attraction);

                //  Log.d(TAG, "attraction Added: " +attractionsList.size());

            }

            @Override
            public void onLongAttractionClick(Attraction attraction) {




                AlertDialog.Builder builder = new AlertDialog.Builder(Travelprofileamash.this);
                builder.setTitle("אישור מחיקה");
                builder.setMessage("האם ברצונך למחוק את הטרקציה?");

                builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // כאן תשים את הקוד שמבצע את המחיקה


                        attractionsList.remove(attraction);
                        adapter.notifyDataSetChanged();


                        databaseService.updateTravel(travel, new DatabaseService.DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void object) {

                            }

                            @Override
                            public void onFailed(Exception e) {

                            }
                        });

                        //updateTrip
                    }
                });

                builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();





                // Log.d(TAG, "attraction Remove: " +attractionArrayListTravel.size());

            }


        });
        rcAttraction.setAdapter(adapter);

    }


    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("אישור מחיקה");
        builder.setMessage("האם ברצונך למחוק את הטרקציה?");

        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // כאן תשים את הקוד שמבצע את המחיקה
            }
        });

        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onClick(View v) {

        if(v==btnSave){

            String travelName=etName.getText().toString();
            String travelDetails=etDetails.getText().toString();
            //בדיקת תקינות

            travel.setDetails(travelDetails);
            travel.setName(travelName);
            databaseService.updateTravel(travel, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {

                    //toast

                }

                @Override
                public void onFailed(Exception e) {

                    //toast

                }
            });

            finish();


        }

        if(v==btnBack){
            finish();
        }

    }
}
