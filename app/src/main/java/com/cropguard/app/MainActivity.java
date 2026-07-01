package com.cropguard.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cardMaize).setOnClickListener(v -> openCrop("maize"));
        findViewById(R.id.cardCassava).setOnClickListener(v -> openCrop("cassava"));
        findViewById(R.id.cardCashew).setOnClickListener(v -> openCrop("cashew"));
    }

    private void openCrop(String crop) {
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtra("crop", crop);
        startActivity(intent);
    }
}