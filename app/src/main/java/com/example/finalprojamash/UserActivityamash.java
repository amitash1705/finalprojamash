package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.adapter.UserAdapter;
import com.example.finalprojamash.model.User;
import com.example.finalprojamash.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class UserActivityamash extends AppCompatActivity {


    private static final String TAG = "UsersListActivity";
    private UserAdapter userAdapter;
    private RecyclerView rcUsers;

    DatabaseService databaseService;

    ArrayList<User>usersList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_activityamash);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    databaseService=DatabaseService.getInstance();


    rcUsers = findViewById(R.id.rcUsers);

        rcUsers.setLayoutManager(new LinearLayoutManager(this));
    userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
        @Override
        public void onUserClick(User user) {
            // Handle user click
            Log.d(TAG, "User clicked: " + user);
         //   Intent intent = new Intent(UserActivityamash.this, UserProfileActivity.class);
          //  intent.putExtra("USER_UID", user.getId());
          //  startActivity(intent);
        }

        @Override
        public void onLongUserClick(User user) {
            // Handle long user click
            Log.d(TAG, "User long clicked: " + user);
        }
    });
        rcUsers.setAdapter(userAdapter);
}


@Override
protected void onResume() {
    super.onResume();
    databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
        @Override
        public void onCompleted(List<User> users) {
            usersList.addAll(users);
            userAdapter.setUserList(usersList);
            userAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(Exception e) {

        }
    });
}

}
