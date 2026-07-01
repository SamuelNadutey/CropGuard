package com.cropguard.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {

    private Bitmap analyzedBitmap;
    private String currentLabel;
    private Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String label = getIntent().getStringExtra("label");
        float confidence = getIntent().getFloatExtra("confidence", 0f);
        String imagePath = getIntent().getStringExtra("imagePath");

        TreatmentData.Info info = TreatmentData.get(label);
        int pct = Math.round(confidence * 100);

        // Photo
        ImageView resultImage = findViewById(R.id.resultImage);
        if (imagePath != null) {
            try {
                analyzedBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(imagePath)));
                resultImage.setImageBitmap(analyzedBitmap);
            } catch (Exception ignored) {}
        }
        currentLabel = label;
        try {
            classifier = new Classifier(this);
        } catch (Exception ignored) {}

        findViewById(R.id.btnExplain).setOnClickListener(v -> runExplanation());

        // Confidence + name
        ((TextView) findViewById(R.id.confidenceCircle)).setText(pct + "%");
        ((TextView) findViewById(R.id.diagnosisName)).setText(info.displayName);

        // Crop tag
        String cropTag = label.contains("_") ? label.substring(0, label.indexOf('_')) : label;
        ((TextView) findViewById(R.id.cropTag)).setText(cropTag.toUpperCase());

        // Severity band
        TextView band = findViewById(R.id.severityBand);
        switch (info.severity) {
            case "healthy":
                band.setText("Healthy — no action needed");
                band.setBackgroundColor(ContextCompat.getColor(this, R.color.status_healthy));
                break;
            case "alert":
                band.setText("Act Now — treatment recommended");
                band.setBackgroundColor(ContextCompat.getColor(this, R.color.status_alert));
                break;
            default:
                band.setText("Act Soon — monitor and manage");
                band.setBackgroundColor(ContextCompat.getColor(this, R.color.status_warn));
                break;
        }

        // What it is
        ((TextView) findViewById(R.id.whatItIs)).setText(info.whatItIs);

        // What to do — numbered steps
        LinearLayout steps = findViewById(R.id.stepsContainer);
        for (int i = 0; i < info.whatToDo.length; i++) {
            steps.addView(buildStep(i + 1, info.whatToDo[i]));
        }

        // Treatment + authority
        ((TextView) findViewById(R.id.treatmentText)).setText(info.treatment);
        if (info.authority != null && !info.authority.isEmpty()) {
            ((TextView) findViewById(R.id.authorityText))
                    .setText("Source: " + info.authority + " · Always confirm with a local extension officer.");
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void runExplanation() {
        if (analyzedBitmap == null || classifier == null) return;

        TextView status = findViewById(R.id.explainStatus);
        status.setText("Generating explanation…");

        int classIndex = classifier.getLabels().indexOf(currentLabel);
        if (classIndex < 0) { status.setText(""); return; }

        Handler main = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            int grid = 8;
            float[][] importance = classifier.occlusionHeatmap(analyzedBitmap, classIndex, grid);
            Bitmap overlay = buildHeatmapBitmap(importance, grid);
            main.post(() -> {
                ImageView ov = findViewById(R.id.heatmapOverlay);
                ov.setImageBitmap(overlay);
                ov.setVisibility(View.VISIBLE);
                status.setText("Highlighted areas most influenced the diagnosis.");
            });
        });
    }

    private Bitmap buildHeatmapBitmap(float[][] importance, int grid) {
        float max = 0f;
        for (float[] row : importance)
            for (float v : row) max = Math.max(max, v);
        if (max <= 0f) max = 1f;

        int size = 224;
        int cell = size / grid;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        for (int gy = 0; gy < grid; gy++) {
            for (int gx = 0; gx < grid; gx++) {
                float norm = importance[gy][gx] / max;
                int color = heatColor(norm);
                for (int y = gy * cell; y < (gy + 1) * cell && y < size; y++) {
                    for (int x = gx * cell; x < (gx + 1) * cell && x < size; x++) {
                        bmp.setPixel(x, y, color);
                    }
                }
            }
        }
        return bmp;
    }

    private int heatColor(float t) {
        if (t < 0.15f) return Color.TRANSPARENT;
        int r = 255;
        int g = (int) (255 * (1 - t));
        return Color.argb(200, r, g, 40);
    }

    private View buildStep(int number, String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dp(12);
        row.setLayoutParams(rowParams);

        TextView num = new TextView(this);
        num.setText(String.valueOf(number));
        num.setGravity(Gravity.CENTER);
        num.setTextColor(ContextCompat.getColor(this, R.color.white));
        num.setTextSize(13);
        LinearLayout.LayoutParams numParams = new LinearLayout.LayoutParams(dp(26), dp(26));
        num.setLayoutParams(numParams);
        num.setBackgroundResource(R.drawable.step_circle);

        TextView body = new TextView(this);
        body.setText(text);
        body.setTextColor(ContextCompat.getColor(this, R.color.ink_soft));
        body.setTextSize(15);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        bodyParams.leftMargin = dp(14);
        body.setLayoutParams(bodyParams);

        row.addView(num);
        row.addView(body);
        return row;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}