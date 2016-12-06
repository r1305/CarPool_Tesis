package com.ulima.carpool.Utils;

import java.security.Key;
import java.security.SecureRandom;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Julian on 25/09/2016.
 */

public class AES {

    private static final String algo = "AES/CBC/PKCS5Padding";
    private static final byte[] keyValue=new byte[16];

    public String encrypt(String Data,String clave) throws Exception {

        Key key = generateKey(clave);
        byte[] iv = new byte[16];
        for(int i=0;i<16;i++){
            iv[i]=1;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        //cifrar
        Cipher c = Cipher.getInstance(algo);
        c.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    public String decrypt(String encryptedData,String clave) throws Exception {
        Key key = generateKey(clave);

        byte[] iv = new byte[16];
        for(int i=0;i<16;i++){
            iv[i]=1;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher c = Cipher.getInstance(algo);
        c.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        byte[] decordedValue = Base64.decode(encryptedData,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey(String clave) throws Exception {
        for(int i=0;i<16;i++){
            keyValue[i]=(byte)clave.charAt(i);
        }
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }
}
