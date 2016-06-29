package ClientPackage.View.GUIResources.Class;

import ClientPackage.Controller.ClientController;
import ClientPackage.View.GUIResources.customComponent.ImageLoader;
import ClientPackage.View.GeneralView.GUIView;
import CommonModel.Snapshot.BaseUser;
import Utilities.Class.Constants;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Giulio on 25/06/2016.
 */
public class FinishMatchController implements Initializable {

    private ClientController clientController;
    private GUIView baseView;
    private ImageView winnerOrLoser;
    private JFXListView<String> ranking = new JFXListView<>();
    private ArrayList<String> usernameRanking = new ArrayList<>();
    private PopOver innerPopOver;
    private ImageView innerImage;
    private Pane innerPaneWhereShow = new Pane();
    private ImageView backgroundImage;

    @FXML private GridPane gridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setClientController(ClientController clientController, GUIView baseView) {
        this.clientController = clientController;
        this.baseView = baseView;
        displayFinalScreen();
    }

    private void displayFinalScreen() {
        backgroundImage = new ImageView(ImageLoader.getInstance().getImage(Constants.IMAGE_PATH + "/LoginBackground.png"));
        gridPane.add(backgroundImage, 0, 0);
        GridPane.setColumnSpan(backgroundImage, 2);
        GridPane.setRowSpan(backgroundImage, 2);
        backgroundImage.fitWidthProperty().bind(gridPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(gridPane.heightProperty());
        gridPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("event x and y "+event.getX()/gridPane.getWidth()+" "+event.getY()/gridPane.getHeight());
            }
        });
        innerImage = new ImageView(ImageLoader.getInstance().getImage(Constants.IMAGE_PATH + "/InnKeeper.png"));
        innerImage.fitHeightProperty().bind(backgroundImage.fitHeightProperty().divide(2));
        innerImage.setPreserveRatio(true);
        innerPaneWhereShow.setPrefWidth(10);
        innerPaneWhereShow.setPrefHeight(10);
        //innerPaneWhereShow.layoutXProperty().bind(gridPane.widthProperty().multiply(0.095));
        //innerPaneWhereShow.layoutYProperty().bind(gridPane.heightProperty().multiply(0.5858));

        for (BaseUser baseUser : clientController.getFinalSnapshot()) {
            usernameRanking.add(baseUser.getUsername());
        }
        ranking.setItems(FXCollections.observableArrayList(usernameRanking));
        if(clientController.amIAWinner()) {
            winnerOrLoser = new ImageView(ImageLoader.getInstance().getImage(Constants.IMAGE_PATH + "/YouWin.png"));
        } else {
            winnerOrLoser = new ImageView(ImageLoader.getInstance().getImage(Constants.IMAGE_PATH + "/GameOver.png"));
        }
        winnerOrLoser.fitWidthProperty().bind(backgroundImage.fitWidthProperty().divide(3));
        winnerOrLoser.setPreserveRatio(true);
        ranking.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                displayInfo(ranking.getSelectionModel().getSelectedItem());
            }
        });
        ranking.setStyle("-fx-background-color: transparent");
        ranking.prefWidthProperty().bind(gridPane.widthProperty().divide(4));
        gridPane.add(innerImage, 0, 0);
        GridPane.setRowSpan(innerImage, 3);
        gridPane.add(ranking, 1, 1);
        gridPane.add(winnerOrLoser, 0, 0);
        GridPane.setRowSpan(winnerOrLoser, 3);
        GridPane.setColumnSpan(winnerOrLoser, 3);
        GridPane.setValignment(winnerOrLoser, VPos.TOP);
        GridPane.setHalignment(winnerOrLoser, HPos.RIGHT);
        GridPane.setValignment(innerImage, VPos.BOTTOM);
        GridPane.setHalignment(innerImage, HPos.LEFT);
        GridPane.setHalignment(ranking, HPos.CENTER);
        GridPane.setValignment(ranking, VPos.CENTER);
        winnerOrLoser.toBack();
        backgroundImage.toBack();
        gridPane.getChildren().add(innerPaneWhereShow);

    }

    private void displayInfo(String selectedItem) {
        BaseUser baseUser = clientController.getUserWithString(selectedItem);
        StackPane popOverStackPane = new StackPane();
        popOverStackPane.setAlignment(Pos.CENTER);
        Text innerText = new Text();
        System.out.println(baseUser.getUsername() + " DA ORA");
        System.out.println(clientController.getUserPosition(selectedItem));
        System.out.println(baseUser.getVictoryPathPosition());
        System.out.println(baseUser.getNobilityPathPosition().getPosition());
        System.out.println(baseUser.getHelpers().size());
        System.out.println(baseUser.getPoliticCardNumber());
        System.out.println(" ECCO QUI" + clientController.getUserBuilding(selectedItem));
        innerText.setText("Vò narrando delle gesta di " + baseUser.getUsername() + ".\nSi posizionò " + clientController.getUserPosition(selectedItem) + "° nella maggior gara del nuovo anno.\n"
                + "Riuscì ad ottenere molti scudi prestigiosi dalle sue " + baseUser.getVictoryPathPosition() + " vittorie.\n"
                + "Conobbe vari nobili città dove gli vennero donati prestigiosi premi. In particolare, rimasto molto affascinato, si fermò nel " + baseUser.getNobilityPathPosition().getPosition() + "° posto.\n "
                + "Riuscì ad ottenere grandi ricompense, fino ad arrivare a " + baseUser.getCoinPathPosition() + " monete d'oro.\n"
                + "Conobbi anche tutti i suoi " + baseUser.getHelpers().size() + " servitori ed aiutanti, a lui molto cari.\n"
                + "Grande personaggio fu questo " + baseUser.getUsername() + ". Mi ricorderò sempre di quel giorno che mi fece vedere le sue" + baseUser.getPoliticCardNumber()
                + "prestigiose carte politiche con cui poteva soddisfare qualsiasi consiglio.\n"
                + "In tutto il mondo è noto il suo nome. Ovunque si sa che il magnifico " + baseUser.getUsername() + "riuscì a costruire empori in molte città, come\n"
                + clientController.getUserBuilding(selectedItem) +"\n" + "Grande uomo fu " + baseUser.getUsername() + ", scaltro nel gioco quanto intelligente nelle azioni.");
        innerText.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 15));
        popOverStackPane.getChildren().add(innerText);
        innerPopOver = new PopOver();
        innerPopOver.setContentNode(popOverStackPane);
        innerPopOver.show(innerPaneWhereShow);
    }

}