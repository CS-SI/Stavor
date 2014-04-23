package model;

import android.content.Context;

import com.google.gson.Gson;

public class ModelSimulation {
	private Gson gson = new Gson();
    public ModelConfiguration config;
    public ModelState state;
    
    public ModelSimulation(Context ctx){
    	config = new ModelConfiguration(ctx);
    	state = new ModelState();
    }
    
    public synchronized String getInitializationJSON() {
        return gson.toJson(config);
    }
    
    public synchronized String getStateJSON() {
        return gson.toJson(state);
    }
    
    public synchronized ModelConfiguration getInitialization(){
    	return config;
    }
    
    public synchronized ModelState getState(){
    	return state;
    }
    
    public synchronized void updateInit(ModelConfiguration cfg){
    	config = cfg;
    }
    
    public synchronized void updateState(ModelState st){
    	state = st;
    }
}
