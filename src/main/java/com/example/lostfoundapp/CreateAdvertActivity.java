package com.example.lostfoundapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAdvertActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MAP = 1;
    private EditText editTextType, editTextName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        db = new DatabaseHelper(this);

        editTextType = findViewById(R.id.editTextType);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);

        Button btnPickLocation = findViewById(R.id.btnPickLocation);
        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAdvertActivity.this, MapActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAP);
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editTextType.getText().toString();
                String name = editTextName.getText().toString();
                String phone = editTextPhone.getText().toString();
                String description = editTextDescription.getText().toString();
                String date = editTextDate.getText().toString();
                String location = editTextLocation.getText().toString();

                if (type.isEmpty() || name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                    Toast.makeText(CreateAdvertActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isInserted = db.insertAdvert(type, name, phone, description, date, location);
                    if (isInserted) {
                        Toast.makeText(CreateAdvertActivity.this, "Advert Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateAdvertActivity.this, "Error Saving Advert", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra("selected_latitude", 0);
            double longitude = data.getDoubleExtra("selected_longitude", 0);
            String location = latitude + ", " + longitude;
            editTextLocation.setText(location);
        }
    }
}
