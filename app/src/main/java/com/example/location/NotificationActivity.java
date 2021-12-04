package com.example.location;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {
private TextView addressName,locationName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addressName = findViewById(R.id.address_name);
        locationName = findViewById(R.id.locationName);
        String messageAddress = getIntent().getStringExtra("message");
        String messageLocation = getIntent().getStringExtra("message2");
        addressName.setText(messageAddress);
        locationName.setText(messageLocation);

    }
}