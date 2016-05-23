package CommonModel.GameModel.Action;

import Utilities.Class.Constants;
import Utilities.Exception.ActionNotPossibleException;
import Server.Model.Game;
import Server.Model.User;

/**
 * Created by Giulio on 17/05/2016.
 */
public class FastActionMoneyForHelper extends Action {

    public FastActionMoneyForHelper() {
        this.type = Constants.FAST_ACTION;
    }

    @Override
    public void doAction(Game game, User user) throws ActionNotPossibleException {
        if(super.checkActionCounter(user)) {
            if (user.getVictoryPathPosition() >= Constants.MONEY_LIMITATION_MONEY_FOR_HELPER) {
                game.getVictoryPath().goAhead(user, -Constants.MONEY_LIMITATION_MONEY_FOR_HELPER);
                user.setHelpers(user.getHelpers() + Constants.HELPER_ADDED_MONEY_FOR_HELPER);
                removeAction(game, user);
            } else {
                throw new ActionNotPossibleException();
            }
        }
    }
}
