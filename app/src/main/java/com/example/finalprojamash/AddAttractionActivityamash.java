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

import java.util.List;

public class AddAttractionActivityamash extends AppCompatActivity {


    ImageView imageView;

    /// Launcher לבחירת תמונה מהגלריה
    private ActivityResultLauncher<Intent> selectImageLauncher;

    /// Launcher לצילום תמונה מהמצלמה
    private ActivityResultLauncher<Intent> captureImageLauncher;


    private EditText etCity, etPrice, etDetails, etName, etAge, etAdress, etWeb;
    private Spinner spType, spCountry;
    private Button btnGallery, btnPhoto, btnAddNewAttraction;


    // קוד לזיהוי בחירת תמונה (גלריה)
    int SELECT_PICTURE = 200;

    DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_attraction_activityamash);


        databaseService = DatabaseService.getInstance();

        // חיבור כל שדות הטקסט מהמסך
        etCity = findViewById(R.id.etCity);
        etPrice = findViewById(R.id.etPrice);
        etDetails = findViewById(R.id.etDetails);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etAdress = findViewById(R.id.etAdress);
        etWeb = findViewById(R.id.etWeb);

        // חיבור ה-Spinner של סוג ושל מדינה
        spType = findViewById(R.id.spType);
        spCountry = findViewById(R.id.spCountry);

        // כפתורי פעולה
        btnGallery = findViewById(R.id.btnGallery);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnAddNewAttraction = findViewById(R.id.btnAddNewAttraction);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView3);

        // הגדרת ה-Spinnerים (מדינה וסוג אטרקציה)
        setUpSpinner();

        // הגדרת בחירת תמונה מהגלריה/מצלמה
        setUpGallery();

        Button btnAddNewAttraction = findViewById(R.id.btnAddNewAttraction);

        // לחיצה על הוספת אטרקציה
        btnAddNewAttraction.setOnClickListener(v -> {
            addToDB();
        });

    }

    private void addToDB() {

        // קריאת כל הנתונים מהטופס
        String city = etCity.getText().toString() + "";
        String price = etPrice.getText().toString() + "";
        String details = etDetails.getText().toString() + "";
        String name = etName.getText().toString() + "";
        String age = etAge.getText().toString() + "";
        String adress = etAdress.getText().toString() + "";
        String web = etWeb.getText().toString() + "";

        // קבלת ערכים מה-Spinner
        String type = spType.getSelectedItem().toString();
        String country = spCountry.getSelectedItem().toString();

        // בדיקה שכל השדות מלאים
        if (city.isEmpty() || price.isEmpty() || details.isEmpty() || name.isEmpty() || age.isEmpty() || adress.isEmpty() || web.isEmpty()) {
            Toast.makeText(this, "fill all information please", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקה אם האטרקציה כבר קיימת לפי שם ומדינה
        isAttractionExists(name, country, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {

                if (exists) {
                    System.out.println("Exists");

                    Toast.makeText(AddAttractionActivityamash.this, "the attraction is already exists", Toast.LENGTH_SHORT).show();

                } else {

                    // יצירת מזהה חדש לאטרקציה
                    String id = DatabaseService.getInstance().generateAttractionId();

                    // המרת תמונה ל-Base64
                    String pic = ImageUtil.convertTo64Base(imageView);

                    // יצירת אובייקט אטרקציה חדש
                    Attraction attraction = new Attraction(id, name, country, type, adress, city, Double.parseDouble(price), pic, age, details, web);

                    // שמירה למסד הנתונים
                    databaseService.createNewAttraction(attraction, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void v) {

                            Toast.makeText(AddAttractionActivityamash.this, "Yay!", Toast.LENGTH_SHORT).show();

                            // סגירת המסך וחזרה אחורה
                            finish();
                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });

                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void setUpGallery() {

        /// רישום פתיחת גלריה ובחירת תמונה
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        imageView.setImageURI(selectedImage);

                        // איפוס תגית התמונה
                        imageView.setTag(null);
                    }
                });

        /// רישום פתיחת מצלמה וצילום תמונה
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(bitmap);

                        // איפוס תגית התמונה
                        imageView.setTag(null);
                    }
                });

        // כפתור פתיחת גלריה
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.requestPermission(AddAttractionActivityamash.this);
                selectImageFromGallery();
            }
        });

        // כפתור פתיחת מצלמה
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.requestPermission(AddAttractionActivityamash.this);
                captureImageFromCamera();
            }
        });
    }

    private void setUpSpinner() {

        // ===== Spinner מדינות =====
        Spinner spinner = findViewById(R.id.spCountry);

        // יצירת Adapter מרשימת המדינות בקובץ strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countryArr, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // חיבור ה-Adapter ל-Spinner
        spinner.setAdapter(adapter);

        // ===== Spinner סוג אטרקציה =====
        Spinner typeArr = findViewById(R.id.spType);

        ArrayAdapter<CharSequence> adapterAttraction = ArrayAdapter.createFromResource(this,
                R.array.typeArr, android.R.layout.simple_spinner_item);

        adapterAttraction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeArr.setAdapter(adapterAttraction);

        // בחירת ערך ברירת מחדל
        typeArr.setSelection(0);
    }

    /// פתיחת גלריה
    private void selectImageFromGallery() {
        imageChooser();
    }

    /// פתיחת מצלמה
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }

    void imageChooser() {

        // יצירת Intent לבחירת תמונה מהגלריה
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // פתיחת מסך בחירת תמונה
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // טיפול בתוצאה של בחירת תמונה
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();

                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                }
            }
        }
    }

    // בדיקה אם אטרקציה כבר קיימת במסד הנתונים
    public void isAttractionExists(
            String name,
            String country,
            DatabaseService.DatabaseCallback<Boolean> callback) {

        databaseService.getAttractionList(new DatabaseService.DatabaseCallback<List<Attraction>>() {

            @Override
            public void onCompleted(List<Attraction> list) {

                if (list == null) {
                    callback.onCompleted(false);
                    return;
                }

                for (Attraction attraction : list) {
                    if (attraction != null
                            && name.equalsIgnoreCase(attraction.getName())
                            && country.equalsIgnoreCase(attraction.getCountry())) {

                        callback.onCompleted(true);
                        return;
                    }
                }

                callback.onCompleted(false);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }
}