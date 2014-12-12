package com.esri.wdc.arctank;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.toolkit.map.MapViewHelper;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.tasks.geocode.Locator;

public class ArcTankActivity extends Activity {

    MapView mMapView;
    MapViewHelper mvHelper;
    Locator locator;

    Drawable icon;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Retrieve the map and map options from XML layout
        mMapView = (MapView) findViewById(R.id.map);
        // enable wrap around
        mMapView.enableWrapAround(true);
        // set logo visible
        mMapView.setEsriLogoVisible(true);
        // Create a MapView Helper
        mvHelper = new MapViewHelper(mMapView);
        // Create drawable icon
        icon = getResources().getDrawable(R.drawable.route_destination);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                // Add a graphic to represent ESRI Headquarters
                int loaded = mvHelper.addMarkerGraphic(
                        34.056695,
                        -117.195693,
                        "ESRI",
                        "World Headquarters",
                        null,
                        icon,
                        false,
                        0);
                if (loaded < 0) {
                    Log.d("TAG", "Marker Graphic not added to MapView");
                }
            }
        });

        // initialize online locator
        locator = Locator.createOnlineLocator();

        final String[] fields = { "Address", "City", "Region", "Postal" };

        // single tap on map to get the address of the location tapped
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSingleTap(float x, float y) {
                Point pt = mMapView.toMapPoint(x, y);
                Point pt4326 = (Point) GeometryEngine.project(
                        pt,
                        mMapView.getSpatialReference(),
                        SpatialReference.create(4326));

                Intent intent = new Intent(ArcTankActivity.this, StreetViewActivity.class);
                intent.putExtra(StreetViewActivity.EXTRA_LONGITUDE, pt4326.getX());
                intent.putExtra(StreetViewActivity.EXTRA_LATITUDE, pt4326.getY());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.unpause();
    }

}