package be.hogent.team10.catan_businesslogic.util;

import java.awt.Color;

/**
 *
 * @author jonasanseeuw
 */

/*
 * Groen: 1f6016
 * Rood: a31514
 * Geel: bb9a00
 * Blauw: 3f63a0
 * */
public enum PlayerColor {

    RED, BLUE, GREEN, YELLOW;

    public Color getColor() {
        if (name().equals("RED")) {
            return Color.RED;
        }
        if (name().equals("BLUE")) {
            return Color.BLUE;
        }
        if (name().equals("GREEN")) {
            return Color.GREEN;
        }
        if (name().equals("YELLOW")) {
            return Color.YELLOW;
        }
        return Color.YELLOW;
    }
}
