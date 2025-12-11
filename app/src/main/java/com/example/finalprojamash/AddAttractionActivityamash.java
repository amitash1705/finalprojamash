package com.example.finalprojamash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalprojamash.utils.ImageUtil;

public class AddAttractionActivityamash extends AppCompatActivity {


    ImageView imageView;

    /// Activity result launcher for selecting image from gallery
    private ActivityResultLauncher<Intent> selectImageLauncher;
    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_attraction_activityamash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView3);
        setUpSpinner();
        setUpGallery();

        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(AddAttractionActivityamash.this, AdminActivityamash.class);
            startActivity(intent);
            finish(); // סוגר את דף האודות כדי שלא יחזור אליו בלחיצה על BACK
        });

    }

    private void setUpGallery() {
        /// register the activity result launcher for selecting image from gallery
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        imageView.setImageURI(selectedImage);
                        /// set the tag for the image view to null
                        imageView.setTag(null);
                    }
                });

        /// register the activity result launcher for capturing image from camera
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(bitmap);
                        /// set the tag for the image view to null
                        imageView.setTag(null);
                    }
                });


        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.requestPermission(AddAttractionActivityamash.this);
                selectImageFromGallery();
            }
        });


        findViewById(R.id.btnPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.requestPermission(AddAttractionActivityamash.this);
                captureImageFromCamera();
            }
        });
    }

    private void setUpSpinner() {
        // ======== כאן מתחיל הקוד של ה-Spinner ========
        Spinner spinner = findViewById(R.id.spCountry);

        // יצירת Adapter מהרשימה ב-strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countryArr, android.R.layout.simple_spinner_item);

        // איך הרשימה תוצג כשפותחים את ה-Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // חיבור ה-Adapter ל-Spinner
        spinner.setAdapter(adapter);


        // ======== Spinner שני - סוג אטרקציה ========
        Spinner typeArr = findViewById(R.id.spType);

        ArrayAdapter<CharSequence> adapterAttraction = ArrayAdapter.createFromResource(this,
                R.array.typeArr, android.R.layout.simple_spinner_item);

        adapterAttraction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeArr.setAdapter(adapterAttraction);
        typeArr.setSelection(0); // פריט ברירת מחדל
        // ======== סוף Spinner שני ========
    }


    /// select image from gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    /// capture image from camera
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }
}