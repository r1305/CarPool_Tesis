package com.ulima.carpool;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian on 21/09/2016.
 */
public class ViajesRecyclerAdapter extends RecyclerView.Adapter<ViajesRecyclerAdapter.ViewHolder>{

    //List<JSONObject> list=new ArrayList<>();
    List<JSONObject> list=new ArrayList<>();

    public ViajesRecyclerAdapter(List<JSONObject> list) {
        this.list = list;
    }

    @Override
    public ViajesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viajes,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViajesRecyclerAdapter.ViewHolder holder, int position) {
        JSONObject o=list.get(position);
        holder.destino.setText(o.get("destino").toString());
        System.out.println("holder: "+o);
        holder.auto.setText(o.get("placa").toString());
    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }else {
            return list.size();

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView destino,auto;

        public ViewHolder(View itemView) {
            super(itemView);
            destino=(TextView)itemView.findViewById(R.id.item_destino);
            auto=(TextView)itemView.findViewById(R.id.item_auto);


            //destino.setTextSize(25);
            //auto.setTextSize(25);

        }
    }
}

