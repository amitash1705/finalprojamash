package com.example.finalprojamash;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

public class AddNewTravelamash extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="ReadAttraction" ;
    DatabaseService databaseService;
    ArrayList<Attraction> attractionsList;
    RecyclerView rcAttraction;
    AttractionAdapter adapter;

    EditText  etTravelDetails;
    Spinner spNameCounty;
    ArrayList<Attraction> attractionArrayListTravel=new ArrayList<>();

    Button btnAddNewTravel;

    TextView tvSelectedAttraction;

    String stAttraction="";
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

                Log.d(TAG, "onCompleted: " + object);
                attractionsList.clear();
                attractionsList.addAll(object);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: ", e);
            }
        });

    }

    private void initViews() {
        databaseService=DatabaseService.getInstance();
        rcAttraction=findViewById(R.id.rvAttractions);

        rcAttraction.setLayoutManager(new LinearLayoutManager(this));

        attractionsList=new ArrayList<>();


        adapter = new AttractionAdapter( attractionsList, new AttractionAdapter.OnAttrctionClickListener() {
            @Override
            public void onAttractionClick(Attraction attraction) {

                attractionArrayListTravel.add(attraction);

                stAttraction+=attraction.getName()+ ", ";
                tvSelectedAttraction.setText(stAttraction);

                Log.d(TAG, "attraction Added: " +attractionArrayListTravel.size());

            }

            @Override
            public void onLongAttractionClick(Attraction attraction) {

                attractionArrayListTravel.remove(attraction);
                Log.d(TAG, "attraction Remove: " +attractionArrayListTravel.size());

            }


        });
        rcAttraction.setAdapter(adapter);

        etTravelDetails=findViewById(R.id.etTravelDetails);
        spNameCounty=findViewById(R.id.spCountry2);

        tvSelectedAttraction=findViewById(R.id.tvSelectedAttraction);

        btnAddNewTravel=findViewById(R.id.btnAddTravel);
        btnAddNewTravel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}