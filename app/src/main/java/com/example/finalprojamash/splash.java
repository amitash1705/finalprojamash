package com.example.finalprojamash;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash extends AppCompatActivity {

    // ImageView שמציג את הלוגו במסך פתיחה
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מצב מסך מלא (EdgeToEdge UI)
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_splash);

        // חיבור ImageView מה-XML
        myImageView = (ImageView) findViewById(R.id.imageView);

        // יצירת Thread למסך פתיחה (Splash Screen)
        Thread msplashTread = new Thread() {
            @Override
            public void run() {
                try {

                    synchronized (this) {

                        // טעינת אנימציה (Fade In)
                        Animation myFadeInAnimation =
                                AnimationUtils.loadAnimation(splash.this, R.anim.tween);

                        // הפעלת אנימציה על הלוגו
                        myImageView.startAnimation(myFadeInAnimation);

                        // השהייה של 2 שניות
                        wait(2000);
                    }

                } catch (InterruptedException ex) {
                    // טיפול בשגיאת השהייה
                }

                // סגירת מסך הספלש
                finish();

                // מעבר למסך הראשי
                Intent intent = new Intent(splash.this, MainActivity.class);
                startActivity(intent);
            }
        };

        // התחלת ה-Thread
        msplashTread.start();
    }
}