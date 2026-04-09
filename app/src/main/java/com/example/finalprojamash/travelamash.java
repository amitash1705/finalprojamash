package com.example.finalprojamash;

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



    private static final String TAG ="ReadUserTravel" ;
    DatabaseService databaseService;


    TravelAdapter adapter;
    List<Travel> travelList = new ArrayList<>();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_travelamash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseService=DatabaseService.getInstance();
        recyclerView = findViewById(R.id.rvTravel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


         adapter = new TravelAdapter(travelamash.this, travelList, new TravelAdapter.OnTravelClickListener() {
            @Override
            public void onEditClick(Travel travel, int position) {
                // קוד לפתיחת מסך עריכה


            }

            @Override
            public void onDeleteClick(Travel travel, int position) {
                travelList.remove(position);
                adapter.notifyItemRemoved(position);
                // אופציונלי: למחוק גם מה־Database

                deleteTravelFromDatabase( travel.getId());
            }

            @Override
            public void onItemClick(Travel travel, int position) {
                // לחיצה על הכרטיס עצמו
            }
        });

        recyclerView.setAdapter(adapter);

         databaseService.getUserTravelList(new DatabaseService.DatabaseCallback<List<Travel>>() {
             @Override
             public void onCompleted(List<Travel> object) {

                 travelList.addAll(object);
                 adapter.notifyDataSetChanged();

             }

             @Override
             public void onFailed(Exception e) {

             }
         });




    }

    private void deleteTravelFromDatabase(String id) {

        databaseService.deleteTravel(id, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

                // toast

            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}