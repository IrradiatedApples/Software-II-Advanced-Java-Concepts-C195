/**
 *
 * @author Arthur J Amende
 * */
package Classes;

import Helper.JDBC;

/** Extended from Individual Class, includes Address, Postal Code, Phone Number, and country division.
 * */
public class Customer extends Individual{
    private String address;
    private String postalCode;
    private String phoneNum;
    private int divisionID;

    /**
     * Parametrized constructor.
     * @param id the ID number
     * @param name the name
     * @param address the address
     * @param postalCode the postalCode
     * @param phoneNum the phoneNum
     * @param divisionID the divisionID
     * */
    public Customer(int id, String name, String address, String postalCode, String phoneNum, int divisionID){
        super(id, name);
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;
        this.divisionID = divisionID;
    }

    /**
     * Copy constructor.
     * @param customerToCopy the Contact to be copied.
     * */
    public Customer(Customer customerToCopy){
        super(customerToCopy.getId(), customerToCopy.getName());
        this.address = customerToCopy.address;
        this.postalCode = customerToCopy.postalCode;
        this.phoneNum = customerToCopy.phoneNum;
        this.divisionID = customerToCopy.divisionID;
    }

    /** Dummy Constructor.
     * Creates a Contact with ID of 0, name 'Dummy Name', address "Dummy Address"
     * postal code '99999', phone number '999-999-9999', and division ID 0.
     * */
    public Customer(){
        super(0,"Dummy Name");
        address = "Dummy Address";
        postalCode = "99999";
        phoneNum = "999-999-9999";
        divisionID = 0;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address){
        this.address = address;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }

    /**
     * @param phoneNum the phoneNum to set
     */
    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }

    /**
     * @param divisionID the divisionID to set
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * @return the address
     */
    public String getAddress(){
        return address;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode(){
        return postalCode;
    }

    /**
     * @return the phoneNum
     */
    public String getPhoneNum(){
        return phoneNum;
    }

    /**
     * @return the divisionID
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @return the divisionID
     */
    public String getDivision(){
        return JDBC.getDivisionName(divisionID);
    }

    /**
     * @return the divisionID
     */
    public String getCountry(){
        return JDBC.getCountryName(divisionID);
    }
}
