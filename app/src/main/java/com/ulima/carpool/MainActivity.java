package com.ulima.carpool;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.ulima.carpool.Utils.AES;
import com.ulima.carpool.Utils.SessionManager;

import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout dl;
    Toolbar toolbar;
    NavigationView nav;
    TextView txt_nav;
    SessionManager session;
    CircleImageView img;
    String email;
    String datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String,String>u=session.getUserDetails();
        email=u.get(SessionManager.KEY_EMAIL);
        getDatos(email);
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        nav=(NavigationView)findViewById(R.id.navigation);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);
        View v=nav.getHeaderView(0);
        txt_nav=(TextView)v.findViewById(R.id.txt_nav_header);
        img=(CircleImageView)v.findViewById(R.id.profile);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.animate();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl.openDrawer(GravityCompat.START);
                if (dl.isDrawerOpen(GravityCompat.START)) {
                    dl.closeDrawers();
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,dl,toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        dl.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        nav.setNavigationItemSelectedListener(this);
        nav.animate();



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch(item.getItemId()){
            case R.id.logout:
                session.logoutUser();
                //LoginManager.getInstance().logOut();
                return true;
            case R.id.registar_viaje:
                Intent i=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
                return true;
            case R.id.perfil:
                Fragment perfil=PerfilFragment.newInstance(datos);
                ft.replace(R.id.flaContenido,perfil);
                toolbar.setTitle("Perfil");
                ft.commit();
                dl.closeDrawers();
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.viajes:
                Fragment lista=ListTripFragment.newInstance();
                ft.replace(R.id.flaContenido,lista);
                toolbar.setTitle("Viajes");
                ft.commit();
                dl.closeDrawers();
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.notificaciones:
                Fragment sol=ListSolFragment.newInstance();
                ft.replace(R.id.flaContenido,sol);
                toolbar.setTitle("Notificaciones");
                ft.commit();
                dl.closeDrawers();
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.carro:
                Fragment newCar=CarRegisterFragment.newInstance();
                ft.replace(R.id.flaContenido,newCar);
                toolbar.setTitle("Registrar Auto");
                ft.commit();
                dl.closeDrawers();
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    public void getDatos(final String u) {
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/getDatos";
        String url2="http://192.168.1.6:8080/Tesis_Ojeda/getDatos";


        // Request a string response from the provided URL.
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("***** "+response+" ****");
                        JSONParser p=new JSONParser();
                        try{
                            datos=response;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment perfil=PerfilFragment.newInstance(datos);
                            ft.replace(R.id.flaContenido,perfil);
                            toolbar.setTitle("Perfil");
                            ft.commit();
                            AES a=new AES();
                            org.json.simple.JSONObject o=(org.json.simple.JSONObject)p.parse(response);
                            txt_nav.setText(a.decrypt(o.get("nombre").toString()));

                            if(o.get("foto").toString()!=""&&o.get("foto").toString()!=null){
                                Picasso.with(MainActivity.this).load(o.get("foto").toString()).into(img);
                            }



                        }catch (Exception e){
                            System.out.println("error: "+e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", u);

                return params;
            }
        };
        queue.add(postRequest);
    }


}
