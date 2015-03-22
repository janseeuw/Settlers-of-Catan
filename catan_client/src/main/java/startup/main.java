/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package startup;

import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;

/**
 *
 * @author HP
 */
public class main {

    public static void main(String args[]) {
        new main();
    }

    public main() {
        Object g = new InitializationState(null);
        String s = "Hello";
        if (s instanceof java.lang.String) {
            System.out.println("is a String");
        }
        if(g instanceof SetupState){
            System.out.println("yes");
        }
    }
}
