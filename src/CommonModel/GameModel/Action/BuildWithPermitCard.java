package CommonModel.GameModel.Action;

import ClientPackage.Service.FactoryService;
import CommonModel.GameModel.ActionNotPossibleException;
import CommonModel.GameModel.Bonus.RegionBonusCard;
import CommonModel.GameModel.Card.PermitCard;
import CommonModel.GameModel.City.*;
import Server.Model.Game;
import Server.UserClasses.User;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;

/**
 * Created by Giulio on 16/05/2016.
 */
public class BuildWithPermitCard implements Action{

    private PermitCard permitCard;
    private City city;
    private final String type = "MAIN_ACTION";

    public BuildWithPermitCard(City city, PermitCard permitCard) {
        this.city = city;
        this.permitCard = permitCard;
    }

    @Override
    public void doAction(Game game, User user) throws ActionNotPossibleException {
        City gameCity = game.getCity(city);
        int helperToSpend = 0;
        for (User userToFind: game.getUsers()) {
            if (userToFind.getUsersEmporium().contains(gameCity)){
                helperToSpend++;
            }
        }
        if (user.getHelpers()>=helperToSpend){
            user.setHelpers(user.getHelpers()-helperToSpend);
            user.addEmporium(gameCity);
            gameCity.getBonus().getBonus(user, game);
            CityVisitor cityVisitor = new CityVisitor(game.getGraph(), user.getUsersEmporium());
            for (City cityToVisit : cityVisitor.visit(gameCity)) {
                cityToVisit.getBonus().getBonus(user, game);
            }
            if (gameCity.getRegion().checkRegion(user.getUsersEmporium())){
                game.getRegionBonusCard(gameCity.getRegion().getRegion()).getBonus(user, game);
            }
            if (gameCity.getColor().checkColor(user.getUsersEmporium())){
                game.getColorBonusCard(gameCity.getColor().getColor()).getBonus(user, game);
            }
        } else {
            throw new ActionNotPossibleException();
        }


    }

    @Override
    public String getType() {
        return type;
    }

}
