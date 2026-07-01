package com.cropguard.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CaptureActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Bitmap selectedBitmap;
    private String crop;
    private Uri cameraImageUri;

    private Classifier classifier;
    private TextView btnAnalyze;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        crop = getIntent().getStringExtra("crop");
        if (crop == null) crop = "crop";

        ((TextView) findViewById(R.id.cropLabel)).setText(capitalize(crop));
        imagePreview = findViewById(R.id.imagePreview);

        btnAnalyze = findViewById(R.id.btnAnalyze);
        try {
            classifier = new Classifier(this);
        } catch (Exception e) {
            Toast.makeText(this, "Model load failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        btnAnalyze.setOnClickListener(v -> runDiagnosis());

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        loadImageFromUri(result.getData().getData());
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && cameraImageUri != null) {
                        loadImageFromUri(cameraImageUri);
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        launchCamera();
                    } else {
                        Toast.makeText(this,
                                "Camera blocked. Enable it in Settings, or use Gallery.",
                                Toast.LENGTH_LONG).show();
                        Intent settings = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(settings);
                    }
                });

        findViewById(R.id.btnGallery).setOnClickListener(v -> openGallery());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCamera).setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File imageFile = new File(getExternalFilesDir("Pictures"), "capture.jpg");
            cameraImageUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void loadImageFromUri(Uri uri) {
        try {
            // First pass: read only the image's dimensions, not the pixels
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            InputStream boundsStream = getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(boundsStream, null, bounds);
            if (boundsStream != null) boundsStream.close();

            // Work out how much to shrink so the image is manageable (~1024px)
            int sample = 1;
            int largest = Math.max(bounds.outWidth, bounds.outHeight);
            while (largest / sample > 1024) {
                sample *= 2;
            }

            // Second pass: actually decode, downsampled
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            InputStream is = getContentResolver().openInputStream(uri);
            selectedBitmap = BitmapFactory.decodeStream(is, null, opts);
            if (is != null) is.close();

            if (selectedBitmap != null) {
                imagePreview.setImageBitmap(selectedBitmap);
                btnAnalyze.setAlpha(1.0f);
            } else {
                Toast.makeText(this, "Could not load image, try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void runDiagnosis() {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Take or choose a photo first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (classifier == null) {
            Toast.makeText(this, "Model not loaded", Toast.LENGTH_SHORT).show();
            return;
        }
        Classifier.Result result = classifier.classify(selectedBitmap);

        String imagePath = null;
        try {
            File f = new File(getCacheDir(), "analyzed.jpg");
            FileOutputStream fos = new FileOutputStream(f);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            imagePath = f.getAbsolutePath();
        } catch (Exception ignored) {}

        Intent intent;
        if (result.confidence < 0.60f) {
            intent = new Intent(this, UncertainActivity.class);
            intent.putExtra("imagePath", imagePath);
        } else {
            intent = new Intent(this, ResultActivity.class);
            intent.putExtra("label", result.label);
            intent.putExtra("confidence", result.confidence);
            intent.putExtra("imagePath", imagePath);
        }
        startActivity(intent);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}