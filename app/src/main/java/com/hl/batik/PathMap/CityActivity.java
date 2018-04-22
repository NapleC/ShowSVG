package com.hl.batik.PathMap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hl.batik.R;

public class CityActivity extends AppCompatActivity {


    private CityMapView detialMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        detialMapView= (CityMapView) findViewById(R.id.svg_map);
        detialMapView.setOnMapClickListener(new CityMapView.OnMapClickListener() {
            @Override
            public void onClick(CityItem cityItem) {
                Toast.makeText(CityActivity.this, cityItem.getCityName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
