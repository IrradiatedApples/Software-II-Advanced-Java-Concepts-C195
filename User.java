/**
 *
 * @author Arthur J Amende
 * */
package Classes;

/** Extended from Individual Class, includes password.
 * */
public class User extends Individual{
    private String password;

    /**
     * Parametrized constructor.
     * @param id the ID number
     * @param name the name
     * @param password the password
     * */
    public User(int id, String name, String password){
        super(id, name);
        this.password = password;
    }

    /**
     * Copy constructor.
     * @param userToCopy the User to be copied.
     * */
    public User(User userToCopy){
        super(userToCopy.getId(),userToCopy.getName());
        this.password = userToCopy.password;
    }

    /** Dummy Constructor. Creates a User with ID of 0, name 'Dummy Name', and password 'Dummy Password'.*/
    public User(){
        super(0,"Dummy Name");
        password = "Dummy Password";
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
