package eksamenTestFX;

/**
 * Class represents a currency
 */
public class Currency {
    //The full name of the currency
    private String name;
    //The currency's position
    private int position;
    //The shortened name of the currency
    private String id;

    /**
     * Sets up the values of the currency
     * @param n The name of the currency
     * @param pos The currency's position
     * @param i The currency's id
     */
    public Currency(String n, int pos, String i){
        name = n;
        position = pos;
        id = i;
    }

    /**
     * Getter for the name field
     * @return The name of the currency
     */
    public String getName(){
        return name;
    }

    /**
     * Getter for the id field
     * @return The id of the currency
     */
    public String getId(){
        return id;
    }

    /**
     * Getter for the position field
     * @return The position of the currency
     */
    public int getPosition(){
        return position;
    }

}
