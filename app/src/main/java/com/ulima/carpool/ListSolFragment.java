package com.ulima.carpool;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ulima.carpool.Utils.SessionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSolFragment extends Fragment {


    ListView list;
    ProgressDialog pDialog;
    RecyclerView trips;
    SolicitudesRecyclerAdapter adapter;
    List<JSONObject> l2 = new ArrayList<>();
    SessionManager session;
    String user;

    private SwipeRefreshLayout swipeContainer;


    public ListSolFragment() {
        // Required empty public constructor
    }

    public static ListSolFragment newInstance() {
        ListSolFragment fragment = new ListSolFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity());
        HashMap<String, String> u = session.getUserDetails();
        user = u.get(SessionManager.KEY_EMAIL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_trip, container, false);

        list = (ListView) v.findViewById(R.id.viajes);

        pDialog = new ProgressDialog(getActivity());

        trips = (RecyclerView) v.findViewById(R.id.recycler_view_trips);
        trips.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new SolicitudesRecyclerAdapter(l2);
        trips.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout)v.findViewById(R.id.swipeContainer_trip);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getSolicitudes();
            }
        });




        String message = "Cargando solicitudes...";

        SpannableString ss2 = new SpannableString(message);
        ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
        ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

        pDialog.setMessage(ss2);

        pDialog.setCancelable(true);
        pDialog.show();
        getSolicitudes();


        return v;
    }

    public List<JSONObject> getSolicitudes() {
        final List<JSONObject> l = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/ListarSolicitudes";
        String url2 = "http://192.168.1.6:8080/Tesis_Ojeda/ListarSolicitudes";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //System.out.println("***** " +
                        swipeContainer.setRefreshing(false);
                        l2.clear();
                        JSONParser jp = new JSONParser();
                        JSONObject obj;
                        try {
                            obj = (JSONObject) jp.parse(response);
                            JSONArray ja = (JSONArray) obj.get("solicitudes");
                            System.out.println(ja);
                            for (int i = 0; i < ja.size(); i++) {
                                l2.add((JSONObject) ja.get(i));
                                //System.out.println(ja.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        } catch (Exception e) {
                            getSolicitudes();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);

                return params;
            }
        };
        ;
        queue.add(postRequest);


        return l;
    }

    public AlertDialog createRadioListDialog(final String user, final String act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("¿Le gustó la recomendación?")

                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //estad(user, act, rpta);
                        Toast.makeText(getActivity(), "¡Solicitud aceptada!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(),"¡Solicitud rechazada!",Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }



}
