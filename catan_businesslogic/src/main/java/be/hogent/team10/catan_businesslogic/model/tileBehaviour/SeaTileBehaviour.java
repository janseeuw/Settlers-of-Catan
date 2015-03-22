package be.hogent.team10.catan_businesslogic.model.tileBehaviour;

import be.hogent.team10.catan_businesslogic.model.TileType;

/* mergedate : OK
 * 
 */
/**
 *
 * @author HP
 */
public class SeaTileBehaviour implements TileBehaviour {
    TileType tileType = TileType.SEA;
    
    public SeaTileBehaviour(){
        
    }
    public void validateSetRobber() throws Exception {
        throw new UnsupportedOperationException("You cannot move the robber here");
    }

    public void validateSetNumber(int number) throws Exception {
        if(number != 0)
            throw new UnsupportedOperationException("You cannot assign a number to this tile");
    }

    public void validateSetResource() throws Exception {
        throw new UnsupportedOperationException("You cannot assign a resource to this tile");
    }

    public TileType getTileType() {
        return tileType;
    }
}
