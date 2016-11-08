package com.ulima.carpool;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ulima.carpool.Utils.SessionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class PreferencesFragment extends Fragment {

    EditText univ,carrera,sexo,fb,edad,ciclo,car;
    int uni,career,sex,fb2,age,cicle,carac;
    Button btn;
    SessionManager session;
    public PreferencesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PreferencesFragment newInstance() {
        PreferencesFragment fragment = new PreferencesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_preferences, container, false);
        univ=(EditText)v.findViewById(R.id.pref_univ);
        carrera=(EditText)v.findViewById(R.id.pref_carrera);
        sexo=(EditText)v.findViewById(R.id.pref_sexo);
        fb=(EditText)v.findViewById(R.id.pref_fb);
        edad=(EditText)v.findViewById(R.id.pref_edad);
        ciclo=(EditText)v.findViewById(R.id.pref_ciclo);
        car=(EditText)v.findViewById(R.id.pref_car);
        btn=(Button) v.findViewById(R.id.btn_pref);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fb2=Integer.parseInt(fb.getText().toString());
                sex=Integer.parseInt(sexo.getText().toString());
                age=Integer.parseInt(edad.getText().toString());
                career=Integer.parseInt(carrera.getText().toString());
                cicle=Integer.parseInt(ciclo.getText().toString());
                uni=Integer.parseInt(univ.getText().toString());
                carac=Integer.parseInt(car.getText().toString());


//                Toast.makeText(getActivity(), "univ: "+uni, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), univ.getText().toString(), Toast.LENGTH_SHORT).show();
                boolean a=false,b=false,c=false;
                if((age+sex+fb2)>10){
                    createDialog("Característica").show();
                    a=false;
                }else{
                    a=true;
                }

                if((career+cicle)>10){
                    createDialog("Universidad").show();
                    b=false;
                }else{
                    b=true;
                }

                if((uni+carac)>10){
                    createDialog("").show();
                    c=false;
                }else{
                    c=true;
                }

                if(a==true && b==true && c==true){



                    JSONObject o=new JSONObject();
                    o.put("edad",age);
                    o.put("sexo",sex);
                    o.put("fb",fb2);
                    o.put("carrera",career);
                    o.put("ciclo",cicle);
                    o.put("carac",carac);
                    o.put("uni",uni);

                    session.createPesos(o.toString());

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment lista=ListTripFragment.newInstance();
                    ft.replace(R.id.flaContenido,lista);
                    //toolbar.setTitle("Viajes");
                    ft.commit();
                    //dl.closeDrawers();
                }

            }
        });

        return v;
    }

    public AlertDialog createDialog(final String factor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle("Valores incorrectos")

                .setMessage("La suma de los Factores "+factor+" debe ser menor o igual a 10")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }



}
