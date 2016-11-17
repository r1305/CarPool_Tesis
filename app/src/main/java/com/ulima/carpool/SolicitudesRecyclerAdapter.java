package com.ulima.carpool;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Julian on 21/09/2016.
 */
public class SolicitudesRecyclerAdapter extends RecyclerView.Adapter<SolicitudesRecyclerAdapter.ViewHolder> {

    //List<JSONObject> list=new ArrayList<>();
    List<JSONObject> list = new ArrayList<>();
    Context c;
    View.OnClickListener listener;

    public SolicitudesRecyclerAdapter(List<JSONObject> list) {
        this.list = list;
    }

    @Override
    public SolicitudesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sol, parent, false));
    }

    @Override
    public void onBindViewHolder(final SolicitudesRecyclerAdapter.ViewHolder holder, int position) {
        final JSONObject o = list.get(position);
        holder.usuario.setText(o.get("idUsuario").toString());
        //System.out.println("holder: "+o);
        holder.estado.setText(o.get("estado").toString());
        holder.itemView.setTag(o);
        holder.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c, "¡Solicitud aceptada!", Toast.LENGTH_SHORT).show();
                updateEstado("Aceptado",o.get("_id").toString());
            }
        });
        holder.rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c, "¡Solicitud rechazada!", Toast.LENGTH_SHORT).show();
                updateEstado("Rechazado",o.get("_id").toString());
            }
        });
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();

        }
    }

//    public void setOnClickListener(View.OnClickListener listener) {
//        this.listener = listener;
//    }


    class ViewHolder extends RecyclerView.ViewHolder {


        TextView usuario, estado;
        Button aceptar,rechazar;

        public ViewHolder(View itemView) {
            super(itemView);
            usuario = (TextView) itemView.findViewById(R.id.item_usuario);
            estado = (TextView) itemView.findViewById(R.id.item_estado);
            aceptar=(Button)itemView.findViewById(R.id.aceptar);
            rechazar=(Button)itemView.findViewById(R.id.rechazar);


        }
    }

    public void updateEstado(final String estado,final String id) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://tesis-ojeda-carrasco.herokuapp.com/aceptar";
        String url2="http://192.168.1.13:8080/Tesis_Ojeda/aceptar";
        //**** validar si el usario ya registro un carro *****
        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(c, "Error: " + error, Toast.LENGTH_LONG).show();
//                        pDialog.dismiss();
                        System.out.println("error response: "+error);

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("estado", estado);
                params.put("id", id);
                return params;
            }
        };
        queue.add(postRequest);
    }
}


