/**
 *
 * @author Arthur J Amende
 * */
package Controllers;

import Classes.Appointment;
import Classes.AppointmentCalendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the All Appointments Form.
 * All Appointments Form includes a table of all Appointments.
 * */
public class AllAppointmentsController implements Initializable {
    private BorderPane bp;

    public TableView tableAppointments;
    public TableColumn colId;
    public TableColumn colTitle;
    public TableColumn colDesc;
    public TableColumn colLocation;
    public TableColumn colContact;
    public TableColumn colType;
    public TableColumn colStart;
    public TableColumn colEnd;
    public TableColumn colCustomerID;
    public TableColumn colUserId;
    public RadioButton radioAll;
    public RadioButton radioMonth;
    public RadioButton radioWeek;
    public ToggleGroup toggleAppointments;

    private ObservableList<Classes.Appointment> displayAppointments = FXCollections.observableArrayList();
    private AppointmentCalendar appointmentCalendar = null;

    /**
     * Initializes the All Appointments Form.
     * Appointments are fetched from the database by a new Appointment Calendar. Table View is initialized.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        appointmentCalendar = new AppointmentCalendar();

        displayAppointments = appointmentCalendar.getAllAppointments();
        tableAppointments.setItems(displayAppointments);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userID"));

        toggleAppointments.selectToggle(radioAll);
    }

    /**
     * Passes the Border Pane from the Master Form to control the display.
     * @param bp the Border Pane from the Master Form
     * */
    public void passControl(BorderPane bp){
        this.bp = bp;
    }

    /**
     * Transfers to the Appointment Form in Add Mode.
     * Triggered by the 'Add' button.
     * */
    public void toAddAppointment(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/Appointment.fxml"));
            Parent root = loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.passControl(bp);
            appointmentController.addMode();

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Add Appointment form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    /**
     * Transfers to the Appointment Form in Update Mode.
     * Triggered by the 'Update' button.
     * */
    public void toUpdateAppointment(ActionEvent actionEvent) {
        Appointment selectedAppointment = (Appointment) tableAppointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/Appointment.fxml"));
            Parent root = loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.passControl(bp);
            appointmentController.updateMode(selectedAppointment);

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Update Appointment form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    /**
     * Deletes Appointment from the database.
     * Triggered by the 'Delete' button.
     * If no Appointment is selected nothing happens.
     * */
    public void onDelete(ActionEvent actionEvent) {
        Appointment selectedAppointment = (Appointment) tableAppointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Appointment");
        alert.setHeaderText("ID: " + selectedAppointment.getId() + "\nTitle: " + selectedAppointment.getTitle() + "\nType: " + selectedAppointment.getType());
        alert.setContentText("Do you want to delete this appointment?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (appointmentCalendar.deleteAppointment(selectedAppointment)) {
                if (toggleAppointments.getSelectedToggle() == radioAll){
                    viewAll(actionEvent);
                } else if (toggleAppointments.getSelectedToggle() == radioMonth){
                    viewMonth(actionEvent);
                } else if (toggleAppointments.getSelectedToggle() == radioWeek){
                    viewWeek(actionEvent);
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Could not perform action.");
                alert.setContentText("Could not delete Appointment. Please check connection to database and restart program.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Displays all Appointments in the database.
     * Triggered by the 'All Appointments'(radioAll) radio button.
     * */
    public void viewAll(ActionEvent actionEvent) {
        displayAppointments = appointmentCalendar.getAllAppointments();
        tableAppointments.setItems(displayAppointments);
    }

    /**
     * Displays Appointments in the database scheduled this month.
     * Triggered by the 'Appointments this Month'(radioMonth) radio button.
     * */
    public void viewMonth(ActionEvent actionEvent) {
        displayAppointments.clear();

        ObservableList<Appointment> allAppointments = appointmentCalendar.getAllAppointments();
        Month thisMonth = LocalDate.now().getMonth();
        allAppointments.stream().filter(n -> n.getStartDateTimeLocal().getMonth() == thisMonth)
                                .forEach(n -> displayAppointments.add(n));
    }

    /**
     * Displays Appointments in the database scheduled this week.
     * Week is defined as Monday through Sunday.
     * Triggered by the 'Appointments this Week'(radioWeek) radio button.
     * */
    public void viewWeek(ActionEvent actionEvent) {
        displayAppointments.clear();
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        LocalDate monday = LocalDate.now().minusDays(dayOfWeek - 1);
        LocalDate sunday = monday.plusDays(6);

        for (Appointment appointment : appointmentCalendar.getAllAppointments()){
            LocalDate meetingStartDate = appointment.getStartDateTimeLocal().toLocalDate();
            LocalDate meetingEndDate = appointment.getEndDateTimeLocal().toLocalDate();
            if (meetingStartDate.compareTo(monday) * meetingStartDate.compareTo(sunday) <= 0){
                displayAppointments.add(appointment);
            } else if (meetingEndDate.compareTo(monday) * meetingEndDate.compareTo(sunday) <= 0){
                displayAppointments.add(appointment);
            }
        }
    }
}
