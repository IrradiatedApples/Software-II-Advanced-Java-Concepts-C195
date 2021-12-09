/**
 *
 * @author Arthur J Amende
 * */
package Controllers;

import Classes.Appointment;
import Classes.AppointmentCalendar;
import Classes.User;
import Helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.im.InputContext;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the Master Form.
 * Master Form contains navigation buttons and Border Pane used
 * to display all other forms.
 * */
public class MasterController implements Initializable {
    public BorderPane bp;
    private ResourceBundle lb;
    static boolean loggedIn;

    public Button buttonCustomers;
    public Button buttonAppointments;
    public Button buttonReports;
    public TextField textUserID;
    public PasswordField textPassword;
    public Label labelLocation;
    public Label labelUserID;
    public Label labelPassword;
    public Button buttonLogin;
    public Button buttonExit;

    /**
     * Initializes the Master Form.
     * Connection to the database is opened.
     * Navigation buttons are disabled.
     * Language is determined from system setting.
     * Login page is set to either English or French.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        JDBC.openConnection();
        buttonCustomers.setDisable(true);
        buttonAppointments.setDisable(true);
        buttonReports.setDisable(true);
        loggedIn = false;

        //System Language
        Locale locale = Locale.getDefault();

        //Keyboard Language
//        Locale locale = InputContext.getInstance().getLocale();

        lb = ResourceBundle.getBundle("LanguageBundle_" + locale.getLanguage());
        labelUserID.setText(lb.getString("userID"));
        labelPassword.setText(lb.getString("password"));
        buttonLogin.setText(lb.getString("login"));
        labelLocation.setText(lb.getString("location") + ZoneId.systemDefault());
        buttonExit.setText(lb.getString("exit"));
    }

    /**
     * Takes the input User ID and Password and checks the database for a match.
     * If login fails a popup is launched in either English or French.
     * */
    public void onLogin(ActionEvent actionEvent) {
        if(JDBC.login(textUserID.getText(), textPassword.getText())){
            buttonCustomers.setDisable(false);
            buttonAppointments.setDisable(false);
            buttonReports.setDisable(false);
            showReminders();
            goToAppointments(actionEvent);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(lb.getString("error"));
            alert.setTitle(lb.getString("errorTitle"));
            alert.setContentText(lb.getString("errorText"));
            alert.showAndWait();
        }
    }

    /**
     * Allows user to attempt login by hitting 'Enter' key.
     * */
    public void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            onLogin(new ActionEvent());
        }
    }

    /**
     * Determines if the logged in User has an appointment in the next 15 minutes.
     * If an appointment is found a popup informs the User of the appointment.
     * If no appointment is found a popup informs the User of no upcoming appointments.
     * */
    public void showReminders() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        Appointment nextAppointment = null;
        User loginUser = JDBC.getLoginUser();

        LocalDateTime timeNow = LocalDateTime.now();
        for (Appointment appointment : appointmentCalendar.getAllAppointments()){
            LocalDateTime startDateTime = appointment.getStartDateTimeLocal();
            if (timeNow.toLocalDate().equals(startDateTime.toLocalDate())) {
                if (appointment.getUserID() == loginUser.getId()) {
                    long timeToAppointment = Duration.between(timeNow, startDateTime).toMinutes();
                    if (timeToAppointment <= 15 && timeToAppointment >= 0) {
                        nextAppointment = appointment;
                        break;
                    }
                }
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Appointment");
        if (nextAppointment != null) {
            alert.setHeaderText("You have an appointment within the next 15 minutes.");
            alert.setContentText("Appointment ID: " + nextAppointment.getId());
        } else {
            alert.setHeaderText("You have no upcoming appointments.");
        }
        alert.showAndWait();
    }

    /**
     * Transfers to the All Customers Form.
     * Triggered by the 'Customers' button.
     * */
    public void goToCustomers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/AllCustomers.fxml"));
            Parent root = loader.load();

            AllCustomersController customersController = loader.getController();
            customersController.passControl(bp);

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Customers form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    /**
     * Transfers to the All Appointments Form.
     * Triggered by the 'Appointments' button.
     * */
    public void goToAppointments(ActionEvent actionEvent) {
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

    /**
     * Transfers to the Reports Form.
     * Triggered by the 'Reports' button.
     * */
    public void goToReports(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/Reports.fxml"));
            Parent root = loader.load();

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Report form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    /**
     * Closes the database connection, if open, and closes application.
     * Triggered by the 'Exit' button.
     * */
    public void exitSystem(ActionEvent actionEvent) {
        if(loggedIn){
            JDBC.closeConnection();
        }
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
