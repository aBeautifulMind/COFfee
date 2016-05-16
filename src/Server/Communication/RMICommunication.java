package Server.Communication;

import CommonModel.GameImmutable;
import CommonModel.GameModel.Action.Action;
import CommonModel.GameModel.ActionNotPossibleException;
import Interface.RMIClientHandler;
import Interface.RMIClientInterface;
import Server.Controller.GameController;
import Server.Managers.GamesManager;
import Server.UserClasses.User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Emanuele on 09/05/2016.
 */
public class RMICommunication extends BaseCommunication implements RMIClientHandler {

    private User user;
    private RMIClientInterface rmiClientInterface;
    private GamesManager gamesManager;
    private GameController gameController;
    private GameImmutable gameImmutable;

    public RMICommunication(String name) throws RemoteException {
        //super();
        gamesManager = GamesManager.getInstance();
        UnicastRemoteObject.exportObject(this,0);
    }




    @Override
    public boolean sendIP(String ip, String name) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(ip,1099);
        try {
            rmiClientInterface = (RMIClientInterface) registry.lookup(name);
            return true;
        } catch (NotBoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tryToSetName(String username) throws RemoteException {
        if(!gamesManager.userAlreadyPresent(username)){
            this.user.setUsername(username);
            gamesManager.addToGame(user);
            return true;
        }

        return false;
    }

    @Override
    public void test(Action electCouncillor) throws ActionNotPossibleException {
        electCouncillor.doAction(user.getGame(), user);
    }


    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void notifyGameStart() {

    }
}
