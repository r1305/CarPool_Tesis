package com.ulima.carpool;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ulima.carpool.Utils.Alumno;
import com.ulima.carpool.Utils.Modelo;
import com.ulima.carpool.Utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class DetailsFragment extends Fragment {

    Modelo m=new Modelo();
    String user1;
    TextView rpta,nombre,edad,carrera;
    SessionManager session;
    HashMap<String,String> u;
    Alumno a,b;

    // TODO: Rename and change types of parameters
    private String mParam1;

    public DetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("alumno1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("alumno1");
            Toast.makeText(getActivity(), mParam1, Toast.LENGTH_SHORT).show();
        }
        session=new SessionManager(getActivity());
        u=session.getUserDetails();
        user1=u.get(SessionManager.KEY_EMAIL);
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_details, container, false);
            rpta=(TextView)v.findViewById(R.id.txt_afinidad);
        nombre=(TextView)v.findViewById(R.id.txt_detail_nombre);
        edad=(TextView)v.findViewById(R.id.txt_detail_edad);
        carrera=(TextView)v.findViewById(R.id.txt_detail_carrera);
        return v;
    }

    public void getData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/getDatos";
        String url2="http://192.168.1.6:8080/Tesis_Ojeda/getDatos";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        a=new Alumno();
                        a=m.setDatosA(response);
                        //Toast.makeText(getActivity(),"alumnoA: "+ response, Toast.LENGTH_SHORT).show();
                        getData2();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(LoginActivity.this, "Error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("correo", user1);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void getData2() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/getDatos";
        String url2="http://192.168.1.6:8080/Tesis_Ojeda/getDatos";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        b=new Alumno();
                        b=m.setDatosB(response);
                        //Toast.makeText(getActivity(), "alumnoB: "+response, Toast.LENGTH_SHORT).show();
                        nombre.setText(b.getNombres());
                        edad.setText((String.valueOf(b.getEdad())));
                        carrera.setText(b.getCarrera());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                double af=100*m.calcularAfinidadTotal(a,b);
                                rpta.setText(String.valueOf(Math.round(af))+"%");
                            }
                        },3000);

                        double af=100*m.calcularAfinidadTotal(a,b);
                        rpta.setText(String.valueOf(Math.round(af))+"%");
                        //pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(LoginActivity.this, "Error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("correo", mParam1);
                return params;
            }
        };
        queue.add(postRequest);
    }


}
