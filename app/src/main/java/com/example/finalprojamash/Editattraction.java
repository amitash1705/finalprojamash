package com.example.finalprojamash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.services.DatabaseService;
import com.example.finalprojamash.utils.ImageUtil;

public class Editattraction extends AppCompatActivity {

    ImageView imageView;

    private EditText editCity, editPrice, editDetails, editName, editAge, editAdress, editWeb;
    private Spinner spType, spCountry;

    private Button btnGallery, btnPhoto, btnSave;

    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editattraction);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpinners();
        setupImagePickers();
    }

    private void initViews() {

        editCity = findViewById(R.id.editCity);
        editPrice = findViewById(R.id.editPrice);
        editDetails = findViewById(R.id.editDetails);
        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editAdress = findViewById(R.id.editAdress);
        editWeb = findViewById(R.id.editWeb);

        spType = findViewById(R.id.speditType);
        spCountry = findViewById(R.id.speditCountry);

        imageView = findViewById(R.id.imageView3);

        btnGallery = findViewById(R.id.btneditGallery);
        btnPhoto = findViewById(R.id.btneditPhoto);
        btnSave = findViewById(R.id.btneditAttraction);

        btnSave.setOnClickListener(v -> saveAttraction());
    }

    private void setupSpinners() {

        ArrayAdapter<CharSequence> countryAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.countryArr,
                        android.R.layout.simple_spinner_item
                );

        countryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCountry.setAdapter(countryAdapter);


        ArrayAdapter<CharSequence> typeAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.typeArr,
                        android.R.layout.simple_spinner_item
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spType.setAdapter(typeAdapter);
    }

    private void setupImagePickers() {

        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                }
        );

        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(bitmap);
                    }
                }
        );

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        });

        btnPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureImageLauncher.launch(intent);
        });
    }

    private void saveAttraction() {

        String city = editCity.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String details = editDetails.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String adress = editAdress.getText().toString().trim();
        String web = editWeb.getText().toString().trim();

        String type = spType.getSelectedItem().toString();
        String country = spCountry.getSelectedItem().toString();

        if (city.isEmpty() || price.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "מלא לפחות שדות חובה", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = DatabaseService.getInstance().generateAttractionId();
        String image = ImageUtil.convertTo64Base(imageView);

        Attraction attraction = new Attraction(
                id,
                name,
                country,
                type,
                adress,
                city,
                Double.parseDouble(price),
                image,
                age,
                details,
                web
        );

        DatabaseService.getInstance().createNewAttraction(
                attraction,
                new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void v) {
                        Toast.makeText(Editattraction.this, "נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(Editattraction.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}