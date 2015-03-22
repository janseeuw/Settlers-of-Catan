/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_businesslogic.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HP
 */
public abstract class Observable {

    private boolean changed;
    private List<Observer> observers;

    public void setChanged() {
        this.changed = true;
    }
    
    public List<Observer> getObservers(){
        if(observers == null)
            observers = new ArrayList<Observer>();
        return observers;
    }

    public int countObservers() {
        if(observers != null)
            return observers.size();
        return 0;
    }

    public void notifyObservers(Object c) {
        if (changed && observers != null) {
            for (Observer o : observers) {
                o.update(this, c);
            }
        }
        changed = false;
    }

    protected void copyObservers(Observable o){
        if(o != null && o.observers!=null)
            for(Observer obs : o.observers){
                addObserver(obs);
            }
    }
    
    public void notifyObservers() {
        notifyObservers(null);
    }

    public void deleteObserver(Observer o) {
        if (observers != null) {
            this.observers.remove(o);
        }
    }

    public void addObserver(Observer o) {
        if (observers == null) {
            observers = new ArrayList<Observer>();
        }
        this.observers.add(o);
        setChanged();
        o.update(this, null);
    }
}