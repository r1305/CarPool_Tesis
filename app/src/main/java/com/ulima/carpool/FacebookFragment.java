package com.ulima.carpool;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ulima.carpool.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacebookFragment extends Fragment {

    String email;
    Button btn;
    SessionManager session;
    Intent i;
    ProgressDialog pDialog;

    public FacebookFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FacebookFragment newInstance() {
        FacebookFragment fragment = new FacebookFragment();
        return fragment;
    }
    CallbackManager callbackManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getBaseContext());

        callbackManager = CallbackManager.Factory.create();
        session=new SessionManager(getActivity());
        HashMap<String,String>u=session.getUserDetails();
        email=u.get(SessionManager.KEY_EMAIL);
        //Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        final LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        btn=(Button)view.findViewById(R.id.salir);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                LoginManager.getInstance().logOut();
            }
        });

        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_friends"));


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //Toast.makeText(getActivity(), "Cargando...", Toast.LENGTH_LONG).show();
                pDialog = new ProgressDialog(getActivity());
                String message = "Cargando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getActivity(), "canceladp", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
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
                                updateFbUser(email,o.getString("id"),String.valueOf(rawName.length()));
                                i=new Intent(getActivity(),MainActivity.class);
                            } catch (Exception e) {
                                e.printStackTrace();
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

    public void updateFbUser(final String correo,final String fb,final String comun) {


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/fb";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        pDialog.dismiss();
                        startActivity(i);
                        getActivity().finishAffinity();
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("fb",fb);
                params.put("comun",comun);

                return params;
            }
        };
        queue.add(postRequest);
    }
}
