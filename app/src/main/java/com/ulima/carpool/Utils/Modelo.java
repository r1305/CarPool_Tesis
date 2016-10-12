package com.ulima.carpool.Utils;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by Julian on 02/09/2016.
 */
public class Modelo {


    public Alumno setDatosA(String alumno) {
        Alumno a = new Alumno();
        AES aes=new AES();
        JSONParser p = new JSONParser();
        JSONObject o;
        try {


            o = (JSONObject) p.parse(alumno);
            //System.out.println(o.get("carrera") + "-" + o.get("ciclo") + "-" + o.get("sexo") + "-" + o.get("edad"));
            a.setNombres(aes.decrypt(o.get("nombre").toString()));
            a.setCarrera(aes.decrypt((String)o.get("carrera")));
            a.setCiclo(Integer.parseInt(String.valueOf((long)o.get("ciclo"))));
            a.setSexo((String)o.get("sexo"));
            a.setEdad(Integer.parseInt(String.valueOf((long)o.get("edad"))));
            a.setComun((int)o.get("comun"));
            System.out.println("alumno A: " + a.getSexo() + "-" + a.getCarrera() + "-" + a.getCiclo() + "-" + a.getEdad());


        } catch (Exception e) {
            System.out.println(e);
        }
        return a;
    }

    public Alumno setDatosB(String alumno) {
        Alumno b = new Alumno();
        AES aes=new AES();
        JSONParser p = new JSONParser();
        JSONObject o;
        try {

            o = (JSONObject) p.parse(alumno);
            b.setNombres(aes.decrypt(o.get("nombre").toString()));
            b.setCarrera(aes.decrypt((String)o.get("carrera")));
            b.setCiclo(Integer.parseInt(String.valueOf((long)o.get("ciclo"))));
            b.setSexo((String)o.get("sexo"));
            b.setEdad(Integer.parseInt(String.valueOf((long)o.get("edad"))));
        } catch (Exception e) {
            System.out.println(e);
        }
        return b;
    }

    public double calcularAfinidadEdad(Alumno alumno, Alumno alumno2) {
        double afinidadEdad;
        int diferenciaEdades, edad1, edad2;

        edad1 = alumno.getEdad();
        edad2 = alumno2.getEdad();

        if (edad1 - edad2 > 0) {
            diferenciaEdades = edad1 - edad2;
        } else if (edad1 - edad2 < 0) {
            diferenciaEdades = (edad1 - edad2) * -1;
        } else {
            diferenciaEdades = 0;
        }

        if (diferenciaEdades <= 2) {
            afinidadEdad = 1;
        } else if (diferenciaEdades > 2 & diferenciaEdades <= 4) {
            afinidadEdad = 0.7;
        } else if (diferenciaEdades > 4 & diferenciaEdades <= 6) {
            afinidadEdad = 0.4;
        } else {
            afinidadEdad = 0.2;
        }
        return afinidadEdad;
    }

    public double calcularAfinidadSexo(Alumno alumno, Alumno alumno2) {
        double afinidadSexo;
        String sexo1, sexo2;

        sexo1 = alumno.getSexo();
        sexo2 = alumno2.getSexo();

        if (sexo1.equalsIgnoreCase(sexo2)) {
            afinidadSexo = 1;
        } else {
            afinidadSexo = 0.5;
        }
        return afinidadSexo;
    }

    public double calcularAfinidadFacebook(Alumno alumno) {
        double afinidadFb;

        if (alumno.getComun() >= 25) {
            afinidadFb = 1;
        } else if (alumno.getComun() < 25 & alumno.getComun() >= 14) {
            afinidadFb = 0.8;
        } else if (alumno.getComun() < 14 & alumno.getComun() >= 7) {
            afinidadFb = 0.5;
        } else if (alumno.getComun() < 7 & alumno.getComun() >= 3) {
            afinidadFb = 0.3;
        } else if (alumno.getComun() < 3 & alumno.getComun() >= 1) {
            afinidadFb = 0.2;
        } else {
            afinidadFb = 0;
        }
        return afinidadFb;
    }

    public double calcularAfinidadCarrera(Alumno alumno, Alumno alumno2) {
        String escuela1, escuela2;
        double afinidadCarrera;

        String ingenieria[] = {"Ingenieria de Sistemas", "Ingenieria Industrial", "Arquitectura"};
        String negocios[] = {"Administracion", "Marketing", "Negocios Internacionales", "Economia", "Contabilidad"};
        String humanidades[] = {"Psicologia", "Comunicaciones", "Derecho"};

        if (alumno.getCarrera().equalsIgnoreCase("Ingenieria de Sistemas") || alumno.getCarrera().equalsIgnoreCase("Ingenieria Industrial") || alumno.getCarrera().equalsIgnoreCase("Arquitectura")) {
            escuela1 = "Ingenieria";
        } else if (alumno.getCarrera().equalsIgnoreCase("Psicologia") || alumno.getCarrera().equalsIgnoreCase("Comunicaciones") || alumno.getCarrera().equalsIgnoreCase("Derecho")) {
            escuela1 = "Humanidades";
        } else {
            escuela1 = "Negocios";
        }

        if (alumno2.getCarrera().equalsIgnoreCase("Ingenieria de Sistemas") || alumno2.getCarrera().equalsIgnoreCase("Ingenieria Industrial") || alumno2.getCarrera().equalsIgnoreCase("Arquitectura")) {
            escuela2 = "Ingenieria";
        } else if (alumno2.getCarrera().equalsIgnoreCase("Psicologia") || alumno2.getCarrera().equalsIgnoreCase("Comunicaciones") || alumno2.getCarrera().equalsIgnoreCase("Derecho")) {
            escuela2 = "Humanidades";
        } else {
            escuela2 = "Negocios";
        }

        if (escuela1.equalsIgnoreCase(escuela2)) {
            afinidadCarrera = 1;
        } else if (escuela1.equalsIgnoreCase("Ingenieria") && escuela2.equalsIgnoreCase("Negocios")) {
            afinidadCarrera = 0.7;
        } else if (escuela1.equalsIgnoreCase("Negocios") && escuela2.equalsIgnoreCase("Ingenieria")) {
            afinidadCarrera = 0.7;
        } else {
            afinidadCarrera = 0.2;
        }

        return afinidadCarrera;
    }

    public double calcularAfinidadCiclo(Alumno alumno, Alumno alumno2) {
        int ciclo1, ciclo2, diferenciaCiclos;
        double afinidadCiclo;

        ciclo1 = alumno.getCiclo();
        ciclo2 = alumno2.getCiclo();

        if (ciclo1 >= ciclo2) {
            diferenciaCiclos = ciclo1 - ciclo2;
        } else {
            diferenciaCiclos = ciclo2 - ciclo1;
        }

        if (diferenciaCiclos <= 1) {
            afinidadCiclo = 1;
        } else if (diferenciaCiclos == 2) {
            afinidadCiclo = 0.7;
        } else if (diferenciaCiclos > 2 && diferenciaCiclos <= 5) {
            afinidadCiclo = 0.4;
        } else {
            afinidadCiclo = 0.2;
        }

        return afinidadCiclo;
    }

    public double calcularAfinidadCaracteristicas(Alumno a, Alumno b) {
        double afinidadEdad = this.calcularAfinidadEdad(a, b);
        double afinidadSexo = this.calcularAfinidadSexo(a, b);
        double afinidadFb = this.calcularAfinidadFacebook(a);
        double factorEdad, factorSexo, factorFb, afinidadCaracteristicas;

        factorEdad = 0.2;
        factorSexo = 0.2;
        factorFb = 0.6;

        afinidadCaracteristicas = afinidadEdad * factorEdad + afinidadSexo * factorSexo + afinidadFb * factorFb;
        return afinidadCaracteristicas;
    }

    public double calcularAfinidadUniversidad(Alumno a, Alumno b) {
        double afinidadCarrera = this.calcularAfinidadCarrera(a, b);
        double afinidadCiclo = this.calcularAfinidadCiclo(a, b);
        double factorCarrera, factorCiclo, afinidadUniversidad;

        factorCarrera = 0.5;
        factorCiclo = 0.5;

        afinidadUniversidad = afinidadCarrera * factorCarrera + afinidadCiclo * factorCiclo;
        return afinidadUniversidad;
    }

    public double calcularAfinidadTotal(Alumno a, Alumno b) {
        double afinidadCaracteristicas = this.calcularAfinidadCaracteristicas(a, b);
        double afinidadUniversidad = this.calcularAfinidadUniversidad(a, b);
        double factorCaracteristicas, factorUniversidad, afinidadTotal;

        factorCaracteristicas = 0.8;
        factorUniversidad = 0.2;

        afinidadTotal = afinidadCaracteristicas * factorCaracteristicas + afinidadUniversidad * factorUniversidad;
        return afinidadTotal;
    }

}
