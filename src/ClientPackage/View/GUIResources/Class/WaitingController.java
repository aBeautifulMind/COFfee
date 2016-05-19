package ClientPackage.View.GUIResources.Class;

import ClientPackage.Controller.ClientController;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Giulio on 16/05/2016.
 */
public class WaitingController implements Initializable {

    private ClientController clientController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    public void firstMainAction(ActionEvent actionEvent) {
        clientController.createAction();
    }

    public void secondMainAction(ActionEvent actionEvent) {

    }
}
