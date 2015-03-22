package be.hogent.team10.catan_businesslogic.model.tileBehaviour;

import be.hogent.team10.catan_businesslogic.model.TileType;

/* mergedate : OK
 * 
 */
/**
 *
 * @author HP
 */
public interface TileBehaviour {

    public abstract void validateSetRobber() throws Exception;

    public abstract void validateSetNumber(int number) throws Exception;

    public abstract void validateSetResource() throws Exception;

    public abstract TileType getTileType();
}
