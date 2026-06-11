package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

    // תגית ללוגים לצורך בדיקות (Debug)
    private static final String TAG = "UsersListActivity";

    // Adapter שמחבר בין הנתונים לבין ה-RecyclerView
    private UserAdapter userAdapter;

    // רשימת ה-RecyclerView
    private RecyclerView rcUsers;

    // שירות דאטהבייס (Firebase / DB אחר)
    DatabaseService databaseService;

    // רשימת המשתמשים שנשמרת בזיכרון
    ArrayList<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI מודרני (מסך מלא)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_activityamash);

        // התאמת padding למערכת (סטטוס בר + ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול שירות הדאטהבייס
        databaseService = DatabaseService.getInstance();

        // חיבור RecyclerView מה-XML
        rcUsers = findViewById(R.id.rcUsers);

        // הגדרת תצוגה אנכית (רשימה)
        rcUsers.setLayoutManager(new LinearLayoutManager(this));

        // יצירת Adapter עם פעולות על משתמשים
        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {

            // לחיצה רגילה על משתמש
            @Override
            public void onUserClick(User user) {

                Log.d(TAG, "User clicked: " + user);

                // כאן אפשר לפתוח מסך פרופיל משתמש
                // Intent intent = new Intent(UserActivityamash.this, UserProfileActivity.class);
                // intent.putExtra("USER_UID", user.getId());
                // startActivity(intent);
            }

            // לחיצה ארוכה = מחיקת משתמש
            @Override
            public void onLongUserClick(User user) {

                // יצירת חלון אישור לפני מחיקה
                new AlertDialog.Builder(UserActivityamash.this)
                        .setTitle("מחיקת משתמש")
                        .setMessage("האם את בטוחה שברצונך למחוק את המשתמש?")

                        // כפתור מחיקה
                        .setPositiveButton("מחק", (dialog, which) -> {

                            // מחיקה מהדאטהבייס
                            databaseService.deleteUser(user.getId(),
                                    new DatabaseService.DatabaseCallback<Void>() {
                                        @Override
                                        public void onCompleted(Void object) {

                                            // עדכון הרשימה אחרי מחיקה
                                            usersList.remove(user);
                                            userAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailed(Exception e) {

                                            // במקרה של שגיאה עדיין מסירים מהמסך (התנהגות לא מומלצת אבל קיימת בקוד)
                                            usersList.remove(user);
                                            userAdapter.notifyDataSetChanged();
                                        }
                                    });
                        })

                        // כפתור ביטול
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();

                Log.d(TAG, "User long clicked: " + user);
            }
        });

        // חיבור האדפטר לרשימה
        rcUsers.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // בכל חזרה למסך – טוענים מחדש את רשימת המשתמשים
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {

                // הוספת הנתונים לרשימה
                usersList.addAll(users);

                // עדכון האדפטר
                userAdapter.setUserList(usersList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                // טיפול בשגיאה (כרגע ריק)
            }
        });
    }
}