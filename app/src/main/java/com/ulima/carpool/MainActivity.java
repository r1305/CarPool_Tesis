package com.ulima.carpool;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ulima.carpool.Utils.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //@Bind(R.id.drawer_layout)
    DrawerLayout dl;
    //@Bind(R.id.toolbar)
    Toolbar toolbar;
    //@Bind(R.id.navigation)
    NavigationView nav;
    //@Bind(R.id.txt_nav_header)
    TextView txt_nav;
    //@Bind(R.id.profile)
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        nav=(NavigationView)findViewById(R.id.navigation);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);
        View v=nav.getHeaderView(0);
        txt_nav=(TextView)v.findViewById(R.id.txt_nav_header);

        session=new SessionManager(getApplicationContext());
        session.checkLogin();
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
                Fragment viaje=TripRegisterFragment.newInstance();
                ft.replace(R.id.flaContenido,viaje);
                toolbar.setTitle("Nuevo viaje");
                ft.commit();
                dl.closeDrawers();
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.perfil:
                //Fragment perfil=ProfileFragment.newInstance(datos);
                //ft.replace(R.id.flaContenido,perfil);
                toolbar.setTitle("Perfil");
                ft.commit();
                dl.closeDrawers();
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.viajes:
                item.setChecked(true);
                Fragment libros=ListTripFragment.newInstance();
                ft.replace(R.id.flaContenido,libros);
                toolbar.setTitle("Viajes");
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


}
