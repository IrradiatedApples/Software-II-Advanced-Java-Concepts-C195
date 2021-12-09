package Helper;

import Classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Abstract Class for accessing SQL database.
 * */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static final String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface

    private static final DateTimeFormatter zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static User loginUser;

    /**
     * Opens connection to the database.
     * */
    public static void openConnection(){
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if user input User ID and Password match user in the database.
     * Only User 1 is valid.
     * Outputs user provided User ID, Date, Time, and if login was successful to login_activity.txt.
     * @return true if input matches User 1, otherwise false.
     * */
    public static boolean login(String loginUserName, String loginPassword){
        int userID = 1;
        loginUser = getUser(userID);

        boolean loggedIn = loginUserName.equals(loginUser.getName()) && loginPassword.equals(loginUser.getPassword());

        try {
            File loginActivity = new File("login_activity.txt");
            if (loginActivity.createNewFile()){
                FileWriter fw = new FileWriter(loginActivity);
                fw.write("USER\tDATE\tTIME\tLOGIN\n");
                fw.close();
            }

            FileWriter fw = new FileWriter(loginActivity, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            String name;
            if (loginUserName.isEmpty()){
                name = "NO_USERID";
            } else {
                name = loginUserName;
            }
            ZonedDateTime nowDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
            String date = nowDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = nowDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss z"));

            pw.println(name + "\t" + date + "\t" + time + "\t" + loggedIn);

            pw.flush();
            pw.close();
            bw.close();
            fw.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return loggedIn;
    }

    public static void closeConnection(){
        try {
            connection.close();
        }
        catch(Exception e) {
//            e.printStackTrace();
        }
    }

    /**
     * @return logged in User.
     * */
    public static User getLoginUser(){
        return new User(loginUser);
    }

    public static ObservableList<Customer> getAllCustomers(){
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers");
            while(resultSet.next()){
                Customer nextCustomer = new Customer();
                nextCustomer.setId(resultSet.getInt("Customer_ID"));
                nextCustomer.setName(resultSet.getString("Customer_Name"));
                nextCustomer.setAddress(resultSet.getString("Address"));
                nextCustomer.setPostalCode(resultSet.getString("Postal_Code"));
                nextCustomer.setPhoneNum(resultSet.getString("Phone"));
                nextCustomer.setDivisionID(resultSet.getInt("Division_ID"));
                allCustomers.add(nextCustomer);
            }
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return allCustomers;
    }

    /**
     * @return all Users from database.
     * */
    public static ObservableList<User> getAllUsers(){
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT User_ID, User_Name, Password FROM users");
            while(resultSet.next()){
                User nextUser = new User();
                nextUser.setId(resultSet.getInt("User_ID"));
                nextUser.setName(resultSet.getString("User_Name"));
                nextUser.setPassword(resultSet.getString("Password"));
                allUsers.add(nextUser);
            }
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return allUsers;
    }

    /**
     * @param userID Primary Key in User Table
     * @return User with matching Primary Key.
     * */
    public static User getUser(int userID){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT User_ID, User_Name, Password FROM users WHERE User_ID = " + userID);
            resultSet.next();

            User user = new User();
            user.setId(resultSet.getInt("User_ID"));
            user.setName(resultSet.getString("User_Name"));
            user.setPassword(resultSet.getString("Password"));

            return user;
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return all Contacts from database.
     * */
    public static ObservableList<Contact> getAllContacts(){
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Contact_ID, Contact_Name, Email FROM contacts");
            while(resultSet.next()){
                Contact nextContact = new Contact();
                nextContact.setId(resultSet.getInt("Contact_ID"));
                nextContact.setName(resultSet.getString("Contact_Name"));
                nextContact.setEmail(resultSet.getString("Email"));
                allContacts.add(nextContact);
            }
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return allContacts;
    }

    /**
     * @param contactID Primary Key in Contact Table
     * @return Contact with matching Primary Key.
     * */
    public static Contact getContact(int contactID){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Contact_ID, Contact_Name, Email FROM contacts WHERE Contact_ID = " + contactID);
            resultSet.next();

            Contact contact = new Contact();
            contact.setId(resultSet.getInt("Contact_ID"));
            contact.setName(resultSet.getString("Contact_Name"));
            contact.setEmail(resultSet.getString("Email"));

            return contact;
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return all Countries from database
     * */
    public static ObservableList<Country> getAllCountries(){
        ObservableList<Country> allCountries = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Country_ID, Country FROM countries");
            while(resultSet.next()){
                Country nextCountry = new Country();
                nextCountry.setId(resultSet.getInt("Country_ID"));
                nextCountry.setName(resultSet.getString("Country"));
                nextCountry.setAllDivisions(getAllDivisions(nextCountry.getId()));
                allCountries.add(nextCountry);
            }
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return allCountries;
    }

    /**
     * @param countryID Foreign Key in Divisions Table
     * @return all divisions from database matching Foreign Key
     * */
    public static ObservableList<Division> getAllDivisions(int countryID){
        ObservableList<Division> allDivisions = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Division_ID, Division FROM first_level_divisions WHERE Country_ID = " + countryID);
            while(resultSet.next()){
                Division nextDivision = new Division();
                nextDivision.setId(resultSet.getInt("Division_ID"));
                nextDivision.setName(resultSet.getString("Division"));
                allDivisions.add(nextDivision);
            }
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return allDivisions;
    }

    /**
     * @param divisionID Primary Key in Division Table
     * @return all divisions from database matching Foreign Key, countryID
     * */
    public static String getDivisionName(int divisionID){
        String divisionName = "Dummy Division";
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Division FROM first_level_divisions WHERE Division_ID = " + divisionID);
            resultSet.next();
            divisionName = resultSet.getString("Division");
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return divisionName;
    }

    /**
     * @param divisionID Primary Key in Division Table
     * @return Country name of division
     * */
    public static String getCountryName(int divisionID){
        String countryName = "Dummy Country";
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Country FROM countries WHERE Country_ID = (SELECT Country_ID FROM first_level_divisions WHERE Division_ID = " + divisionID + ")");
            resultSet.next();
            countryName = resultSet.getString("Country");
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return countryName;
    }

    /**
     * @return all Appointments from database
     * */
    public static ObservableList<Appointment> getAllAppointments(){
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM appointments");
            while(resultSet.next()){
                Appointment nextAppointment = new Appointment();
                nextAppointment.setId(resultSet.getInt("Appointment_ID"));
                nextAppointment.setTitle(resultSet.getString("Title"));
                nextAppointment.setDescription(resultSet.getString("Description"));
                nextAppointment.setLocation(resultSet.getString("Location"));
                nextAppointment.setType(resultSet.getString("Type"));

                String startDateTime = resultSet.getString("Start") + " UTC";
                String endDateTime = resultSet.getString("End") + " UTC";
                ZonedDateTime startZonedDateTime = ZonedDateTime.parse(startDateTime, zonedDateTimeFormatter);
                ZonedDateTime endZonedDateTime = ZonedDateTime.parse(endDateTime, zonedDateTimeFormatter);
                nextAppointment.setStartDateTime(startZonedDateTime);
                nextAppointment.setEndDateTime(endZonedDateTime);

                nextAppointment.setCustomerID(resultSet.getInt("Customer_ID"));
                nextAppointment.setUserID(resultSet.getInt("User_ID"));
                nextAppointment.setContactID(resultSet.getInt("Contact_ID"));
                allAppointments.add(nextAppointment);
            }
        } catch (SQLException e){
//            e.printStackTrace();
        }
        return allAppointments;
    }

    /**
     * @param newCustomer Customer to be added to database
     * @return true if successfully added to database, otherwise return false.
     * */
    public static boolean addCustomer(Customer newCustomer){
        try{
            Statement statement = connection.createStatement();
            String name = newCustomer.getName();
            String address = newCustomer.getAddress();
            String postalCode = newCustomer.getPostalCode();
            String phoneNum = newCustomer.getPhoneNum();
            String user = loginUser.getName();
            int divisionID = newCustomer.getDivisionID();
            statement.execute("INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                    "VALUES('"+name+"', '"+address+"', '"+postalCode+"', '"+phoneNum+"', NOW(), '"+user+"', NOW(), '"+user+"', "+divisionID+")");
        } catch (SQLException e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param updateCustomer Customer to be updated to database
     * @return true if successfully update in database, otherwise return false.
     * */
    public static boolean updateCustomer(Customer updateCustomer){
        try{
            Statement statement = connection.createStatement();
            String name = updateCustomer.getName();
            String address = updateCustomer.getAddress();
            String postalCode = updateCustomer.getPostalCode();
            String phoneNum = updateCustomer.getPhoneNum();
            String user = loginUser.getName();
            int divisionID = updateCustomer.getDivisionID();
            int id = updateCustomer.getId();
            statement.execute("UPDATE customers SET " +
                    "Customer_Name = '" + name + "', " +
                    "Address = '" + address + "', " +
                    "Postal_Code = '" + postalCode + "', " +
                    "Phone = '" + phoneNum + "', " +
                    "Last_Update = NOW(), " +
                    "Last_Updated_By = '" + user + "', " +
                    "Division_ID = " + divisionID + " " +
                    "WHERE Customer_ID = " + id);
        } catch (SQLException e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param customerID Customer to be deleted from database
     * @return true if successfully deleted from database, otherwise return false.
     * */
    public static boolean deleteCustomer(int customerID){
        try{
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM appointments WHERE Customer_ID = " + customerID);
            statement.execute("DELETE FROM customers WHERE Customer_ID = " + customerID);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param newAppointment Appointment to be added to database
     * @return true if successfully added to database, otherwise return false.
     * */
    public static boolean addAppointment(Appointment newAppointment){
        try{
            Statement statement = connection.createStatement();
            String title = newAppointment.getTitle();
            String description = newAppointment.getDescription();
            String location = newAppointment.getLocation();
            String type = newAppointment.getType();

            ZonedDateTime startZoned = newAppointment.getStartDateTimeZoned();
            ZonedDateTime endZoned = newAppointment.getEndDateTimeZoned();
            startZoned = startZoned.withZoneSameInstant(ZoneId.of("UTC"));
            endZoned = endZoned.withZoneSameInstant(ZoneId.of("UTC"));
            String start = startZoned.toLocalDateTime().format(localDateTimeFormatter);
            String end = endZoned.toLocalDateTime().format(localDateTimeFormatter);

            String user = loginUser.getName();
            int customerID = newAppointment.getCustomerID();
            int userID = newAppointment.getUserID();
            int contactID = newAppointment.getContactID();
            statement.execute("INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                    "VALUES ('"+title+"', '"+description+"', '"+location+"', '"+type+"', '"+start+"', '"+end+"', NOW(), '"+user+"', NOW(), '"+user+"', "+customerID+", "+userID+", "+contactID+")");
        } catch (SQLException e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param updateAppointment Appointment to be updated in database
     * @return true if successfully updated in database, otherwise return false.
     * */
    public static boolean updateAppointment(Appointment updateAppointment){
        try{
            Statement statement = connection.createStatement();
            String title = updateAppointment.getTitle();
            String description = updateAppointment.getDescription();
            String location = updateAppointment.getLocation();
            String type = updateAppointment.getType();

            ZonedDateTime startZoned = updateAppointment.getStartDateTimeZoned();
            ZonedDateTime endZoned = updateAppointment.getEndDateTimeZoned();
            startZoned = startZoned.withZoneSameInstant(ZoneId.of("UTC"));
            endZoned = endZoned.withZoneSameInstant(ZoneId.of("UTC"));
            String start = startZoned.toLocalDateTime().format(localDateTimeFormatter);
            String end = endZoned.toLocalDateTime().format(localDateTimeFormatter);

            String user = loginUser.getName();
            int customerID = updateAppointment.getCustomerID();
            int contactID = updateAppointment.getContactID();
            int userID = updateAppointment.getUserID();
            int appointmentID = updateAppointment.getId();
            statement.execute("UPDATE appointments SET " +
                    "Title = '"+title+"', " +
                    "Description = '"+description+"', " +
                    "Location = '"+location+"', " +
                    "Type = '"+type+"', " +
                    "Start = '"+start+"', " +
                    "End = '"+end+"', " +
                    "Last_Update = NOW(), " +
                    "Last_Updated_By = '"+user+"', " +
                    "Customer_ID = "+customerID+", " +
                    "User_ID = "+userID+", " +
                    "Contact_ID = "+contactID+" " +
                    "WHERE Appointment_ID = "+appointmentID);
        } catch (SQLException e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param appointmentID Appointment to be deleted from database
     * @return true if successfully deleted from database, otherwise return false.
     * */
    public static boolean deleteAppointment(int appointmentID){
        try{
            Statement statement = connection.createStatement();
            statement.execute(" DELETE FROM appointments WHERE Appointment_ID = " + appointmentID);
        } catch (SQLException e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }
}
