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

    private static final String TAG ="ReadAttraction" ;

    DatabaseService databaseService;

    ArrayList<Attraction> attractionsList, displayList=new ArrayList<>();

    RecyclerView rcAttraction;

    AttractionAdapter adapter;

    EditText  etTravelDetails;
    Spinner spNameCounty;

    ArrayList<Attraction> attractionArrayListTravel=new ArrayList<>();

    Button btnAddNewTravel;

    TextView tvSelectedAttraction;

    String stAttraction="";

    String country="";

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
                displayList.addAll(object);
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

        adapter = new AttractionAdapter( displayList, new AttractionAdapter.OnAttrctionClickListener() {
            @Override
            public void onAttractionClick(Attraction attraction) {

                if(attractionArrayListTravel.contains(attraction)) {
                    Toast.makeText(AddNewTravelamash.this,
                            "You Picked This Attraction Already",
                            Toast.LENGTH_SHORT).show();
                } else {

                    attractionArrayListTravel.add(attraction);

                    stAttraction += attraction.getName() + ", ";
                    tvSelectedAttraction.setText(stAttraction);

                    Log.d(TAG, "attraction Added: " + attractionArrayListTravel.size());
                }

            }
            @Override
            public void onLongAttractionClick(Attraction attraction) {

                attractionArrayListTravel.remove(attraction);
                String st="";
                for(int i=0;i<attractionArrayListTravel.size();i++)
                    st+=attractionArrayListTravel.get(i).getName();

                tvSelectedAttraction.setText(st);
                Log.d(TAG, "attraction Remove: " +attractionArrayListTravel.size());

            }

        });
        rcAttraction.setAdapter(adapter);

        etTravelDetails=findViewById(R.id.etTravelDetails);
        spNameCounty=findViewById(R.id.spCountry2);
        spNameCounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                country= (String) parent.getItemAtPosition(position);

                filter(country);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvSelectedAttraction=findViewById(R.id.tvSelectedAttraction);

        btnAddNewTravel=findViewById(R.id.btnAddTravel);
        btnAddNewTravel.setOnClickListener(this);

    }

    public void filter(String text) {
        displayList.clear();

        if (text.isEmpty()) {
            displayList.addAll(attractionsList);
        } else {
            for (Attraction  item : attractionsList) {
                if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                    displayList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        String travelId=databaseService.generateTravelId();


        String details = etTravelDetails.getText().toString().trim();

// בדיקה אם שם הטיול ריק
        if (details.isEmpty()) {
            etTravelDetails.setError("Please enter travel details");
            etTravelDetails.requestFocus();
            return;
        }



        Travel  newTravel= new Travel(travelId,country,attractionArrayListTravel,details);

        if(attractionArrayListTravel!=null && attractionArrayListTravel.size()>0) {

            databaseService.createNewTravel(newTravel, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {


                    Intent intent1 = new Intent(AddNewTravelamash.this, UserPageActivity.class);
                    startActivity(intent1);

                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
        else {

            Toast.makeText(AddNewTravelamash.this, "please pick an attraction", Toast.LENGTH_SHORT).show();

        }

        }
}