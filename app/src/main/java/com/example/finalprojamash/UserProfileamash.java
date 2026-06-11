package com.example.finalprojamash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// שדות קלט למשתמש
import android.widget.EditText;

import com.example.finalprojamash.model.User;
import com.example.finalprojamash.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import com.example.finalprojamash.adapter.ImageSourceAdapter;
import com.example.finalprojamash.model.ImageSourceOption;
import com.example.finalprojamash.utils.ImageUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class UserProfileamash extends AppCompatActivity {

    // רכיב שמציג את תמונת הפרופיל של המשתמש
    private ImageView ivProfilePic;

    // כפתור יציאה מהמערכת
    private Button btnLogout;

    // כפתור שינוי שם משתמש
    private Button btnChangeUsername;

    // טקסט שמציג את שם המשתמש
    private TextView tvUsername;

    // כותרת הגדרות (משמשת גם ככפתור פתיחה/סגירה של תפריט)
    private TextView tvSettingsTitle;

    // מערכת חדשה של Android לבחירת תמונות (גלריה/מצלמה)
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    // מזהה לבחירת תמונה ישנה (deprecated API)
    int SELECT_PICTURE = 200;

    // Firebase Authentication - אחראי על משתמש מחובר
    FirebaseAuth auto;

    // מזהה המשתמש המחובר כרגע (UID של Firebase)
    String userId;

    // אובייקט של המשתמש מהדאטהבייס (כולל שם, תמונה וכו')
    User currentUser = null;

    // שירות שמתקשר עם הדאטהבייס (Firestore / Realtime DB)
    DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // הפעלת UI מודרני שבו התוכן נכנס מתחת לסטטוס בר
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_profileamash);

        // חיבור בין XML לקוד (מצא את האלמנטים במסך)
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnChangeUsername = findViewById(R.id.btnChangeUsername);
        btnLogout = findViewById(R.id.btnLogout);
        tvUsername = findViewById(R.id.tvUsername);
        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);

        // התחברות ל-Firebase Authentication
        auto = FirebaseAuth.getInstance();

        // קבלת ה-UID של המשתמש המחובר
        userId = auto.getUid();

        // יצירת instance של שירות הדאטהבייס
        databaseService = DatabaseService.getInstance();

        /*
         * כאן מתבצעת פעולה קריטית:
         * שליפת המשתמש מהדאטהבייס לפי UID
         * זה מה שממלא את המסך בנתונים אמיתיים
         */
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {

            @Override
            public void onCompleted(User user) {

                // שמירת המשתמש בזיכרון לשימוש בהמשך (עדכון תמונה/שם וכו')
                currentUser = user;

                // הצגת שם המשתמש במסך
                tvUsername.setText(currentUser.getFname());

                /*
                 * בדיקה כפולה לשם המשתמש:
                 * אם אין שם - עדיין לא לשבור UI
                 */
                if (currentUser != null) {

                    String displayName = currentUser.getFname();

                    if (displayName == null || displayName.isEmpty()) {
                        tvUsername.setText(displayName);
                    } else {
                        tvUsername.setText(displayName);
                    }
                }

                /*
                 * אם קיימת תמונה שמורה בדאטהבייס:
                 * ממירים אותה מ-Base64 לתמונה ומציגים
                 */
                if (currentUser.getPic() != null)
                    ivProfilePic.setImageBitmap(
                            ImageUtil.convertFrom64base(currentUser.getPic())
                    );
            }

            @Override
            public void onFailed(Exception e) {
                // במקרה של כשל בטעינת משתמש (כרגע לא מטופל)
            }
        });

        /*
         * בהתחלה מסתירים כפתורים מתקדמים
         * כדי לשמור על UI נקי
         */
        btnChangeUsername.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);

        /*
         * לחיצה על Settings:
         * פותחת/סוגרת תפריט הגדרות
         */
        tvSettingsTitle.setOnClickListener(v -> {

            if (btnChangeUsername.getVisibility() == View.GONE) {

                btnChangeUsername.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.VISIBLE);

            } else {

                btnChangeUsername.setVisibility(View.GONE);
                btnLogout.setVisibility(View.GONE);
            }
        });

        /*
         * כפתור שינוי שם:
         * פותח דיאלוג להזנת שם חדש
         */
        btnChangeUsername.setOnClickListener(v -> showChangeUsernameDialog());

        /*
         * יציאה מהמערכת:
         * סוגר משתמש ב-Firebase ומחזיר למסך התחברות
         */
        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(this,
                    "You have been logged out",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserProfileamash.this, Loginamash.class);

            // מנקה היסטוריה כדי שלא יחזור אחורה
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        });

        /*
         * לחיצה על תמונת פרופיל:
         * מאפשרת למשתמש לשנות תמונה
         */
        ivProfilePic.setOnClickListener(v -> showImageSourceDialog());

        /*
         * התאמת padding למסך (מערכת Android UI מודרנית)
         */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom);

            return insets;
        });

        /*
         * מערכת חדשה לבחירת תמונה מהגלריה
         */
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Uri selectedImage = result.getData().getData();

                        ivProfilePic.setImageURI(selectedImage);

                        // מאפס תג (לא חובה אבל שימושי לניהול מצב)
                        ivProfilePic.setTag(null);
                    }
                });

        /*
         * מערכת חדשה לצילום תמונה מהמצלמה
         */
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Bitmap bitmap =
                                (Bitmap) result.getData()
                                        .getExtras()
                                        .get("data");

                        ivProfilePic.setImageBitmap(bitmap);

                        ivProfilePic.setTag(null);

                        /*
                         * שמירת תמונה חדשה למשתמש:
                         * המרה ל-Base64 ואז עדכון בדאטהבייס
                         */
                        currentUser.setPic(
                                ImageUtil.convertTo64Base(ivProfilePic)
                        );

                        databaseService.updateUser(
                                currentUser,
                                new DatabaseService.DatabaseCallback<Void>() {

                                    @Override
                                    public void onCompleted(Void object) {
                                        // הצלחה בעדכון
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        // כשל בעדכון
                                    }
                                });
                    }
                });
    }

    /*
     * דיאלוג לשינוי שם משתמש:
     * משתמש מזין שם חדש -> נשמר בדאטהבייס
     */
    private void showChangeUsernameDialog() {

        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this);

        builder.setTitle("Change Username");

        final android.widget.EditText input =
                new android.widget.EditText(this);

        input.setHint("Enter new username");

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {

            String newUsername = input.getText().toString().trim();

            if (!newUsername.isEmpty()) {

                // עדכון UI מידי
                tvUsername.setText(newUsername);

                // עדכון אובייקט המשתמש
                currentUser.setFname(newUsername);

                // שמירת שינוי בדאטהבייס
                databaseService.updateUser(
                        currentUser,
                        new DatabaseService.DatabaseCallback<Void>() {

                            @Override
                            public void onCompleted(Void object) {
                            }

                            @Override
                            public void onFailed(Exception e) {
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /*
     * דיאלוג בחירת מקור תמונה:
     * גלריה או מצלמה (Bottom Sheet)
     */
    private void showImageSourceDialog() {

        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this);

        View bottomSheetView =
                getLayoutInflater()
                        .inflate(R.layout.bottom_sheet_image_source, null);

        bottomSheetDialog.setContentView(bottomSheetView);

        ArrayList<ImageSourceOption> options = new ArrayList<>();

        // אפשרות גלריה
        options.add(new ImageSourceOption(
                getString(R.string.gallery_title),
                getString(R.string.gallery_description),
                R.drawable.gallery_thumbnail));

        // אפשרות מצלמה
        options.add(new ImageSourceOption(
                getString(R.string.camera_title),
                getString(R.string.camera_description),
                R.drawable.photo_camera));

        ListView listView =
                bottomSheetView.findViewById(R.id.list_view_image_sources);

        ImageSourceAdapter adapter =
                new ImageSourceAdapter(this, options, option -> {

                    bottomSheetDialog.dismiss();

                    ImageUtil.requestPermission(this);

                    if (option.getTitle().equals(getString(R.string.gallery_title))) {
                        selectImageFromGallery();
                    } else {
                        captureImageFromCamera();
                    }
                });

        listView.setAdapter(adapter);
        bottomSheetDialog.show();
    }

    // פתיחת גלריה
    private void selectImageFromGallery() {
        imageChooser();
    }

    // פתיחת מצלמה
    private void captureImageFromCamera() {
        Intent intent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        captureImageLauncher.launch(intent);
    }

    /*
     * בחירת תמונה ישנה (deprecated)
     * עדיין עובד אבל פחות מומלץ
     */
    void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(
                Intent.createChooser(i, "Select Picture"),
                SELECT_PICTURE);
    }

    /*
     * טיפול בתוצאה של בחירת תמונה (ישן)
     */
    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {

                    ivProfilePic.setImageURI(selectedImageUri);

                    // שמירת תמונה חדשה לדאטהבייס
                    currentUser.setPic(
                            ImageUtil.convertTo64Base(ivProfilePic));

                    databaseService.updateUser(
                            currentUser,
                            new DatabaseService.DatabaseCallback<Void>() {

                                @Override
                                public void onCompleted(Void object) {
                                }

                                @Override
                                public void onFailed(Exception e) {
                                }
                            });
                }
            }
        }
    }
}