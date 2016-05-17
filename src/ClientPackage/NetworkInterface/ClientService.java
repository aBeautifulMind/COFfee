package ClientPackage.NetworkInterface;

import CommonModel.GameModel.Action.MainActionElectCouncilor;
import Utilities.Exception.ActionNotPossibleException;

import java.rmi.RemoteException;

/**
 * Created by Emanuele on 09/05/2016.
 */
 public abstract class ClientService {



    public abstract boolean Connect();

    public abstract void sendName(String name);

    public abstract void onTestAction(MainActionElectCouncilor electCouncilor) throws ActionNotPossibleException, RemoteException;
}