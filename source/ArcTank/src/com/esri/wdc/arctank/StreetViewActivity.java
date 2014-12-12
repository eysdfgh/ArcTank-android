package com.esri.wdc.arctank;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class StreetViewActivity extends Activity {

    private static final String TAG = StreetViewActivity.class.getSimpleName();
    private static final String STREET_VIEW_KEY = null;

    public static final String EXTRA_LONGITUDE = "com.esri.wdc.arctank.Longitude";
    public static final String EXTRA_LATITUDE = "com.esri.wdc.arctank.Latitude";

    private int heading = 0;
    private double longitude = 0;
    private double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Bundle extras = getIntent().getExtras();
        longitude = extras.getDouble(EXTRA_LONGITUDE);
        latitude = extras.getDouble(EXTRA_LATITUDE);
        
        replaceImage();
    }
    
    private void replaceImage() {
        final ImageView streetView = (ImageView) findViewById(R.id.imageView_streetView);
        streetView.post(new Runnable() {
            public void run() {
                try {
                    StringBuilder urlString = new StringBuilder(
                            "https://maps.googleapis.com/maps/api/streetview?size=640x640&location=")
                            .append(latitude).append(",").append(longitude)
                            .append("&fov=120&heading=").append(heading).append("&pitch=0");
                    if (null != STREET_VIEW_KEY) {
                        urlString.append("&key=").append(STREET_VIEW_KEY);
                    }
                    URL url = new URL(urlString.toString());
                    AsyncTask<URL, Integer, Long> task = new AsyncTask<URL, Integer, Long>() {

                        @Override
                        protected Long doInBackground(URL... params) {
                            final Bitmap bitmap;
                            try {
                                bitmap = BitmapFactory.decodeStream(params[0]
                                        .openStream());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        streetView.setImageBitmap(bitmap);
                                    }
                                });
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            return null;
                        }

                    };
                    task.execute(url);
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
    }
    
    public void nextImageToLeft(View view) {
        adjustHeading(-45);
    }
    
    public void nextImageToRight(View view) {
        adjustHeading(45);
    }
    
    private void adjustHeading(double delta) {
        this.heading += delta;
        normalizeHeading();
        replaceImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.street_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void normalizeHeading() {
        while (360 <= heading) {
            heading -= 360;
        }
        while (0 > heading) {
            heading += 360;
        }
    }
    
}
