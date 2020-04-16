/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.settings;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import library.alertMaker.AlertMaker;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class Preferences {
    
    public static final String Confiq="confiq.txt";
    int nDays;
    float finePerDay;
    String username;
    String password;
    public Preferences(){
        nDays= 14;
        finePerDay= 2;
        username="admin";
        setPassword("admin");
    }    

    public int getnDays() {
        return nDays;
    }

    public void setnDays(int nDays) {
        this.nDays = nDays;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password.length()<16){
        this.password = DigestUtils.shaHex(password);
        }else
            this.password=password;
    }
    
    public static void initConfiq(){
        Writer writer=null;
        try {
            Preferences preference=new Preferences();
            Gson gson=new Gson();
            writer = new FileWriter(Confiq);
            gson.toJson(preference,writer);
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public static Preferences getPreferences(){
        Gson gson=new Gson();
        Preferences preferences=new Preferences();
        try {
            preferences=gson.fromJson(new FileReader(Confiq), Preferences.class);
        } catch (FileNotFoundException ex) {
            //initConfiq();
            //Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(Preferences.class.getName()).info("Config file is missing. Creating new one with default config");
            initConfiq();
        }
        return preferences;
        
    } 
    public static void writePreferences(Preferences preference){
            Writer writer=null;
        try {
            Gson gson=new Gson();
            writer = new FileWriter(Confiq);
            gson.toJson(preference,writer);
            AlertMaker.showSimpleAlert("Success", "Settings Updated");
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            AlertMaker.showErrorMessage(ex, "Failed", "Can't Save Configuration File");
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    
    }
}
