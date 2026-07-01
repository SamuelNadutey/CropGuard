package com.cropguard.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;

public class UncertainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uncertain);

        String imagePath = getIntent().getStringExtra("imagePath");
        ImageView img = findViewById(R.id.resultImage);
        if (imagePath != null) {
            try {
                Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(new File(imagePath)));
                img.setImageBitmap(bmp);
            } catch (Exception ignored) {}
        }

        String[] tips = {
                "Move into open shade or face the sun — avoid shadows on the leaf.",
                "Fill the frame with just one leaf. The affected area should be clearly visible.",
                "Photograph a single leaf. Remove background clutter if possible.",
                "Rest your phone to avoid blur. Let the camera focus before the shot."
        };
        LinearLayout tipsContainer = findViewById(R.id.tipsContainer);
        for (String tip : tips) {
            tipsContainer.addView(buildTip(tip));
        }

        findViewById(R.id.btnRetake).setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private View buildTip(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dp(14);
        row.setLayoutParams(rowParams);

        TextView dot = new TextView(this);
        dot.setText("•");
        dot.setTextColor(ContextCompat.getColor(this, R.color.clay));
        dot.setTextSize(18);
        dot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(20), LinearLayout.LayoutParams.WRAP_CONTENT);
        dot.setLayoutParams(dotParams);

        TextView body = new TextView(this);
        body.setText(text);
        body.setTextColor(ContextCompat.getColor(this, R.color.ink_soft));
        body.setTextSize(15);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        bodyParams.leftMargin = dp(8);
        body.setLayoutParams(bodyParams);

        row.addView(dot);
        row.addView(body);
        return row;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}