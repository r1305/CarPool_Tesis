package com.ulima.carpool;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ulima.carpool.Utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class TripRegisterFragment extends Fragment {


    EditText destino,asientos;
    Button btn;
    SessionManager session;
    HashMap<String,String> u;
    ProgressDialog pDialog;

    public TripRegisterFragment() {
        // Required empty public constructor
    }


    public static TripRegisterFragment newInstance() {
        TripRegisterFragment fragment = new TripRegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(getActivity().getApplicationContext());
        u=session.getUserDetails();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_trip_register, container, false);

        destino=(EditText)v.findViewById(R.id.reg_destino);
        asientos=(EditText)v.findViewById(R.id.reg_trip_asiento);
        btn=(Button)v.findViewById(R.id.reg_trip_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(getActivity());
                String message = "Cargando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
                trip_register(destino.getText().toString(),asientos.getText().toString());
            }
        });
        return v;
    }

    public void trip_register(final String destino,final String asientos) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/viajes";
        String url2="http://192.168.1.15:8080/Tesis_Ojeda/viajes";
        //**** validar si el usario ya registro un carro *****
        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("true")) {
                            System.out.println(response);
                            Toast.makeText(getActivity(), "Viaje registrado", Toast.LENGTH_SHORT).show();

                            pDialog.dismiss();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            android.app.Fragment perfil=ListTripFragment.newInstance();
                            ft.replace(R.id.flaContenido,perfil);
                            ft.commit();
                        } else {
                            System.out.println(response);
                            Toast.makeText(getActivity(),"¡Ocurrió un error!", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_LONG).show();
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
                params.put("usuario",u.get(SessionManager.KEY_EMAIL));


                return params;
            }
        };
        queue.add(postRequest);
    }

}
