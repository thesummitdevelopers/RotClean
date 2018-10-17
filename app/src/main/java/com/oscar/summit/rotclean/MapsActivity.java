package com.oscar.summit.rotclean;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private MarkerOptions marker;

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

        LatLng sydney = new LatLng(-16.4291856,-71.5219084);

        mMap.setMinZoomPreference(10); //ESTABLECER EL ZOOM MINIMO EN EL MAPA
        mMap.setMaxZoomPreference(18); //ESTABLECER EL ZOOM MAXIMO EN EL MAPA

        // Add a marker in Sydney and move the camera



        marker = new MarkerOptions();
        marker.position(sydney);
        marker.title("Gracias por usar RotClean");
        marker.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on));

        mMap.addMarker(marker);
        CameraPosition camera = new CameraPosition.Builder()
                .target(sydney)
                .zoom(15) //LIMITE DE ZOOM ES 21
                .bearing(90) //Orientacion de la camara hacia el este
                .tilt(30) // Efecto 3d en 90°
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

}
