package com.ulima.carpool;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class DetailsFragment extends Fragment {

    Modelo m = new Modelo();
    String user1,pesos;
    TextView rpta, nombre, edad, carrera;
    SessionManager session;
    HashMap<String, String> u;
    float univ, fb, car, ciclo, age, sexo, carac;
    Alumno a, b;
    Button reg;
    ProgressDialog pDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String viaje;

    public DetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("alumno1", param1);
        args.putString("idViaje", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("alumno1");
            viaje = getArguments().getString("idViaje");
            //Toast.makeText(getActivity(), mParam1, Toast.LENGTH_SHORT).show();
        }
        session = new SessionManager(getActivity());
        u = session.getUserDetails();
        user1 = u.get(SessionManager.KEY_EMAIL);
        pesos = u.get(SessionManager.KEY_PESOS);
        try {
            JSONParser p = new JSONParser();
            JSONObject o = (JSONObject) p.parse(pesos);
            m.setUniv(Integer.parseInt(String.valueOf((long)o.get("uni"))));
            m.setCarac(Integer.parseInt(String.valueOf((long) o.get("carac"))));
            m.setCiclo(Integer.parseInt(String.valueOf((long)o.get("ciclo"))));
            m.setSexo(Integer.parseInt(String.valueOf((long)o.get("sexo"))));
            m.setFb(Integer.parseInt(String.valueOf((long)o.get("fb"))));
            m.setEdad(Integer.parseInt(String.valueOf((long)o.get("edad"))));
            m.setCarrera(Integer.parseInt(String.valueOf((long)o.get("carrera"))));
            System.out.println("detail");
            System.out.println((long)o.get("uni"));
            System.out.println((long)o.get("carrera"));
        } catch (Exception e) {
            m.setUniv(2);
            m.setCarac(8);
            m.setCiclo(5);
            m.setSexo(2);
            m.setFb(6);
            m.setEdad(2);
            m.setCarrera(5);
            System.out.println("error detail "+e);
            System.out.println(pesos);
        }


        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        pDialog = new ProgressDialog(getActivity());
        rpta = (TextView) v.findViewById(R.id.txt_afinidad);
        nombre = (TextView) v.findViewById(R.id.txt_detail_nombre);
        edad = (TextView) v.findViewById(R.id.txt_detail_edad);
        carrera = (TextView) v.findViewById(R.id.txt_detail_carrera);
        reg = (Button) v.findViewById(R.id.sol_btn);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Registrando solicitud...";
                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
                registrar();
            }
        });
        return v;
    }

    public void getData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/getDatos";
        String url2 = "http://192.168.1.6:8080/Tesis_Ojeda/getDatos";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        a = new Alumno();
                        a = m.setDatosA(response);
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
        String url2 = "http://192.168.1.6:8080/Tesis_Ojeda/getDatos";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        b = new Alumno();
                        b = m.setDatosB(response);
                        //Toast.makeText(getActivity(), "alumnoB: "+response, Toast.LENGTH_SHORT).show();
                        nombre.setText(b.getNombres());
                        edad.setText((String.valueOf(b.getEdad())));
                        carrera.setText(b.getCarrera());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                double af = 100 * m.calcularAfinidadTotal(a, b);
                                rpta.setText(String.valueOf(Math.round(af)) + "%");
                            }
                        }, 3000);

                        double af = 100 * m.calcularAfinidadTotal(a, b);
                        rpta.setText(String.valueOf(Math.round(af)) + "%");
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

    public void registrar() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/notificacion";
        String url2 = "http://192.168.1.13:8080/Tesis_Ojeda/notificacion";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        pDialog.dismiss();
                        if (response.equals("ok")) {
                            Toast.makeText(getActivity(), "Solicitud enviada", Toast.LENGTH_SHORT).show();
                            reg.setEnabled(false);
                        } else {
                            Toast.makeText(getActivity(), "Error al registrar", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user1);
                params.put("user2", mParam1);
                params.put("idCarrera", viaje);
                return params;
            }
        };
        queue.add(postRequest);

    }


}
