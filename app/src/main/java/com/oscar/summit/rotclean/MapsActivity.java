package com.oscar.summit.rotclean;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    private GoogleMap mMap;

    private MarkerOptions marker;

    LatLng home = new LatLng(-16.416232,-71.501152);

    List<Double> pintla=new ArrayList<Double>();
    List<Double> pintlo=new ArrayList<Double>();
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
        //A ESTE METODO SE LE AGREGA LA POSICION CON LA CUAL INICIARA AL MOMENTO DE CARGAR EL MAPA
        mMap = googleMap;

        mMap.setMinZoomPreference(10); //ESTABLECER EL ZOOM MINIMO EN EL MAPA
        mMap.setMaxZoomPreference(18); //ESTABLECER EL ZOOM MAXIMO EN EL MAPA


        CameraPosition camera = new CameraPosition.Builder()
                .target(home)
                .zoom(15) //LIMITE DE ZOOM ES 21
                .bearing(90) //Orientacion de la camara hacia el este
                .tilt(30) // Efecto 3d en 90°
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        new ConsultarDatos().execute("https://11coolest.es/consulta.php");
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        marker = new MarkerOptions();
        marker.position(home);
        marker.title("Gracias por usar RotClean");
        marker.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on));

        mMap.addMarker(marker);


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


            try {
                ja = new JSONArray(result);

                for(int i=0; i<ja.length(); i++){
                    pintla.add(ja.getJSONObject(i).getDouble("latitud"));
                    pintlo.add(ja.getJSONObject(i).getDouble("longitud"));
                    points.add(new LatLng(pintla.get(i),pintlo.get(i)));
                }


                for (int i = 0 ; i < ja.length(); i++){

                    mMap.addMarker(new MarkerOptions().position(points.get(i)));

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
