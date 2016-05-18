package CommonModel.Snapshot;

import CommonModel.GameModel.Card.SingleCard.PoliticCard.PoliticCard;
import Server.Model.User;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Emanuele on 18/05/2016.
 */
public class CurrentUser extends BaseUser implements Serializable{

    protected ArrayList<PoliticCard> politicCards;

    protected int mainActionCounter=0;

    protected int fastActionCounter = 0;


    public CurrentUser() {
    }

    public ArrayList<PoliticCard> getPoliticCards() {
        return politicCards;
    }

    public int getMainActionCounter() {
        return mainActionCounter;
    }

    public int getFastActionCounter() {
        return fastActionCounter;
    }
}
