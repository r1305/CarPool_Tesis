package com.ulima.carpool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {


        TextView usuario, estado;

        public ViewHolder(View itemView) {
            super(itemView);
            usuario = (TextView) itemView.findViewById(R.id.item_usuario);
            estado = (TextView) itemView.findViewById(R.id.item_estado);

        }
    }
}


