package com.ulima.carpool;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ulima.carpool.Utils.AES;
import com.ulima.carpool.Utils.SessionManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Spinner sexo, carrera;
    EditText nombre,edad,ciclo;
    SessionManager session;
    Button signup;
    Context c=this;
    HashMap<String,String>u;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new SessionManager(c);
        u=session.getUserDetails();

        sexo=(Spinner)findViewById(R.id.sexo);
        carrera=(Spinner)findViewById(R.id.carrera);
        nombre=(EditText)findViewById(R.id.nombre);
        edad=(EditText)findViewById(R.id.edad);
        ciclo=(EditText)findViewById(R.id.ciclo);
        signup=(Button)findViewById(R.id.btn_signup);

        List<String> gender=new ArrayList<>();
        gender.add("Masculino");
        gender.add("Femenino");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gender);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(RegisterActivity.this,sexo.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sexo.setAdapter(dataAdapter);

        List<String> career=new ArrayList<>();
        career.add("Ingenieria de Sistemas");
        career.add("Ingeniería Industrial");
        career.add("Psicología");
        career.add("Marketing");
        career.add("Comunicaciones");
        career.add("Negocios Internacionales");
        career.add("Derecho");
        career.add("Arquitectura");
        career.add("Economía");
        career.add("Contabilidad");
        career.add("Administración");
        java.util.Collections.sort(career);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, career);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carrera.setAdapter(dataAdapter2);
        carrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(RegisterActivity.this, carrera.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(RegisterActivity.this);
                String message = "Cargando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
                AES aes=new AES();
                try {
                    String nombre_enc = aes.encrypt(nombre.getText().toString());
                    String sexo_enc=aes.encrypt(sexo.getSelectedItem().toString());
                    String carrera_enc=aes.encrypt(carrera.getSelectedItem().toString());

                    signup(nombre_enc,edad.getText().toString(),carrera_enc,ciclo.getText().toString(),sexo_enc);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        });
    }

    public void signup(final String name,final String edad,final String carrera,final String ciclo,final String sexo) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/registro";
        String url2 = "http://192.168.1.6:8080/Tesis_Ojeda/registro";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.toString().equals("ok")) {
                            Toast.makeText(RegisterActivity.this, "Registro correcto", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                            RegisterActivity.this.finish();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "¡El correo ya existe!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", name);
                params.put("edad", edad);
                params.put("correo", u.get(SessionManager.KEY_EMAIL));
                params.put("sexo", sexo);
                params.put("clave", "123");
                params.put("carrera",carrera);
                params.put("ciclo",ciclo);

                return params;
            }
        };
        queue.add(postRequest);
    }
}
