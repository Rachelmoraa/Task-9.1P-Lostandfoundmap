package com.example.lostfoundapp;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {

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
