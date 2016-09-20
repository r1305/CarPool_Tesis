package com.ulima.carpool;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
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


public class CarRegisterFragment extends Fragment {

    EditText placa, marca,modelo,asientos;
    Button registrar;
    SessionManager session;
    HashMap<String,String> u;
    ProgressDialog pDialog;

    public CarRegisterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CarRegisterFragment newInstance() {
        CarRegisterFragment fragment = new CarRegisterFragment();

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
        View v=inflater.inflate(R.layout.fragment_car_register, container, false);
        placa=(EditText)v.findViewById(R.id.reg_placa);
        marca=(EditText)v.findViewById(R.id.reg_marca);
        modelo=(EditText)v.findViewById(R.id.reg_modelo);
        asientos=(EditText)v.findViewById(R.id.reg_asientos);
        registrar=(Button)v.findViewById(R.id.reg_btn);

        registrar.setOnClickListener(new View.OnClickListener() {
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

                car_register(placa.getText().toString(),marca.getText().toString(),modelo.getText().toString(),asientos.getText().toString());

            }
        });
        return v;
    }

    public void car_register(final String placa,final String marca,final String modelo,final String asientos) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/carro";
        String url2="http://192.168.1.15:8080/Tesis_Ojeda/carro";
        //**** validar si el usario ya registro un carro *****
        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("true")) {
                            System.out.println(response);
                            Toast.makeText(getActivity(), "Registro correcto", Toast.LENGTH_SHORT).show();

                            pDialog.dismiss();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment perfil=ListTripFragment.newInstance();
                            ft.replace(R.id.flaContenido,perfil);
                            ft.commit();
                        } else {
                            System.out.println(response);
                            Toast.makeText(getActivity(),"¡Ya está registrado!", Toast.LENGTH_SHORT).show();
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
                params.put("placa", placa);
                params.put("marca", marca);
                params.put("modelo", modelo);
                params.put("asientos", asientos);
                params.put("usuario",u.get(SessionManager.KEY_EMAIL));


                return params;
            }
        };
        queue.add(postRequest);
    }



}
