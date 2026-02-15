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

//
import android.widget.EditText;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;


import com.example.finalprojamash.adapter.ImageSourceAdapter;
import com.example.finalprojamash.model.ImageSourceOption;
import com.example.finalprojamash.utils.ImageUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class UserProfileamash extends AppCompatActivity {

    private ImageView ivProfilePic;
    private Button btnLogout, btnChangeUsername, btnChangePassword;
    private TextView tvUsername, tvSettingsTitle;

    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profileamash);

        // --- חיבור ל־Views ---
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnChangeUsername = findViewById(R.id.btnChangeUsername);

        btnLogout = findViewById(R.id.btnLogout);
        tvUsername = findViewById(R.id.tvUsername);
        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);

        //
        // --- הצגת username או email ---
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            String displayName = user.getDisplayName();
            String email = user.getEmail();

            if (displayName == null || displayName.isEmpty()) {
                tvUsername.setText(email);
            } else {
                tvUsername.setText(displayName);
            }
        }




        // --- להתחלה נסתר
        btnChangeUsername.setVisibility(View.GONE);

        btnLogout.setVisibility(View.GONE);

        // --- לחיצה על כותרת Settings ---
        tvSettingsTitle.setOnClickListener(v -> {
            if (btnChangeUsername.getVisibility() == View.GONE) {
                btnChangeUsername.setVisibility(View.VISIBLE);

                btnLogout.setVisibility(View.VISIBLE);
            } else {
                btnChangeUsername.setVisibility(View.GONE);

                btnLogout.setVisibility(View.GONE);
            }
        });

        // --- שינוי שם משתמש ---
        btnChangeUsername.setOnClickListener(v -> showChangeUsernameDialog());

        // --- Logout ---
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileamash.this, Loginamash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // --- שינוי תמונה ---
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {



                                                showImageSourceDialog();
                                            }
                                        });



        // --- EdgeToEdge Padding ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- ActivityResultLaunchers ---
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        ivProfilePic.setImageURI(selectedImage);
                        ivProfilePic.setTag(null);
                    }
                });

        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        ivProfilePic.setImageBitmap(bitmap);
                        ivProfilePic.setTag(null);
                    }
                });
    }

    // --- Dialog לשינוי שם משתמש ---
    private void showChangeUsernameDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter new username");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                // עדכון TextView
                tvUsername.setText(newUsername);

                // עדכון Firebase
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(
                            new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUsername)
                                    .build()
                    ).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // --- Dialog לבחירת מקור תמונה ---
    private void showImageSourceDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_image_source, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ArrayList<ImageSourceOption> options = new ArrayList<>();
        options.add(new ImageSourceOption(getString(R.string.gallery_title), getString(R.string.gallery_description), R.drawable.gallery_thumbnail));
        options.add(new ImageSourceOption(getString(R.string.camera_title), getString(R.string.camera_description), R.drawable.photo_camera));

        ListView listView = bottomSheetView.findViewById(R.id.list_view_image_sources);
        ImageSourceAdapter adapter = new ImageSourceAdapter(this, options, option -> {
            bottomSheetDialog.dismiss();
            ImageUtil.requestPermission(this);
            if (option.getTitle().equals(getString(R.string.gallery_title))) {
                selectImageFromGallery();
            } else if (option.getTitle().equals(getString(R.string.camera_title))) {
                captureImageFromCamera();
            }
        });
        listView.setAdapter(adapter);
        bottomSheetDialog.show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    private void captureImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(intent);
    }
}
