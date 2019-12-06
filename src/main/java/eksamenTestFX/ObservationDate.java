package eksamenTestFX;

public class ObservationDate {

    //Start time
    private String start;
    //The date
    private String name;
    //End time
    private String end;
    //Id
    private String id;

    /**
     * Constructor sets up values
     * @param s The starttime
     * @param n The date
     * @param e The endtime
     * @param i The id
     */
    public ObservationDate(String s, String n, String e, String i){
        start = s;
        name = n;
        end = e;
        id = i;
    }

    /**
     * Getter for the start field
     * @return The starttime
     */
    public String getStart(){
        return start;
    }

    /**
     * Getter for the name field
     * @return The date
     */
    public String getName(){
        return name;
    }

    /**
     * Getter for the end field
     * @return The endtime
     */
    public String getEnd(){
        return end;
    }

    /**
     * Getter for the id field
     * @return The id
     */
    public String getId(){
        return id;
    }

}
