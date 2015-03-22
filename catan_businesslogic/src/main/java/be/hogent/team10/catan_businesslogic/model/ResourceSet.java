package be.hogent.team10.catan_businesslogic.model;
/* mergedate : OK
 * 
 */
import be.hogent.team10.catan_businesslogic.model.exception.ResourceException;
import be.hogent.team10.catan_businesslogic.util.Observable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author HP
 */
public class ResourceSet extends Observable{

    private Map<String, Integer> resources;
    private int total;
    
    public ResourceSet() {
        resources = new HashMap<String, Integer>();
        for (Resource r : Resource.values()) {
            resources.put(r.name(), 0);
        }
    }

    public ResourceSet(int brick, int wood, int wool, int grain, int ore) {
        resources = new HashMap<String, Integer>();
        resources.put(Resource.BRICK.name(), brick);
        resources.put(Resource.GRAIN.name(), grain);
        resources.put(Resource.LUMBER.name(), wood);
        resources.put(Resource.ORE.name(), ore);
        resources.put(Resource.WOOL.name(), wool);
    }
    /**
     * used to see how much resources the other players have in total. since this can't be calculated, it has a fixed value.
     * @return 
     */
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Map<String, Integer> getResources() {
        return this.resources;
    }

    public void add(Resource resource, int amount) {
        resources.put(resource.name(), resources.get(resource.name()) + amount);
    }

    public int getAmount(Resource resource) {
        return resources.get(resource.name());
    }
    
    public int getAmount(String resource){
        return resources.get(resource);
    }

    public int getTotalAmount() {
        int result = 0;
        for (Resource r : Resource.values()) {
            result += resources.get(r.name());
        }
        return result;
    }
    

    /**
     *
     * @param resourceset resourceset to subract
     * @see checkEnoughResources(ResourceSet resourceSet) When you wish to
     * subract a resourceset from another resourceSet, the system must verify
     * that enough resources are available.
     *
     * @throws ResourceException If one of the resources is too low, and the
     * result would be negative, a ResourceException is thrown.
     *
     */
    public void remove(ResourceSet resourceset) throws Exception {
        if (!checkEnoughResources(resourceset)) {
            throw new ResourceException("Niet genoeg grondstoffen beschikbaar om deze actie uit te voeren.");
        }
        // only after verifying whether enough resources are available you may remove
        // resources from the resourceset;
        for (Resource r : Resource.values()) {
            resources.put(r.name(), resources.get(r.name()) - resourceset.getAmount(r));
        }
    }

    /**
     *
     * @param resourceset
     * @return Checks if the amount every resource is higher or equal to the
     * number of resources
     */
    public boolean checkEnoughResources(ResourceSet resourceset) {
        for (Resource r : Resource.values()) {
            if (resources.get(r.name()) < resourceset.getAmount(r)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Resourceset : ";
        for (Resource r : Resource.values()) {
            s += "\n " + r.name() + " : " + resources.get(r.name());
        }
        return s;
    }

    public void add(ResourceSet resources) {
        for (Resource r : Resource.values()) {
            add(r, resources.getAmount(r));
        }
    }
    
    public void setResource(Resource resource, int amount){
        resources.put(resource.toString(), amount);
    }
    
    public void synchronize(ResourceSet newResourceSet){
        this.total = newResourceSet.getTotal();
        for(Resource r : Resource.values()){
            if(newResourceSet.getAmount(r) != this.getAmount(r)){
                this.setResource(r, newResourceSet.getAmount(r));
                setChanged();
            }
        }
        notifyObservers();
    }
}
