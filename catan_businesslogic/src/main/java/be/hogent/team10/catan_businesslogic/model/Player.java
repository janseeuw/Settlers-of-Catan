package be.hogent.team10.catan_businesslogic.model;

import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import be.hogent.team10.catan_businesslogic.util.PlayerColor;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HP
 */
public class Player extends Observable {

    @Expose
    private String name;
    @Expose
    private int id;
    @Expose
    private String email;
    @Expose
    private String avatar;
    @Expose
    private char status;
    @Expose
    private boolean admin;
    private String password;
    private int facebook;
    private Calendar timeJoined;
    private int points;
    private int knightforce;
    private boolean biggestKnightForce;
    private int longestTraderoute;
    private int availableStreets;
    private int availableCities;
    private int availableSettlements;
    private ResourceSet resources;
    private List<Node> capturedNodes;
    private List<Border> capturedBorders;
    private Map<Integer, DevelopmentCard> capturedDevelopmentCards;
    private PlayerColor playerColor;

    public Player() {
        this.availableCities = 4;
        this.availableSettlements = 5;
        this.availableStreets = 15;
        this.knightforce = 0;
        this.longestTraderoute = 0;
        this.points = 0;
        this.capturedNodes = new ArrayList<Node>();
        this.capturedBorders = new ArrayList<Border>();
        this.capturedDevelopmentCards = new HashMap<Integer, DevelopmentCard>();
        this.resources = new ResourceSet();
        this.biggestKnightForce = false;
    }

    public Player(int id) {
        this.id = id;
    }

    public Player(String name, String password) {
        this();
        this.name = name;
        this.password = password;
    }

    public Player(String name, String email, String password) {
        this(name, password);
        this.email = email;
    }

    public Player(String name, int id, String email, String password) {
        this(name, email, password);
        this.password = password;
        this.id = id;
    }

    public void addResource(Resource resource, int amount) {
        resources.add(resource, amount);
        setChanged();
        notifyObservers();
    }

    public void removeResources(ResourceSet resources) throws Exception {
        this.resources.remove(resources);
        setChanged();
        notifyObservers();
    }

    public ResourceSet getResources() {
        return resources;
    }

    public void setResources(ResourceSet resources) {
        this.resources = resources;
        setChanged();
        notifyObservers();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getTimeJoined() {
        return timeJoined;
    }

    public void setTimeJoined(Calendar time) {
        this.timeJoined = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        setChanged();
        notifyObservers();
    }

    public String getAvatar() {
        return avatar;
    }

    public int getFacebook() {
        return facebook;
    }

    public void setFacebook(int facebook) {
        this.facebook = facebook;
    }

    /**
     * @return the availableStreets
     */
    public int getAvailableStreets() {
        return availableStreets;
    }

    /**
     * @param availableStreets the availableStreets to set
     */
    public void setAvailableStreets(int availableStreets) {
        this.availableStreets = availableStreets;
    }

    /**
     * @return the availableCities
     */
    public int getAvailableCities() {
        return availableCities;
    }

    /**
     * @param availableCities the availableCities to set
     */
    public void setAvailableCities(int availableCities) {
        this.availableCities = availableCities;
    }

    /**
     * @return the availableSettlements
     */
    public int getAvailableSettlements() {
        return availableSettlements;
    }

    /**
     * @param availableSettlements the availableSettlements to set
     */
    public void setAvailableSettlements(int availableSettlements) {
        this.availableSettlements = availableSettlements;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Node> getCapturedNodes() {
        return capturedNodes;
    }

    public Map<Integer, DevelopmentCard> getCapturedDevelopmentCards() {
        return this.capturedDevelopmentCards;
    }

    public void setCapturedNodes(List<Node> capturedNodes) {
        this.capturedNodes = capturedNodes;
    }

    public List<Border> getCapturedBorders() {
        return capturedBorders;
    }

    public void setCapturedBorders(List<Border> capturedBorders) {
        this.capturedBorders = capturedBorders;
    }

    public void setCapturedDevelopmentCards(Map<Integer, DevelopmentCard> capturedDevelopmentCards) {
        this.capturedDevelopmentCards = capturedDevelopmentCards;
    }

    public int getKnightforce() {
        return knightforce;
    }

    public void setKnightforce(int knightforce) {
        this.knightforce = knightforce;
    }

    public int getLongestTraderoute() {
        return longestTraderoute;
    }

    public void setLongestTraderoute(int longestTraderoute) {
        this.longestTraderoute = longestTraderoute;
    }

    public boolean getBiggestKnightForce() {
        return this.biggestKnightForce;
    }

    public void setBiggestKnightForce(boolean biggestKnightForce) {
        this.biggestKnightForce = biggestKnightForce;
    }

    public void setPlayerColor(PlayerColor color) {
        this.playerColor = color;
        setChanged();
        notifyObservers();
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    /**
     *
     * This method is used to build a settlement or a city. It also gives the current player an extra point whenever he
     * has build or upgraded something.
     *
     * @param Node node Represents the node that will be added or updated of the current player.
     * @throws Exception
     */
    public void addNode(Node node) throws Exception {
        if (capturedNodes == null) {
            capturedNodes = new ArrayList<Node>();
        }
        int index = capturedNodes.indexOf(node);
        if (index != -1 && capturedNodes.get(index).getBuildingtype() != node.getBuildingtype()) {
            capturedNodes.remove(index);
        }

        if (index == -1 || capturedNodes.get(index).getBuildingtype() != node.getBuildingtype()) {
            capturedNodes.add(node);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Adds a street to the capturedBorders of the current Player and notifies the view when this is happend.
     *
     * @param border
     * @throws Exception
     */
    public void addBorder(Border border) throws Exception {
        if (!capturedBorders.contains(border)) {
            border.setOwner(this);
            this.capturedBorders.add(border);
            setChanged();
            notifyObservers();
        }
    }

    public void addDevelopmentCard(DevelopmentCard developmentCard) throws Exception {
        if (!(capturedDevelopmentCards.containsKey(id)) || (capturedDevelopmentCards.get(developmentCard.getId()) != null && capturedDevelopmentCards.get(developmentCard.getId()).getPlayed() != developmentCard.getPlayed())) {
            this.capturedDevelopmentCards.put(developmentCard.getId(), developmentCard);
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;

        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public ResourceSet getTradeRatio() {
        return new ResourceSet(4, 4, 4, 4, 4);
    }

    public List<String> getTradeableResources(String wanted, int quantity) {
        List<String> res = new ArrayList<String>();
        ResourceSet ratio = getTradeRatio();
        for (Resource r : Resource.values()) {
            // don't trade same resourde for less -  
            /*
             * don't use == for strings!
             */
            if (!r.name().equalsIgnoreCase(wanted) && this.getResources().getAmount(r.name()) >= ratio.getAmount(wanted) * quantity) {
                res.add(r.name());
            }
        }
        return res;
    }

    /*
     * debug. you can buy grain for 3:1
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerAdmin(char bool) {
        if (bool == 'N') {
            this.admin = false;
        } else {
            this.admin = true;
        }
    }

    public boolean getPlayerAdmin() {
        return admin;
    }

    /**
     * Returns the amount of cards of a player based on the type of card. 0=KnightCards 1=University 2=Total
     *
     * @param type
     * @return
     */
    public int getCards(int type) {
        int total = 0;
        // Since their aren't many universitycards, we just count how many the player has of this type.
        // Based on that we know how much of the total cards are knightcards.
        if (!this.capturedDevelopmentCards.isEmpty()) {

            switch (type) {
                case 0:
                    //Knight
                    for (DevelopmentCard d : this.capturedDevelopmentCards.values()) {
                        if (d.getDevelopmentCardType() == DevelopmentCardType.KNIGHT && !d.getPlayed()) {
                            total++;
                        }
                    }
                    break;
                case 1:
                    //University
                    for (DevelopmentCard d : this.capturedDevelopmentCards.values()) {
                        if (d.getDevelopmentCardType() == DevelopmentCardType.UNIVERSITY && !d.getPlayed()) {
                            total++;
                        }
                    }
                    break;
                case 2:
                    // Total
                    total = this.capturedDevelopmentCards.size();
                    break;
            }

            return total;
        } else {
            return 0;
        }
    }

    public DevelopmentCard getLatestCard() {
        if (!this.getCapturedDevelopmentCards().isEmpty()) {
            return this.getCapturedDevelopmentCards().get(this.getCapturedDevelopmentCards().size() - 1);
        }
        return null;
    }

    /**
     * Makes a predefined resource set based on the current player his resources. It divides the total amounts of
     * resources by 2
     *
     * @return
     */
    public ResourceSet makeResourceDivideSet() {
        ResourceSet goingBack = new ResourceSet();

        if (resources.getTotalAmount() >= 7) {
            int amount = 0;
            for (Resource r : Resource.values()) {
                goingBack.add(r, 0);
            }
            while (resources.getTotalAmount() - amount > (resources.getTotalAmount() / 2 + 1)) {
                for (Resource r : Resource.values()) {
                    if (resources.getAmount(r) - goingBack.getAmount(r) > 0) {
                        goingBack.add(r, 1);
                        amount++;
                    }
                }
            }
        }
        return goingBack;
    }

    /**
     * Subtracts a predefined resourceset from the player his resources based on his previous total of resources. This
     * method will be called in robber state when a player has more than 7 resources.
     *
     * @param r
     */
    public void divideResources(ResourceSet r) {
        for (Resource res : Resource.values()) {
            resources.add(res, -r.getAmount(res));
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Method that can calulate the points of a player any moment. Calculation is based on Captured nodes, captured &
     * played developmentCards. Every time we sync a player this method will be called all the players their points are
     * updated to the latest version
     */
    public void recalculatePoints() {
        int newpoints = 0;
        // If the player has the biggest knight force he will receive two extra points.
        if (biggestKnightForce) {
            newpoints += 2;
        }

        // Check if the current player has university cards he has played during the game.
        for (DevelopmentCard c : capturedDevelopmentCards.values()) {
            if (c.getPlayed() && c.getDevelopmentCardType() == DevelopmentCardType.UNIVERSITY) {
                newpoints++;
            } else if (c.getPlayed() && c.getDevelopmentCardType() == DevelopmentCardType.KNIGHT) {
                this.setKnightforce(this.getKnightforce() + 1);
            }
        }

        // Check all his nodes
        for (Node n : capturedNodes) {
            switch (n.getBuildingtype()) {
                case CITY:
                    newpoints += 2;
                    break;
                case SETTLEMENT:
                    newpoints += 1;
                    break;
            }
        }

        // If newpoints is not equal to points we set ourselves as changed so the GUI gets updated
        if (newpoints != points) {
            points = newpoints;
            setChanged();
        }

    }

    public void synchronize(Player updatedPlayer) {
        if (updatedPlayer.getResources() != null && updatedPlayer.getResources().getTotal() != getResources().getTotal()) {
            this.getResources().synchronize(updatedPlayer.getResources());
            setChanged();
        }
        if (this.playerColor != updatedPlayer.getPlayerColor() && updatedPlayer.getPlayerColor() != null) {
            this.setPlayerColor(updatedPlayer.getPlayerColor());
        }
        if (this.availableCities != updatedPlayer.getAvailableCities()) {
            availableCities = updatedPlayer.getAvailableCities();
            setChanged();
        }
        if (this.availableSettlements != updatedPlayer.getAvailableSettlements()) {
            availableSettlements = updatedPlayer.getAvailableSettlements();
            setChanged();
        }
        if (this.availableStreets != updatedPlayer.getAvailableStreets()) {
            availableStreets = updatedPlayer.getAvailableStreets();
            setChanged();
        }

        if (this.knightforce != updatedPlayer.getKnightforce()) {
            knightforce = updatedPlayer.getKnightforce();
            setChanged();
        }

        recalculatePoints();
        notifyObservers();
    }
}
