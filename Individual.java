/**
 *
 * @author Arthur J Amende
 * */
package Classes;

/** Super Abstract Class for Customer, User, and Contact. Contains ID and Name.
 * */
public abstract class Individual {
    private int id;
    private String name;

    /**
     * Parametrized constructor.
     * @param id the ID number
     * @param name the name
     * */
    public Individual(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}