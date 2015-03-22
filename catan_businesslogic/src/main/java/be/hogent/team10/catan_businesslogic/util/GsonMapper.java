/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_businesslogic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Joachim
 */
public class GsonMapper<T> {
    
    Gson gson;
    public GsonMapper(){
       this.gson = new GsonBuilder().create(); 
    }
    
    public String toJson(T object){
        String gObject = gson.toJson(object);
        return gObject;
    }
    
    public T fromJson(String gString, Class t){
        T object = (T) gson.fromJson(gString, t);
        return object;
    }
    
}
