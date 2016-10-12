package com.ulima.carpool;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.ulima.carpool.Utils.AES;
import com.ulima.carpool.Utils.SessionManager;

import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    LatLng origin;
    DrawerLayout dl;
    Toolbar toolbar;
    NavigationView nav;
    TextView txt_nav;
    CircleImageView img;
    String email;
    SessionManager session;
    Location locationA;
    Location locationB;
    LatLng dir;
    float distance;
    Button search,reg;
    EditText destino, asientos;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> u = session.getUserDetails();
        email = u.get(SessionManager.KEY_EMAIL);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nav = (NavigationView) findViewById(R.id.navigation);
        dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        destino = (EditText) findViewById(R.id.reg_destino);
        asientos = (EditText) findViewById(R.id.reg_trip_asiento);
        reg=(Button)findViewById(R.id.reg_trip_btn);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(MapsActivity.this);
                String message = "Cargando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
                trip_register(destino.getText().toString(),asientos.getText().toString(),String.valueOf(distance));
            }
        });


        search = (Button) findViewById(R.id.btn_buscar);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationFromAddress(getApplicationContext(), destino.getText().toString());
                LatLng lat_long = origin;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lat_long, 15);
                mMap.animateCamera(cameraUpdate);
                mMap.addMarker(new MarkerOptions().position(lat_long)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("Destino"));
                //Toast.makeText(MapsActivity.this, distance + " KM", Toast.LENGTH_SHORT).show();
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        locationB=new Location("Destino");
                        locationB.setLatitude(marker.getPosition().latitude);
                        locationB.setLongitude(marker.getPosition().longitude);
                        System.out.println("locationB: "+locationB);
                        System.out.println(locationB.getLatitude()+"-"+locationB.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Destino")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .draggable(true));
                        distance = locationA.distanceTo(locationB) / 1000;
                        System.out.println("distancia: "+locationA.distanceTo(locationB));
                        Toast.makeText(MapsActivity.this, (distance) + " KM", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Toast.makeText(MapsActivity.this, "Ubicando...", Toast.LENGTH_LONG).show();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Inicio"));

                locationA = new Location("Inicio");
                locationA.setLatitude(latLng.latitude);
                locationA.setLongitude(latLng.longitude);
                System.out.println("locationB: "+locationA);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Toast.makeText(MapsActivity.this, "Active su GPS porfavor", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Inicio")
                        .draggable(true));
                locationA=new Location("Inicio");
                locationA.setLatitude(latLng.latitude);
                locationA.setLongitude(latLng.longitude);
                System.out.println("locationA: "+locationA);
                System.out.println(locationA.getLatitude()+"-"+locationA.getLongitude());
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Destino")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(true));
                locationB=new Location("Destino");
                locationB.setLatitude(latLng.latitude);
                locationB.setLongitude(latLng.longitude);
                System.out.println("locationB: "+locationB);
                System.out.println(locationB.getLatitude()+"-"+locationB.getLongitude());
                System.out.println("distancia: "+locationA.distanceTo(locationB));
                distance = locationA.distanceTo(locationB) / 1000;
                Toast.makeText(MapsActivity.this, (distance) + " KM", Toast.LENGTH_SHORT).show();

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        locationB=new Location("Destino");
                        locationB.setLatitude(marker.getPosition().latitude);
                        locationB.setLongitude(marker.getPosition().longitude);
                        System.out.println("locationB: "+locationB);
                        System.out.println(locationB.getLatitude()+"-"+locationB.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Destino")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .draggable(true)
                                .snippet("Destino"));
                        distance = locationA.distanceTo(locationB) / 1000;
                        System.out.println("distancia: "+locationA.distanceTo(locationB));
                        Toast.makeText(MapsActivity.this, (distance) + " KM", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        UiSettings u = mMap.getUiSettings();
        u.setZoomControlsEnabled(true);
        u.setMapToolbarEnabled(false);
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {


        Geocoder coder = new Geocoder(context);

        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            locationB=new Location("Destino");
            locationB.setLatitude(location.getLatitude());
            locationB.setLongitude(location.getLongitude());
            System.out.println(locationB.getLatitude()+"-"+locationB.getLongitude());
            distance=locationA.distanceTo(locationB)/1000;
            System.out.println("distancia: "+locationA.distanceTo(locationB));
            Toast.makeText(context, distance+" KM", Toast.LENGTH_SHORT).show();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            origin = p1;


        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public void trip_register(final String destino,final String asientos,final String d) {

        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/viajes";
        String url2="http://192.168.1.13:8080/Tesis_Ojeda/viajes";
        //**** validar si el usario ya registro un carro *****
        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("true")) {
                            System.out.println(response);
                            Toast.makeText(MapsActivity.this, "Viaje registrado", Toast.LENGTH_SHORT).show();

                            pDialog.dismiss();

                        } else {
                            System.out.println(response);
                            Toast.makeText(MapsActivity.this,"¡Ocurrió un error!", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(MapsActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        System.out.println("error response: "+error);

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("destino", destino);
                params.put("asientos", asientos);
                params.put("usuario",email);
                params.put("km",d);


                return params;
            }
        };
        queue.add(postRequest);
    }


}
