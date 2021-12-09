/**
 *
 * @author Arthur J Amende
 * */
package Classes;

import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** Directory of all Customers, Users, Contacts, and Countries.
 * */
public class Directory {
    private ObservableList<Customer> allCustomers;
    private ObservableList<User> allUsers;
    private ObservableList<Contact> allContacts;

    private ObservableList<Country> allCountries;

    /** Constructor getting all Customers, Users, Contacts, and Countries from the database. */
    public Directory(){
        allCustomers = JDBC.getAllCustomers();
        allUsers = JDBC.getAllUsers();
        allContacts = JDBC.getAllContacts();
        allCountries = JDBC.getAllCountries();
    }

    /**
     * @param newCustomer Customer added to the database
     * @return true if added, false if not added
     * */
    public boolean addCustomer(Customer newCustomer){
        return JDBC.addCustomer(newCustomer);
    }

    /**
     * @param updateCustomer Customer updated in the database
     * @return true if updated, false if not updated
     * */
    public boolean updateCustomer(Customer updateCustomer){
        return JDBC.updateCustomer(updateCustomer);
    }

    /**
     * @param deleteCustomer Customer to be deleted from the database
     * @return true if deleted, false if not deleted
     * */
    public boolean deleteCustomer(Customer deleteCustomer){
        if (JDBC.deleteCustomer(deleteCustomer.getId())){
            allCustomers = JDBC.getAllCustomers();
            return true;
        }
        return false;
    }

    /**
     * @param customerID ID number of Customer
     * @return Customer with matching ID number, if no Customer found returns null
     * */
    public Customer getCustomer(int customerID){
        for (Customer customer : allCustomers){
            if (customer.getId() == customerID){
                return new Customer(customer);
            }
        }
        return null;
    }

    /**
     * @param userID ID number of User
     * @return User with matching ID number, if no User found returns null
     * */
    public User getUser(int userID){
        for (User user : allUsers){
            if (user.getId() == userID){
                return new User(user);
            }
        }
        return null;
    }

    /**
     * @param contactID ID number of Contact
     * @return Contact with matching ID number, if no Contact found returns null
     * */
    public Contact getContact(int contactID){
        for (Contact contact : allContacts){
            if (contact.getId() == contactID){
                return new Contact(contact);
            }
        }
        return null;
    }

    /**
     * @return all Customers in Directory
     * */
    public ObservableList<Customer> getAllCustomers(){
        return FXCollections.observableArrayList(allCustomers);
    }

    /**
     * @return all Users in Directory
     * */
    public ObservableList<User> getAllUsers(){
        return FXCollections.observableArrayList(allUsers);
    }

    /**
     * @return all Contacts in Directory
     * */
    public ObservableList<Contact> getAllContacts(){
        return FXCollections.observableArrayList(allContacts);
    }

    /**
     * @return all Countries in Directory
     * */
    public ObservableList<Country> getAllCountries(){
        return FXCollections.observableArrayList(allCountries);
    }
}
