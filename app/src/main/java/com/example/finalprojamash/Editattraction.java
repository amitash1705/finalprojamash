package com.example.finalprojamash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

    // שדות לעריכת נתוני אטרקציה
    private EditText editCity, editPrice, editDetails, editName, editAge, editAdress, editWeb;

    // בחירת סוג ומדינה
    private Spinner spType, spCountry;

    // כפתורים: גלריה, מצלמה, שמירה
    private Button btnGallery, btnPhoto, btnSave;

    // Launchers לבחירת תמונה מהגלריה או מצלמה
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    // מזהה האטרקציה לעריכה
    private String attId;

    // שירות דאטהבייס
    private DatabaseService databaseService;

    // האטרקציה הנוכחית שמועלת לעריכה
    Attraction currentAttraction;

    // דגל לבדיקה אם בוצע שינוי בטופס
    boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_editattraction);

        // התאמת padding למערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול כל ה-Views
        initViews();

        // יצירת instance של שירות הדאטהבייס
        databaseService = DatabaseService.getInstance();

        // קבלת ID של האטרקציה מהמסך הקודם
        attId = getIntent().getStringExtra("attId");

        // שליפת אטרקציה קיימת לעריכה מהדאטהבייס
        databaseService.getAttraction(attId, new DatabaseService.DatabaseCallback<Attraction>() {
            @Override
            public void onCompleted(Attraction att) {

                // שמירת האובייקט הנוכחי
                currentAttraction = att;

                // מילוי שדות בטופס
                editCity.setText(att.getCity());
                editPrice.setText(String.valueOf(att.getPrice()));
                editDetails.setText(att.getDetails());
                editName.setText(att.getName());
                editAge.setText(att.getAges());
                editAdress.setText(att.getAddress());
                editWeb.setText(att.getWeb());

                // בחירת ערך ב-Spinner מדינה לפי הנתון הקיים
                spCountry.setSelection(
                        ((ArrayAdapter) spCountry.getAdapter()).getPosition(att.getCountry())
                );

                // בחירת ערך ב-Spinner סוג לפי הנתון הקיים
                spType.setSelection(
                        ((ArrayAdapter) spType.getAdapter()).getPosition(att.getType())
                );

                // הצגת תמונה קיימת (Base64 → Bitmap)
                imageView.setImageBitmap(ImageUtil.convertFrom64base(att.getPic()));
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Editattraction.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });

        // אתחול Spinnerים
        setupSpinners();

        // אתחול בחירת תמונות
        setupImagePickers();
    }

    private void initViews() {

        // חיבור שדות טקסט
        editCity = findViewById(R.id.editCity);
        editPrice = findViewById(R.id.editPrice);
        editDetails = findViewById(R.id.editDetails);
        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editAdress = findViewById(R.id.editAdress);
        editWeb = findViewById(R.id.editWeb);

        // חיבור Spinnerים
        spType = findViewById(R.id.speditType);
        spCountry = findViewById(R.id.speditCountry);

        // חיבור ImageView
        imageView = findViewById(R.id.imageView3);

        // חיבור כפתורים
        btnGallery = findViewById(R.id.btneditGallery);
        btnPhoto = findViewById(R.id.btneditPhoto);
        btnSave = findViewById(R.id.btneditAttraction);

        // כפתור שמירה
        btnSave.setOnClickListener(v -> saveAttraction());
    }

    private void setupSpinners() {

        // Spinner מדינות
        ArrayAdapter<CharSequence> countryAdapter =
                ArrayAdapter.createFromResource(this, R.array.countryArr,
                        android.R.layout.simple_spinner_item);

        countryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCountry.setAdapter(countryAdapter);

        // Spinner סוג אטרקציה
        ArrayAdapter<CharSequence> typeAdapter =
                ArrayAdapter.createFromResource(this, R.array.typeArr,
                        android.R.layout.simple_spinner_item);

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spType.setAdapter(typeAdapter);
    }

    private void setupImagePickers() {

        // בחירת תמונה מהגלריה
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                }
        );

        // צילום תמונה מהמצלמה
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(bitmap);
                    }
                }
        );

        // כפתור פתיחת גלריה
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        });

        // כפתור פתיחת מצלמה
        btnPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureImageLauncher.launch(intent);
        });
    }

    private void saveAttraction() {

        // בדיקת שינוי בכל שדה (שימור מצב עדכון)
        String city = editCity.getText().toString().trim();
        if (!city.equals(currentAttraction.getCity()))
            isChange = true;

        String price = editPrice.getText().toString().trim();
        if (!price.equals(String.valueOf(currentAttraction.getPrice())))
            isChange = true;

        String details = editDetails.getText().toString().trim();
        if (!details.equals(currentAttraction.getDetails()))
            isChange = true;

        String name = editName.getText().toString().trim();
        if (!name.equals(currentAttraction.getName()))
            isChange = true;

        String age = editAge.getText().toString().trim();
        if (!age.equals(currentAttraction.getAges()))
            isChange = true;

        String adress = editAdress.getText().toString().trim();
        if (!adress.equals(currentAttraction.getAddress()))
            isChange = true;

        String web = editWeb.getText().toString().trim();
        if (!web.equals(currentAttraction.getWeb()))
            isChange = true;

        String type = spType.getSelectedItem().toString();
        if (!type.equals(currentAttraction.getType()))
            isChange = true;

        String country = spCountry.getSelectedItem().toString();
        if (!country.equals(currentAttraction.getCountry()))
            isChange = true;

        // אם לא היה שינוי
        if (!isChange) {
            Toast.makeText(this, "No changes were made", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקת שדות חובה
        if (city.isEmpty() || price.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "please fill the data", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקה שהאטרקציה נטענה נכון
        if (currentAttraction == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        // ביצוע עדכון בפועל
        if (isChange) {

            // שימוש ב-ID קיים לעדכון ולא יצירה מחדש
            String id = currentAttraction.getId();

            // המרת תמונה ל-Base64
            String image = ImageUtil.convertTo64Base(imageView);

            // יצירת אובייקט מעודכן
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

            // עדכון בדאטהבייס (UPDATE)
            databaseService.updateAttraction(
                    attraction,
                    new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void v) {
                            Toast.makeText(Editattraction.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(Editattraction.this, "Error updating", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}