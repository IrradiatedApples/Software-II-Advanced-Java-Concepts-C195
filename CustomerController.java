/**
 *
 * @author Arthur J Amende
 * */
package Controllers;

import Classes.Customer;
import Classes.Directory;
import Classes.Division;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Customer Form.
 * The Customer Form adds new Customer and updates existing Customer.
 * */
public class CustomerController implements Initializable {
    public Label labelError;
    private BorderPane bp;

    private enum MODE {ADD, UPDATE}
    private MODE mode;

    public TextField textID;
    public TextField textName;
    public TextField textAddress;
    public TextField textPostalCode;
    public TextField textPhoneNum;
    public Label labelTitle;
    public ComboBox<String> comboCountry;
    public ComboBox<String> comboDivision;

    private Directory directory = null;

    /**
     * Initializes the Customer Form.
     * Customers, Users, Contacts, and Countries are fetched from the database by a new Directory.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        directory = new Directory();

        ObservableList<String> countryNames = FXCollections.observableArrayList();

        //Lambda Expression
        directory.getAllCountries().forEach((n) -> countryNames.add(n.getName()));
        comboCountry.getItems().addAll(countryNames);

        labelError.setVisible(false);
    }

    /**
     * Passes the Border Pane from the Master Form to control the display.
     * @param bp the Border Pane from the Master Form
     * */
    public void passControl(BorderPane bp){
        this.bp = bp;
    }

    /**
     * Initializes the Customer From in Add Mode.
     * Title is set to 'Add Customer'.
     * */
    public void addMode(){
        mode = MODE.ADD;
        labelTitle.setText("Add Customer");
    }

    /**
     * Initializes the Customer From in Update Mode.
     * Title is set to 'Update Customer'.
     * All fields are set to the values of updateCustomer.
     * @param updateCustomer the Customer to be updated
     * */
    public void updateMode(Customer updateCustomer){
        mode = MODE.UPDATE;
        labelTitle.setText("Update Customer");
        textID.setText(String.valueOf(updateCustomer.getId()));
        textName.setText(updateCustomer.getName());
        textAddress.setText(updateCustomer.getAddress());
        textPostalCode.setText(updateCustomer.getPostalCode());
        textPhoneNum.setText(updateCustomer.getPhoneNum());

        comboCountry.setValue(updateCustomer.getCountry());
        onCountrySelected(new ActionEvent());
        comboDivision.setValue(updateCustomer.getDivision());
    }

    /**
     * Saves the Customer.
     * <p>
     * In Add Mode a new Customer is generated and added to the database.
     * In Update Mode the Customer is updated in the database.
     * Error checking is performed to ensure all fields have data.
     * Any exceptions found stops the save.
     * Triggered when the 'Save' button is selected.
     * </p>
     * */
    public void onSave(ActionEvent actionEvent) {
        String name = textName.getText();
        String address = textAddress.getText();
        String postalCode = textPostalCode.getText();
        String phoneNum = textPhoneNum.getText();

        boolean errorCheck = false;
        //All text fields must have input
        if (name.isEmpty()){
            textName.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            textName.setStyle(null);
        }
        if (address.isEmpty()){
            textAddress.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            textAddress.setStyle(null);
        }
        if (postalCode.isEmpty()){
            textPostalCode.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            textPostalCode.setStyle(null);
        }
        if (phoneNum.isEmpty()){
            textPhoneNum.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            textPhoneNum.setStyle(null);
        }

        //All Combo Boxes must have selection
        if (comboCountry.getValue() == null){
            comboCountry.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            comboCountry.setStyle(null);
        }
        if (comboDivision.getValue() == null){
            comboDivision.setStyle("-fx-border-color: rgb(255, 0, 0);");
            errorCheck = true;
        } else {
            comboDivision.setStyle(null);
        }

        if (!errorCheck){
            Customer newCustomer = new Customer();
            newCustomer.setName(name);
            newCustomer.setAddress(address);
            newCustomer.setPostalCode(postalCode);
            newCustomer.setPhoneNum(phoneNum);

            int countryIndex = comboCountry.getItems().indexOf(comboCountry.getValue());
            int divisionIndex = comboDivision.getItems().indexOf(comboDivision.getValue());
            int divisionID = directory.getAllCountries().get(countryIndex).getAllDivisions().get(divisionIndex).getId();
            newCustomer.setDivisionID(divisionID);

            if (mode == MODE.ADD) {
                if (!directory.addCustomer(newCustomer)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add Failed.");
                    alert.setContentText("Could not add Customer. Please check connection to database and restart program.");
                    alert.showAndWait();
                }
            } else if (mode == MODE.UPDATE){
                newCustomer.setId(Integer.parseInt(textID.getText()));
                if (!directory.updateCustomer(newCustomer)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add Failed.");
                    alert.setContentText("Could not update Customer. Please check connection to database and restart program.");
                    alert.showAndWait();
                }
            }

            goToAllCustomers(actionEvent);
        } else {
            labelError.setVisible(true);
        }
    }

    /**
     * Transfers back to the All Customers From.
     * Triggered when the 'Cancel' button is selected.
     * */
    public void onCancel(ActionEvent actionEvent) {
        goToAllCustomers(actionEvent);
    }

    /**
     * Transfers back to the All Customers From.
     * */
    private void goToAllCustomers(ActionEvent actionEvent) {
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
     * Populates the Division combo box when a Country is selected.
     * */
    public void onCountrySelected(ActionEvent actionEvent) {
        int countryIndex = comboCountry.getItems().indexOf(comboCountry.getValue());
        ObservableList<String> divisionNames = FXCollections.observableArrayList();
        for (Division division : directory.getAllCountries().get(countryIndex).getAllDivisions()){
            divisionNames.add(division.getName());
        }
        comboDivision.getItems().clear();
        comboDivision.getItems().addAll(divisionNames);
        comboDivision.setValue(null);
    }
}
