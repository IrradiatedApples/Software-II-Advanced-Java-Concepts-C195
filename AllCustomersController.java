/**
 *
 * @author Arthur J Amende
 * */
package Controllers;

import Classes.Customer;
import Classes.Directory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the All Customers Form.
 * All Customers Form includes a table of all Customers.
 * */
public class AllCustomersController implements Initializable {
    private BorderPane bp;

    public TableView tableCustomers;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colPostalCode;
    public TableColumn colPhoneNum;
    public TableColumn colDivision;
    public TableColumn colCountry;

    private ObservableList<Classes.Customer> displayCustomers = FXCollections.observableArrayList();
    private Directory directory = null;

    /**
     * Initializes the All Appointments Form.
     * Customers, Users, Contacts, and Countries are fetched from the database by a new Directory. Table View is initialized.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        directory = new Directory();

        displayCustomers = directory.getAllCustomers();
        tableCustomers.setItems(displayCustomers);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        colCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        colPhoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
    }

    /**
     * Passes the Border Pane from the Master Form to control the display.
     * @param bp the Border Pane from the Master Form
     * */
    public void passControl(BorderPane bp){
        this.bp = bp;
    }

    /**
     * Transfers to the Customer Form in Add Mode.
     * Triggered by the 'Add' button.
     * */
    public void toAddCustomer(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/Customer.fxml"));
            Parent root = loader.load();

            CustomerController customerController = loader.getController();
            customerController.passControl(bp);
            customerController.addMode();

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Add Customer form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    /**
     * Transfers to the Customer Form in Update Mode.
     * Triggered by the 'Update' button.
     * */
    public void toUpdateCustomer(ActionEvent actionEvent) {
        Customer selectedCustomer = (Customer) tableCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/Customer.fxml"));
            Parent root = loader.load();

            CustomerController customerController = loader.getController();
            customerController.passControl(bp);
            customerController.updateMode(selectedCustomer);

            bp.setCenter(root);
        } catch (Exception e){
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not open form.");
            alert.setContentText("Could not open Update Customer form. Please check connection to database and restart program.");
            alert.showAndWait();
        }
    }

    /**
     * Deletes Customer from the database.
     * All Appointments for the selected Customer will also be deleted from the database.
     * Triggered by the 'Delete' button.
     * If no Customer is selected nothing happens.
     * */
    public void onDelete(ActionEvent actionEvent) {
        Customer selectedCustomer = (Customer) tableCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText(selectedCustomer.getName());
        alert.setContentText("Do you want to delete this customer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (directory.deleteCustomer(selectedCustomer)){
                displayCustomers = directory.getAllCustomers();
                tableCustomers.setItems(displayCustomers);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Could not perform action.");
                alert.setContentText("Could not delete Customer. Please check connection to database and restart program.");
                alert.showAndWait();
            }
        }
    }
}
