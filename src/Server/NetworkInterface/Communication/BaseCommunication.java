package Server.NetworkInterface.Communication;

import Server.Model.User;

/**
 * Created by Emanuele on 09/05/2016.
 */
public abstract class BaseCommunication {

    public abstract void setUser (User user);

    public abstract void notifyGameStart();
}