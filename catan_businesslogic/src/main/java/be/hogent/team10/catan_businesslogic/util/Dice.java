package be.hogent.team10.catan_businesslogic.util;

import java.util.Random;

/**
 *
 * @author jonasanseeuw
 */
public class Dice extends Observable {

    private Random random = new Random();
    private int value;

    /**
     * @return the value of the first dice
     */
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        setChanged();
        notifyObservers();
    }

    public Dice rollDice() {
        value = (1 + random.nextInt(6)) + (1 + random.nextInt(6));

        return this;
    }

    public void synchonize(Dice dice) {
        if(dice.getValue() != value){
            setValue(dice.getValue());
            setChanged();
            notifyObservers();
        }
    }
}
