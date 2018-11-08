package com.oscar.summit.rotclean;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    private GoogleMap mMap;
    private MarkerOptions marker;

    LatLng home;


    JSONArray ja = null;

    List<LatLng> points=new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        if (savedInstanceState == null) { //PARA QUE LA APLICACION AL MOMENTO DE ROTAR, SOLO SEA LLAMADA 1 VEZ,
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

            }
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////////////
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        mMap.setMinZoomPreference(10); //ESTABLECER EL ZOOM MINIMO EN EL MAPA
        mMap.setMaxZoomPreference(18); //ESTABLECER EL ZOOM MAXIMO EN EL MAPA

        /*CameraPosition camera = new CameraPosition.Builder()
                .target(mMap.)
                .zoom(15) //LIMITE DE ZOOM ES 21
                .bearing(90) //Orientacion de la camara hacia el este
                .tilt(30) // Efecto 3d en 90°
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));*/


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        /*marker = new MarkerOptions();
        marker.position(home);
        //marker.title("Gracias por usar RotClean");
        marker.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on));

        mMap.addMarker(marker);*/

        /////////////////////////////////////////////////////////////////////////////////////////////

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(final Location location) {
                // Called when a new location is found by the network location provider.
                home = new LatLng(location.getLatitude(),location.getLongitude());
                new ConsultarDatos().execute("https://11coolest.es/consulta.php");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //A ESTE METODO SE LE AGREGA LA POSICION CON LA CUAL INICIARA AL MOMENTO DE CARGAR EL MAPA



    }

    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            List<Double> pintla=new ArrayList<Double>();
            List<Double> pintlo=new ArrayList<Double>();

            try {
                ja = new JSONArray(result);

                for(int i=0; i<ja.length(); i++){
                    pintla.add(ja.getJSONObject(i).getDouble("latitud"));
                    pintlo.add(ja.getJSONObject(i).getDouble("longitud"));
                    points.add(new LatLng(pintla.get(i),pintlo.get(i)));
                }


                for (int i = 0 ; i < ja.length(); i++){

                    mMap.addMarker(new MarkerOptions().position(points.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.trash)));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl=myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
