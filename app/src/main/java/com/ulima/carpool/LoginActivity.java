package com.ulima.carpool;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;
import com.ulima.carpool.Utils.SessionManager;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    SignInButton btn;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session=new SessionManager(this);

        btn=(SignInButton)findViewById(R.id.btn_google);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUserAccount();
            }
        });

    }

    public String validate(final String user) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://tesis-ojeda-carrasco.herokuapp.com/validate";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("***"+response);
                        if(response.equals("error")){
                            pDialog.dismiss();
                            Intent i=new Intent(LoginActivity.this, RegisterActivity.class);
                            session.createLoginSession(user);
                            startActivity(i);
                            LoginActivity.this.finishAffinity();
                            //Toast.makeText(LoginActivity.this, "Debe registrarse", Toast.LENGTH_SHORT).show();

                        }
                        if(response.equals("fail")){
                            pDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Use correo ulima", Toast.LENGTH_SHORT).show();

                        }
                        if(response.equals(user)){
                            pDialog.dismiss();
                            Intent i=new Intent(LoginActivity.this, MainActivity.class);
                            session.createLoginSession(response);

                            startActivity(i);
                            LoginActivity.this.finishAffinity();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("correo", user);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                15,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

        return user;
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    ProgressDialog pDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                pDialog = new ProgressDialog(LoginActivity.this);
                String message = "Cargando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                validate(mEmail);
                //Toast.makeText(LoginActivity.this, mEmail, Toast.LENGTH_SHORT).show();
                // With the account name acquired, go get the auth token
                //getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
