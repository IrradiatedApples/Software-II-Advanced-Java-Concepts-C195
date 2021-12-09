/**
 *
 * @author Arthur J Amende
 * */
package Controllers;

import Classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

/**
 * Controller for the Appointment Form.
 * The Appointment Form adds new Appointments and updates existing Appointments.
 * */
public class AppointmentController implements Initializable {
    public Label labelError;
    public Label labelTimeError;
    private BorderPane bp;

    private enum MODE {ADD, UPDATE}
    private MODE mode;

    public TextField textID;
    public TextField textTitle;
    public TextField textDescription;
    public TextField textLocation;
    public TextField textType;
    public ComboBox<String> comboCustomer;
    public ComboBox<String> comboUser;
    public DatePicker dateStart;
    public ComboBox<String> comboStart;
    public DatePicker dateEnd;
    public ComboBox<String > comboEnd;
    public Label labelTitle;
    public ComboBox<String> comboContact;

    private Directory directory = null;
    private AppointmentCalendar appointmentCalendar = null;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    private final String delimiter = ": ";

    /**
     * Initializes the Appointment Form.
     * Appointments are fetched from the database by a new Appointment Calendar.
     * Customers, Users, Contacts, and Countries are fetched from the database by a new Directory.
     * <p>
     *     Lambda Expression is used to populate the Combo Boxes for Customers, Users, and Contacts.
     *     The code to do so is identical in each case with only the Individual (Customer, User, or Contact)
     *     list and combo box changing. I built a BiConsumer to add an Individual to a combo box. I then
     *     used the stream forEach on allCustomers, allUsers, and allContacts to populate the combo boxes.
     * </p>
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        directory = new Directory();
        appointmentCalendar = new AppointmentCalendar();

        //Lambda Expression
        BiConsumer<Individual, ComboBox<String>> comboAdder = (n, c) -> c.getItems().add(n.getId() + delimiter + n.getName());
        directory.getAllCustomers().forEach(n -> comboAdder.accept(n, comboCustomer));
        directory.getAllUsers().forEach(n -> comboAdder.accept(n, comboUser));
        directory.getAllContacts().forEach(n -> comboAdder.accept(n, comboContact));

        comboStart.getItems().addAll(getMeetingTimes());
        comboEnd.getItems().addAll(comboStart.getItems());

        labelError.setVisible(false);
        labelTimeError.setVisible(false);
    }

    /**
     * Passes the Border Pane from the Master Form to control the display.
     * @param bp the Border Pane from the Master Form
     * */
    public void passControl(BorderPane bp){
        this.bp = bp;
    }

    /**
     * Initializes the Appointment From in Add Mode.
     * Title is set to 'Add Appointment'. Dates a defaulted to the closest future hour or half hour to now.
     * */
    public void addMode(){
        mode = MODE.ADD;
        labelTitle.setText("Add Appointment");

        dateStart.setValue(LocalDate.now());
        dateEnd.setValue(LocalDate.now());

        int hour = LocalTime.now().getHour();
        LocalTime defaultStartTime;
        LocalTime defaultEndTime;
        if (LocalTime.now().getMinute() < 30) {
            defaultStartTime  = LocalTime.of(hour, 30);
            ++hour;
            defaultEndTime = LocalTime.of(hour, 0);
        } else {
            ++hour;
            defaultStartTime  = LocalTime.of(hour, 0);
            defaultEndTime = LocalTime.of(hour, 30);
        }

        comboStart.setValue(defaultStartTime.format(timeFormatter));
        comboEnd.setValue(defaultEndTime.format(timeFormatter));
    }

    /**
     * Initializes the Appointment From in Update Mode.
     * Title is set to 'Update Appointment'.
     * All fields are set to the values of updateAppointment.
     * @param updateAppointment the Appointment to be updated
     * */
    public void updateMode(Appointment updateAppointment){
        mode = MODE.UPDATE;

        labelTitle.setText("Update Appointment");

        textID.setText(String.valueOf(updateAppointment.getId()));
        textTitle.setText(updateAppointment.getTitle());
        textDescription.setText(updateAppointment.getDescription());
        textLocation.setText(updateAppointment.getLocation());
        textType.setText(updateAppointment.getType());

        dateStart.setValue(updateAppointment.getStartDateTimeLocal().toLocalDate());
        dateEnd.setValue(updateAppointment.getEndDateTimeLocal().toLocalDate());

        comboStart.setValue(updateAppointment.getStartDateTimeLocal().toLocalTime().format(timeFormatter));
        comboEnd.setValue(updateAppointment.getEndDateTimeLocal().toLocalTime().format(timeFormatter));

        Customer customer = directory.getCustomer(updateAppointment.getCustomerID());
        User user = directory.getUser(updateAppointment.getUserID());
        Contact contact = directory.getContact(updateAppointment.getContactID());

        comboCustomer.setValue(customer.getId() + delimiter + customer.getName());
        comboUser.setValue(user.getId() + delimiter + user.getName());
        comboContact.setValue(contact.getId() + delimiter + contact.getName());
    }

    /**
     * Saves the Appointment.
     * <p>
     * In Add Mode a new Appointment is generated and added to the database.
     * In Update Mode the Appointment is updated in the database.
     * Error checking is performed to ensure all fields have data.
     * Appointment time must be within business hours, Monday through Friday, 8:00 a.m. to 10:00 p.m. Eastern Time.
     * Appointments with the same User can not have overlapping times.
     * Any exceptions found stops the save.
     * Triggered when the 'Save' button is selected.
     * </p>
     * <p>
     *     Lambda Expression is used to determine if appointments will overlap.
     *     The stream forEach is used to cycle through each existing appointment.
     *     A Lambda is then used to determine if they will overlap with the New Appointment.
     *     If they will overlap the appointment is added to a list of overlapping Appointments.
     * </p>
     * */
    public void onSave(ActionEvent actionEvent) {
        String title = textTitle.getText();
        String description = textDescription.getText();
        String location = textLocation.getText();
        String type = textType.getText();

        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = dateEnd.getValue();
        LocalTime startTime = LocalTime.parse(comboStart.getValue(), timeFormatter);
        LocalTime endTime = LocalTime.parse(comboEnd.getValue(), timeFormatter);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        boolean errorCheck = false;
        //All text fields must have input
        if (title.isEmpty()){
            errorCheck = true;
            textTitle.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            textTitle.setStyle(null);
        }
        if (description.isEmpty()){
            errorCheck = true;
            textDescription.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            textDescription.setStyle(null);
        }
        if (location.isEmpty()){
            errorCheck = true;
            textLocation.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            textLocation.setStyle(null);
        }
        if (type.isEmpty()){
            errorCheck = true;
            textType.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            textType.setStyle(null);
        }

        //End time must be after Start time
        if (endDateTime.compareTo(startDateTime) <= 0){
            errorCheck = true;
            dateStart.setStyle("-fx-border-color: rgb(255, 0, 0);");
            dateEnd.setStyle("-fx-border-color: rgb(255, 0, 0);");
            comboStart.setStyle("-fx-border-color: rgb(255, 0, 0);");
            comboEnd.setStyle("-fx-border-color: rgb(255, 0, 0);");
            labelTimeError.setVisible(true);
        } else {
            dateStart.setStyle(null);
            dateEnd.setStyle(null);
            comboStart.setStyle(null);
            comboEnd.setStyle(null);
            labelTimeError.setVisible(false);
        }

        //Check if appointment in business hours
        if (!AppointmentCalendar.inBusinessHours(startDateTime,endDateTime)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointment Time");
            alert.setHeaderText("All appointment times must be within business hours of " +
                    "\nMonday through Friday, 8:00 a.m. to 10:00 p.m. Eastern Time.\n" +
                    "\nEntered Start Time: " + AppointmentCalendar.toHQZoneID(startDateTime).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)) +
                    "\nEntered End Time:   " + AppointmentCalendar.toHQZoneID(endDateTime).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)));
            alert.showAndWait();
            dateStart.setStyle("-fx-border-color: rgb(255, 0, 0);");
            dateEnd.setStyle("-fx-border-color: rgb(255, 0, 0);");
            comboStart.setStyle("-fx-border-color: rgb(255, 0, 0);");
            comboEnd.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            dateStart.setStyle(null);
            dateEnd.setStyle(null);
            comboStart.setStyle(null);
            comboEnd.setStyle(null);
        }

        //Begin setting up New Appointment
        Appointment newAppointment = new Appointment();

        newAppointment.setTitle(title);
        newAppointment.setDescription(description);
        newAppointment.setLocation(location);
        newAppointment.setType(type);

        newAppointment.setStartDateTime(startDateTime);
        newAppointment.setEndDateTime(endDateTime);

        //All Combo Boxes must have selection
        if (comboCustomer.getValue() == null){
            errorCheck = true;
            comboCustomer.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            comboCustomer.setStyle(null);
        }

        if (comboUser.getValue() == null){
            errorCheck = true;
            comboUser.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            comboUser.setStyle(null);

            String userID = comboUser.getValue().split(delimiter)[0];
            newAppointment.setUserID(Integer.parseInt(userID));

            if (mode == MODE.UPDATE) newAppointment.setId(Integer.parseInt(textID.getText()));

            //Overlapping Appointments
            ObservableList<Appointment> overlappingAppointments = FXCollections.observableArrayList();
            //Lambda Expression
            appointmentCalendar.getAllAppointments().forEach((n) -> {if (n.overlap(newAppointment)) overlappingAppointments.add(n);});
            if (overlappingAppointments.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Overlap");
                StringBuilder headerText = new StringBuilder("Appointment overlaps with existing appointment(s): ");
                for (Appointment appointment : overlappingAppointments){
                    headerText.append("\n\nAppointment ID: ").append(appointment.getId())
                              .append("\nStart Time: ").append(appointment.getStartDateTime())
                              .append("\nEnd Time:   ").append(appointment.getEndDateTime());
                }
                alert.setHeaderText(headerText.toString());
                dateStart.setStyle("-fx-border-color: rgb(255, 0, 0);");
                dateEnd.setStyle("-fx-border-color: rgb(255, 0, 0);");
                comboStart.setStyle("-fx-border-color: rgb(255, 0, 0);");
                comboEnd.setStyle("-fx-border-color: rgb(255, 0, 0);");
                alert.showAndWait();
                errorCheck = true;
            } else {
                dateStart.setStyle(null);
                dateEnd.setStyle(null);
                comboStart.setStyle(null);
                comboEnd.setStyle(null);
            }
        }

        if (comboContact.getValue() == null){
            errorCheck = true;
            comboContact.setStyle("-fx-border-color: rgb(255, 0, 0);");
        } else {
            comboContact.setStyle(null);
        }

        if (!errorCheck) {
            String customerID = comboCustomer.getValue().split(delimiter)[0];
            String contactID = comboContact.getValue().split(delimiter)[0];

            newAppointment.setCustomerID(Integer.parseInt(customerID));
            newAppointment.setContactID(Integer.parseInt(contactID));

            labelError.setVisible(false);

            if (mode == MODE.ADD){
                if (!appointmentCalendar.addAppointment(newAppointment)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add Failed.");
                    alert.setContentText("Could not add appointment. Please check connection to database and restart program.");
                    alert.showAndWait();
                }
            } else if (mode == MODE.UPDATE){
                newAppointment.setId(Integer.parseInt(textID.getText()));
                if (!appointmentCalendar.updateAppointment(newAppointment)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update Failed.");
                    alert.setContentText("Could not update appointment. Please check connection to database and restart program.");
                    alert.showAndWait();
                }
            }
            toAllAppointments(actionEvent);
        } else {
            labelError.setVisible(true);
        }
    }

    /**
     * Transfers back to the All Appointments From.
     * Triggered when the 'Cancel' button is selected.
     * */
    public void onCancel(ActionEvent actionEvent) {
        toAllAppointments(actionEvent);
    }

    /**
     * Transfers back to the All Appointments From.
     * */
    private void toAllAppointments(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/AllAppointments.fxml"));
            Parent root = loader.load();

            AllAppointmentsController appointmentsController = loader.getController();
            appointmentsController.passControl(bp);

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Appointments form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    private ObservableList<String> getMeetingTimes(){
        ObservableList<String> meetingTimes = FXCollections.observableArrayList();

        LocalTime midnight = LocalTime.of(0,0);
        meetingTimes.add(midnight.format(timeFormatter));

        int minutes = 15;
        LocalTime meetingTime = midnight.plusMinutes(minutes);
        while (!meetingTime.equals(midnight)){
            meetingTimes.add(meetingTime.format(timeFormatter));
            meetingTime = meetingTime.plusMinutes(minutes);
        }

        return meetingTimes;
    }
}
