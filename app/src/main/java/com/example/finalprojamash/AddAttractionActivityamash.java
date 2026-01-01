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

public class AddAttractionActivityamash extends AppCompatActivity {


    ImageView imageView;

    /// Activity result launcher for selecting image from gallery
    private ActivityResultLauncher<Intent> selectImageLauncher;
    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;


    private EditText etCity, etPrice, etDetails, etName, etAge, etAdress, etWeb ;
    private Spinner spType, spCountry;
    private Button btnGallery, btnPhoto, btnAddNewAttraction;


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_attraction_activityamash);

        etCity = findViewById(R.id.etCity);
        etPrice = findViewById(R.id.etPrice);
        etDetails = findViewById(R.id.etDetails);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etAdress = findViewById(R.id.etAdress);
        etWeb = findViewById(R.id.etWeb);

        spType = findViewById(R.id.spType);
        spCountry = findViewById(R.id.spCountry);

        btnGallery = findViewById(R.id.btnGallery);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnAddNewAttraction = findViewById(R.id.btnAddNewAttraction);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView3);
        setUpSpinner();
        setUpGallery();

        Button btnAddNewAttraction = findViewById(R.id.btnAddNewAttraction);
        btnAddNewAttraction.setOnClickListener(v -> {

            addToDB();
        });

    }

    private void addToDB() {
        String city = etCity.getText().toString()+"";
        String price = etPrice.getText().toString()+"";
        String details = etDetails.getText().toString()+"";
        String name = etName.getText().toString()+"";
        String age = etAge.getText().toString()+"";
        String adress = etAdress.getText().toString()+"";
        String web = etWeb.getText().toString()+"";


       String type = spType.getSelectedItem().toString();
       String country = spCountry.getSelectedItem().toString();

        // בדיקה שכל השדות מלאים
        if (city.isEmpty() || price.isEmpty() || details.isEmpty() || name.isEmpty() || age.isEmpty() || adress.isEmpty() || web.isEmpty()) {
            Toast.makeText(this, "fill all information please", Toast.LENGTH_SHORT).show();
            return;
        }


        // create attraction
        String id = DatabaseService.getInstance().generateAttractionId();
        String pic = ImageUtil.convertTo64Base(imageView);
        Attraction attraction = new Attraction(id,name,country,type,adress,city,Double.parseDouble(price),pic,age,details,web);

        ///  call to database service
        DatabaseService.getInstance().createNewAttraction(attraction, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(AddAttractionActivityamash.this, "Yay!", Toast.LENGTH_SHORT).show();
                finish(); // סוגר את דף האודות כדי שלא יחזור אליו בלחיצה על BACK
            }

            @Override
            public void onFailed(Exception e) {

            }
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