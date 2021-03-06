package ClientPackage.View.GeneralView;

import CommonModel.GameModel.Action.Action;
import CommonModel.GameModel.City.City;
import CommonModel.Snapshot.SnapshotToSend;
import Server.Model.Map;
import Utilities.Exception.ActionNotPossibleException;

import java.util.ArrayList;

/** This abstract class is that in which are written the methods which will then ovverrided from implementations in the CLI and GUI.
 * Created by Emanuele on 13/05/2016.
 */
public interface BaseView {

    void initView();

    void showLoginError();

    void showWaitingForStart();

    void showMap(ArrayList<Map> mapArrayList);

    void gameInitialization(SnapshotToSend snapshotToSend);

    void turnFinished();

    void isMyTurn(SnapshotToSend snapshot);

    void updateSnapshot();

    void onStartMarket();

    void onStartBuyPhase();

    void onFinishMarket();

    void selectPermitCard();

    void selectCityRewardBonus();

    void onMoveKing(ArrayList<City> kingPath);

    void onActionNotPossibleException(ActionNotPossibleException e);

    void sendMatchFinishedWithWin();

    void selectOldPermitCardBonus();

    void onActionDone(Action action);

    void onUserDisconnect(String username);
}
