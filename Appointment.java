/**
 *
 * @author Arthur J Amende
 * */
package Classes;

import Helper.JDBC;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Fully defines one appointment.
 * */
public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int customerID;
    private int userID;
    private int contactID;

    /**
     * Parametrized constructor.
     * @param id the ID number
     * @param title the title
     * @param description the description
     * @param location the location
     * @param type the type
     * @param startDateTime the Start Time
     * @param endDateTime the End Time
     * @param customerID the Customer ID
     * @param userID the User ID
     * @param contactID the Contact ID
     * */
    public Appointment(int id, String title, String description, String location, String type, LocalDateTime startDateTime, LocalDateTime endDateTime, int customerID, int userID, int contactID){
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * Copy constructor.
     * @param appointmentToCopy the Appointment to be copied.
     * */
    public Appointment(Appointment appointmentToCopy){
        this.id = appointmentToCopy.id;
        this.title = appointmentToCopy.title;
        this.description = appointmentToCopy.description;
        this.location = appointmentToCopy.location;
        this.type = appointmentToCopy.type;
        this.startDateTime = appointmentToCopy.startDateTime;
        this.endDateTime = appointmentToCopy.endDateTime;
        this.customerID = appointmentToCopy.customerID;
        this.userID = appointmentToCopy.userID;
        this.contactID = appointmentToCopy.contactID;
    }

    /** Dummy Constructor. Creates an Appointment with all strings set to 'Dummy' + variable name, all integers set to 0, and times set to now().*/
    public Appointment(){
        this.id = 0;
        this.title = "Dummy Title";
        this.description = "Dummy Description";
        this.location = "Dummy Location";
        this.type = "Dummy Type";
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = LocalDateTime.now();
        this.customerID = 0;
        this.userID = 0;
        this.contactID = 0;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param startDateTimeZoned the startDateTime to set from a ZonedDateTime
     */
    public void setStartDateTime(ZonedDateTime startDateTimeZoned) {
        ZoneId localZoneId = ZoneId.systemDefault();
        LocalDateTime startDateTimeLocal = startDateTimeZoned.withZoneSameInstant(localZoneId).toLocalDateTime();
        setStartDateTime(startDateTimeLocal);
    }

    /**
     * @param startDateTimeLocal the startDateTime to set from a LocalDateTime
     */
    public void setStartDateTime(LocalDateTime startDateTimeLocal){
        startDateTime = startDateTimeLocal;
    }

    /**
     * @param endDateTimeZoned the endDateTime to set from a ZonedDateTime
     */
    public void setEndDateTime(ZonedDateTime endDateTimeZoned) {
        ZoneId localZoneId = ZoneId.systemDefault();
        LocalDateTime endDateTimeLocal = endDateTimeZoned.withZoneSameInstant(localZoneId).toLocalDateTime();
        setEndDateTime(endDateTimeLocal);
    }

    /**
     * @param endDateTimeLocal the endDateTime to set from a LocalDateTime
     */
    public void setEndDateTime(LocalDateTime endDateTimeLocal){
        endDateTime = endDateTimeLocal;
    }

    /**
     * @param customerID the customerID to set
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @param contactID the contactID to set
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the startDateTime in localized date time format, short
     */
    public String getStartDateTime() {
        return startDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }

    /**
     * @return the startDateTime
     */
    public LocalDateTime getStartDateTimeLocal(){
        return startDateTime;
    }

    /**
     * @return the startDateTime with system default zone
     */
    public ZonedDateTime getStartDateTimeZoned(){
        return startDateTime.atZone(ZoneId.systemDefault());
    }

    /**
     * @return the endDateTime
     */
    public String getEndDateTime() {
        return endDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }

    /**
     * @return the endDateTime in localized date time format, short
     */
    public LocalDateTime getEndDateTimeLocal(){
        return endDateTime;
    }

    /**
     * @return the endDateTime with system default zone
     */
    public ZonedDateTime getEndDateTimeZoned(){
        return endDateTime.atZone(ZonedDateTime.now().getZone());
    }

    /**
     * @return the customerID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @return the contactID
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @return the Contact Name
     */
    public String getContactName(){
        return JDBC.getContact(contactID).getName();
    }

    /**
     * Checks if this Appointment meeting times overlaps with another Appointment.
     * Returns true if meeting times overlap. Returns false if the do not overlap.
     * @param checkAppointment the Appointment to be checked against this Appointment
     */
    public boolean overlap(Appointment checkAppointment){
        if (this.id == checkAppointment.id) return false;
        if (this.userID != checkAppointment.getUserID()) return false;

        LocalDateTime newStartTime = checkAppointment.getStartDateTimeLocal();
        LocalDateTime newEndTime = checkAppointment.getEndDateTimeLocal();

        LocalDateTime thisStartTime = this.getStartDateTimeLocal();
        LocalDateTime thisEndTime = this.getEndDateTimeLocal();

        if (newStartTime.equals(thisStartTime) && newEndTime.equals(thisEndTime)) {
            return true;
        }
        if (newStartTime.compareTo(thisStartTime) * newStartTime.compareTo(thisEndTime) < 0){
            return true;
        }
        if (newEndTime.compareTo(thisStartTime) * newEndTime.compareTo(thisEndTime) < 0){
            return true;
        }
        if (thisStartTime.compareTo(newStartTime) * thisStartTime.compareTo(newEndTime) < 0){
            return true;
        }
        if (thisEndTime.compareTo(newStartTime) * thisEndTime.compareTo(newEndTime) < 0){
            return true;
        }
        return false;
    }
}
