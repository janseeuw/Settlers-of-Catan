package be.hogent.team10.catan_businesslogic.model.tileBehaviour;

import be.hogent.team10.catan_businesslogic.model.TileType;

/* mergedate : OK
 * 
 */
/**
 *
 * @author HP
 */
public class DesertTileBehaviour implements TileBehaviour {
    TileType tileType = TileType.DESERT;
    
    public DesertTileBehaviour(){
    }
    
    public void validateSetRobber() {
        // empty
    }

    public void validateSetNumber(int number) throws Exception {
        if(number != 0)
            throw new UnsupportedOperationException("Er kan geen nummer toegekend worden aan een woestijntegel");
    }

    public void validateSetResource() throws Exception {
        throw new UnsupportedOperationException("Er kan geen grondstof toegekend worden aan de woestijntegel");
    }

    public TileType getTileType() {
        return TileType.DESERT;
    }
}
