package com.ulima.carpool;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ulima.carpool.Utils.SessionManager;


public class FacebookActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_facebook);

        session=new SessionManager(this);
        session.checkLogin();


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fb = FacebookFragment.newInstance();
        ft.replace(R.id.flaContenido, fb);
        ft.commit();
    }

}
