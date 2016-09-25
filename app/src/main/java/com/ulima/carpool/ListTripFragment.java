package com.ulima.carpool;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ListTripFragment extends Fragment {


    ListView list;
    ProgressDialog pDialog;
    RecyclerView trips;
    ViajesRecyclerAdapter adapter;
    List<JSONObject> l2=new ArrayList<>();

    public ListTripFragment() {
        // Required empty public constructor
    }

    public static ListTripFragment newInstance() {
        ListTripFragment fragment = new ListTripFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_list_trip, container, false);

        list=(ListView)v.findViewById(R.id.viajes);
        pDialog = new ProgressDialog(getActivity());

        trips=(RecyclerView)v.findViewById(R.id.recycler_view_trips);
        trips.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new ViajesRecyclerAdapter(l2);
        trips.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject o=(JSONObject)view.getTag();
                //Toast.makeText(getActivity(), o.toJSONString(), Toast.LENGTH_SHORT).show();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                Fragment details=DetailsFragment.newInstance(o.get("user").toString());
                ft.replace(R.id.flaContenido,details);
                ft.commit();
            }
        });

        String message = "Cargando viajes...";

        SpannableString ss2 = new SpannableString(message);
        ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
        ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

        pDialog.setMessage(ss2);

        pDialog.setCancelable(true);
        pDialog.show();
        getViajes();


        return v;
    }

    public List<JSONObject> getViajes() {
        final List<JSONObject> l = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/ListarViajes";
        String url2="http://192.168.1.6:8080/Tesis_Ojeda/ListarViajes";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("***** " + response);
                        JSONParser jp = new JSONParser();
                        JSONObject obj;
                        try {
                            obj = (JSONObject) jp.parse(response);
                            JSONArray ja = (JSONArray) obj.get("viajes");
                            System.out.println(ja);
                            for(int i=0;i<ja.size();i++){
                                l2.add((JSONObject)ja.get(i));
                                System.out.println(ja.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        } catch (Exception e) {
                            getViajes();
                            Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_SHORT).show();
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
        );
        queue.add(postRequest);


        return l;
    }

}
