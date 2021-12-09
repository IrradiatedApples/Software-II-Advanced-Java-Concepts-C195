/**
 *
 * @author Arthur J Amende
 * */
package Controllers;

import Classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

/**
 * Controller for the Reports Form.
 * Report One, number of appointments by month.
 * Report Two, appointments for selected contact.
 * Report Three, total appointment hours per customer.
 * */
public class ReportsController implements Initializable {
    public TableColumn colMonth;
    public TableColumn colType;
    public TableColumn colCount;
    public TableView tableReportOne;
    public TableColumn colID;
    public TableColumn colTitle;
    public TableColumn colDescription;
    public TableColumn colTypeTwo;
    public TableColumn colStart;
    public TableColumn colEnd;
    public TableColumn colCustomerID;
    public TableView tableReportTwo;
    public ComboBox comboContact;
    public TableView tableReportThree;
    public TableColumn colCustomer;
    public TableColumn colHours;

    private ObservableList<ReportOne> allReportOne = FXCollections.observableArrayList();
    private ObservableList<Appointment> displayAppointments = FXCollections.observableArrayList();
    private ObservableList<ReportThree> allReportThree = FXCollections.observableArrayList();

    private AppointmentCalendar appointmentCalendar = null;
    private Directory directory = null;

    private final String delimiter = ": ";

    /**
     * Initializes the Reports Form.
     * Appointments are fetched from the database by a new Appointment Calendar.
     * Customers, Users, Contacts, and Countries are fetched from the database by a new Directory.
     * Table Views are initialized.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        appointmentCalendar = new AppointmentCalendar();
        directory = new Directory();

        //Report One
        populateReportOne();
        tableReportOne.setItems(allReportOne);

        colMonth.setCellValueFactory(new PropertyValueFactory<>("monthYear"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("count"));

        //Report Two
        tableReportTwo.setItems(displayAppointments);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTypeTwo.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        //Lambda Expression
        BiConsumer<Individual, ComboBox<String>> comboAdder = (n, c) -> c.getItems().add(n.getId() + delimiter + n.getName());
        directory.getAllContacts().forEach(n -> comboAdder.accept(n, comboContact));

        //Report Three
        getAllReportThree();
        tableReportThree.setItems(allReportThree);

        colCustomer.setCellValueFactory(new PropertyValueFactory<>("name"));
        colHours.setCellValueFactory(new PropertyValueFactory<>("hours"));

        tableReportThree.getSortOrder().add(colCustomer);
        tableReportThree.getSortOrder().clear();
    }

    /**
     * Populates table data for Report One
     * */
    public void populateReportOne(){
        ObservableList<Appointment> allAppointments = appointmentCalendar.getAllAppointments();

        Appointment appointment = allAppointments.get(0);
        int month = appointment.getStartDateTimeLocal().getMonth().getValue();
        int year = appointment.getStartDateTimeLocal().getYear();
        String type = appointment.getType();
        allReportOne.add(new ReportOne(month,year,type));

        for (int i = 1; i < allAppointments.size(); ++i){
            appointment = allAppointments.get(i);
            month = appointment.getStartDateTimeLocal().getMonth().getValue();
            year = appointment.getStartDateTimeLocal().getYear();
            type = appointment.getType();

            boolean counted = false;
            for (ReportOne reportOne : allReportOne){
                if (reportOne.getMonth() == month && reportOne.getYear() == year && reportOne.getType().equals(type)) {
                    reportOne.incrementCount();
                    counted = true;
                    break;
                }
            }
            if (!counted){
                allReportOne.add(new ReportOne(month,year,type));
            }
        }
    }

    /**
     * Populates table data for Report Two based on selected Contact.
     * */
    public void onSelectContact(ActionEvent actionEvent) {
        int contactID = Integer.parseInt(comboContact.getValue().toString().split(delimiter)[0]);
        displayAppointments.clear();
        //Lambda
        appointmentCalendar.getAllAppointments().forEach((n) -> {if (n.getContactID() == contactID) displayAppointments.add(n);});
    }

    /**
     * Populates table data for Report Three
     * */
    public void getAllReportThree(){
        ObservableList<Appointment> allAppointments = appointmentCalendar.getAllAppointments();

        Appointment appointment = allAppointments.get(0);
        LocalDateTime start = appointment.getStartDateTimeLocal();
        LocalDateTime end = appointment.getEndDateTimeLocal();

        int customerID = appointment.getCustomerID();
        String name = directory.getCustomer(customerID).getName();
        double hours = Duration.between(start,end).toMinutes() / 60.0;

        allReportThree.add(new ReportThree(customerID, name, hours));
        for (int i = 1; i < allAppointments.size(); ++i){
            appointment = allAppointments.get(i);
            start = appointment.getStartDateTimeLocal();
            end = appointment.getEndDateTimeLocal();

            customerID = appointment.getCustomerID();
            hours = Duration.between(start,end).toMinutes() / 60.0;

            boolean counted = false;
            for (ReportThree reportThree : allReportThree){
                if (reportThree.getId() == customerID) {
                    reportThree.addHours(hours);
                    counted = true;
                    break;
                }
            }
            if (!counted){
                name = directory.getCustomer(customerID).getName();
                allReportThree.add(new ReportThree(customerID, name, hours));
            }
        }

        for (Customer customer : directory.getAllCustomers()){
            boolean counted = false;
            for (ReportThree reportThree : allReportThree){
                if (customer.getId() == reportThree.getId()){
                    counted = true;
                    break;
                }
            }
            if (!counted) {
                allReportThree.add(new ReportThree(customer.getId(), customer.getName(), 0));
            }
        }
    }
}
