package CommonModel.GameModel.Bonus.Reward;

import CommonModel.GameModel.Bonus.SingleBonus.VictoryPointBonus;
import Utilities.Class.Constants;
import Utilities.Exception.ActionNotPossibleException;
import CommonModel.GameModel.City.Color;
import Server.Model.Game;
import Server.Model.User;
import java.io.Serializable;

/**
 * Created by Giulio on 13/05/2016.
 */

/**
 * Carte nella plancia del re che dicono bonus per ogni colore della città
 */
public class ColorBonusCard implements RewardBonusCard,Serializable {

    private Color color;
    private VictoryPointBonus victoryPointBonus;

    public ColorBonusCard() {
    }

    public ColorBonusCard(Color color) {
        this.color = color;
        switch(color.getColor()){
            case Constants.BLUE:
                victoryPointBonus = new VictoryPointBonus(Constants.BLUE_BONUS);
                break;
            case Constants.GREY:
                victoryPointBonus = new VictoryPointBonus(Constants.GREY_BONUS);
                break;
            case Constants.ORANGE:
                victoryPointBonus = new VictoryPointBonus(Constants.ORANGE_BONUS);
                break;
            case Constants.YELLOW:
                victoryPointBonus = new VictoryPointBonus(Constants.YELLOW_BONUS);
                break;
        }
    }

    @Override
    public void getBonus(User user, Game game) throws ActionNotPossibleException {
        victoryPointBonus.getBonus(user, game);
    }

}
