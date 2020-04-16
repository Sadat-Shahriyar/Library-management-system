/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.encryption;

import java.io.Serializable;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class CipherSpec implements Serializable {

    private final byte[] key;
    private final byte[] iv;

    public CipherSpec(byte[] key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIV() {
        return iv;
    }

    public boolean isValid() {
        return key != null && iv != null;
    }

    @Override
    public String toString() {
        return "CipherSpec{" + "key=" + key + ", iv=" + iv + '}';
    }
    
}