package be.hogent.team10.catan_businesslogic.model.tileBehaviour;

import be.hogent.team10.catan_businesslogic.model.TileType;

/* mergedate : OK
 * 
 */
/**
 *
 * @author HP
 */
public class ResourceTileBehaviour implements TileBehaviour {
    TileType tileType = TileType.RESOURCE;
    
    public ResourceTileBehaviour(){
        
    }
    public void validateSetRobber() {
        // empty
    }

    public void validateSetNumber(int number) {
        // empty
    }

    public void validateSetResource() {
        // empty
    }

    public TileType getTileType() {
        return tileType;
    }
}
