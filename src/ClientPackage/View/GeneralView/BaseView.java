package ClientPackage.View.GeneralView;

import CommonModel.GameModel.City.City;
import CommonModel.Snapshot.SnapshotToSend;
import Server.Model.Map;
import Utilities.Exception.ActionNotPossibleException;

import java.util.ArrayList;

/**
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
}
