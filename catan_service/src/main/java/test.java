
import be.hogent.team10.catan_businesslogic.model.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jonasanseeuw
 */
public class test {

    private static final Logger slf4jLogger = LoggerFactory.getLogger(test.class);

    public static void main(String[] args) {
        List<Player> nextplayers = new ArrayList<Player>();
        nextplayers.add(new Player(1));
        nextplayers.get(0).setTimeJoined(new GregorianCalendar(2010, 2, 11));
        nextplayers.add(new Player(2));
        nextplayers.get(1).setTimeJoined(new GregorianCalendar(2010, 2, 13));
        nextplayers.add(new Player(3));
        nextplayers.get(2).setTimeJoined(new GregorianCalendar(2010, 2, 14));
        Collections.sort(nextplayers, new Comparator<Player>() {
            @Override
            public int compare(Player t, Player t1) {
                if (t.getTimeJoined().after(t1.getTimeJoined())) {
                    return -1;
                }
                return 1;
            }
        });
        for (Player p : nextplayers) {
            System.out.println(p.getId());
        }
    }
}