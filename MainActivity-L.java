package com.example.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        Button btnViewItems = findViewById(R.id.btnViewItems);

        btnCreateAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
                startActivity(intent);
            }
        });

        btnViewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
                startActivity(intent);
            }
        });
    }
}
class CreateAdvertActivity extends AppCompatActivity {

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
class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LostAndFound.db";
    private static final String TABLE_NAME = "adverts";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TYPE";
    private static final String COL_3 = "NAME";
    private static final String COL_4 = "PHONE";
    private static final String COL_5 = "DESCRIPTION";
    private static final String COL_6 = "DATE";
    private static final String COL_7 = "LOCATION";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TYPE TEXT, NAME TEXT, PHONE TEXT, DESCRIPTION TEXT, DATE TEXT, LOCATION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAdvert(String type, String name, String phone, String description, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, type);
        contentValues.put(COL_3, name);
        contentValues.put(COL_4, phone);
        contentValues.put(COL_5, description);
        contentValues.put(COL_6, date);
        contentValues.put(COL_7, location);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public ArrayList<String> getAllAdverts() {
        ArrayList<String> advertsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while(res.moveToNext()) {
            StringBuilder advert = new StringBuilder();
            advert.append("Type: ").append(res.getString(1)).append("\n");
            advert.append("Name: ").append(res.getString(2)).append("\n");
            advert.append("Phone: ").append(res.getString(3)).append("\n");
            advert.append("Description: ").append(res.getString(4)).append("\n");
            advert.append("Date: ").append(res.getString(5)).append("\n");
            advert.append("Location: ").append(res.getString(6)).append("\n");
            advertsList.add(advert.toString());
        }
        res.close();
        return advertsList;
    }

    public boolean deleteAdvert(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id}) > 0;
    }
}
 class MapActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mapView = findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(51.5, -0.1)); // Set to a default location

        mapView.setOnLongClickListener(v -> {
            mapView.getOverlays().clear();
            GeoPoint point = (GeoPoint) mapView.getProjection().fromPixels((int) v.getX(), (int) v.getY());
            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            mapView.getOverlays().add(marker);
            mapView.invalidate();

            double latitude = point.getLatitude();
            double longitude = point.getLongitude();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_latitude", latitude);
            resultIntent.putExtra("selected_longitude", longitude);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}
class ViewItemActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listViewItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        db = new DatabaseHelper(this);
        listViewItems = findViewById(R.id.listViewItems);

        final ArrayList<String> itemList = db.getAllAdverts();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listViewItems.setAdapter(adapter);

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = itemList.get(position);
                new AlertDialog.Builder(ViewItemActivity.this)
                        .setTitle("Remove Item")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove item from database
                                db.deleteAdvert(selectedItem);

                                // Remove item from the list and update the adapter
                                itemList.remove(selectedItem);
                                adapter.notifyDataSetChanged();

                                Toast.makeText(ViewItemActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}

