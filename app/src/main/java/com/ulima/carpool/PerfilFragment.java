package com.ulima.carpool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.ulima.carpool.Utils.AES;
import com.ulima.carpool.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {



    CallbackManager callbackManager;
    String email;
    SessionManager session;
    ProgressDialog pDialog;
    TextView nombre,carrera,edad;
    CircleImageView img;
    String data;

    public PerfilFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String datos) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args=new Bundle();
        args.putString("datos",datos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getBaseContext());
        callbackManager = CallbackManager.Factory.create();
        session=new SessionManager(getActivity());
        HashMap<String,String>u=session.getUserDetails();
        email=u.get(SessionManager.KEY_EMAIL);
        //getDatos(email);
        if(getArguments()!=null){
            data=getArguments().getString("datos");
            System.out.println(data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_perfil, container, false);

        final LoginButton loginButton = (LoginButton) v.findViewById(R.id.login_button);
        nombre=(TextView) v.findViewById(R.id.nombre);
        carrera=(TextView)v.findViewById(R.id.carrera);
        edad=(TextView)v.findViewById(R.id.edad_perfil);
        img=(CircleImageView)v.findViewById(R.id.foto_perfil);

        try {
            JSONParser p = new JSONParser();
            org.json.simple.JSONObject o = (org.json.simple.JSONObject) p.parse(data);
            AES a=new AES();
            System.out.println(o.get("nombre"));
            System.out.println(o.get("nombre").toString());
            nombre.setText(a.decrypt(o.get("nombre").toString()));
            carrera.setText(a.decrypt(o.get("carrera").toString()));
            edad.setText(o.get("edad").toString());
            Picasso.with(getActivity()).load(o.get("foto").toString()).into(img);

        }catch (Exception e){

        }


        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getActivity(), "cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        getFbData();

    }

    private void getFbData() {
        try {
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            try {
                                JSONObject o=response.getJSONObject();
                                JSONArray rawName=o.getJSONObject("friends").getJSONArray("data");
                                String url="https://graph.facebook.com/"+o.getString("id")+"/picture?height=100&width=120";
                                Toast.makeText(getActivity(),String.valueOf(rawName.length()), Toast.LENGTH_SHORT).show();
                                System.out.println(o);
                                //pDialog.dismiss();

                                updateFbUser(email,o.getString("id"),String.valueOf(rawName.length()),url);
                            } catch (Exception e) {
                                System.out.println(e);
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email,gender,friends");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {

        }
    }

    public void updateFbUser(final String correo,final String fb,final String comun,final String foto) {
        System.out.println("updateFbUser");
        pDialog = new ProgressDialog(getActivity());
        String message = "Cargando...";

        SpannableString ss2 = new SpannableString(message);
        ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
        ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

        pDialog.setMessage(ss2);

        pDialog.setCancelable(true);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/fb";
        String url2 = "http://192.168.1.15:8080/Tesis_Ojeda/fb";
        System.out.println("antes del postRequest");
        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("on response");
                        System.out.println(response);
                        pDialog.dismiss();
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //updateFbUser(correo,fb,comun,foto);
                        System.out.println("error: "+error);
                        pDialog.dismiss();
                        // error
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("fb",fb);
                params.put("comun",comun);
                params.put("foto",foto);

                return params;
            }
        };
        queue.add(postRequest);
    }
}
