package Server.Controller;

import CommonModel.GameModel.Action.Action;
import CommonModel.GameModel.Card.Deck.PermitDeck;
import CommonModel.GameModel.Card.SingleCard.PermitCard.PermitCard;
import CommonModel.GameModel.Card.SingleCard.PoliticCard.PoliticCard;
import CommonModel.GameModel.City.City;
import CommonModel.GameModel.City.CityName;
import CommonModel.GameModel.City.RegionName;
import CommonModel.GameModel.Council.Helper;
import CommonModel.GameModel.Council.King;
import CommonModel.GameModel.Market.BuyableObject;
import CommonModel.GameModel.Market.BuyableWrapper;
import CommonModel.Snapshot.BaseUser;
import CommonModel.Snapshot.SnapshotToSend;
import Server.Model.FakeUser;
import Server.Model.Game;
import Server.Model.Map;
import Server.Model.User;
import Utilities.Class.Constants;
import Utilities.Exception.ActionNotPossibleException;
import Utilities.Exception.AlreadyPresentException;
import Utilities.Exception.MapsNotFoundException;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Emanuele on 13/05/2016.
 */
public class GameController implements Serializable{

    private Game game;
    private TimerTask timerTask;
    private Timer timer;
    private int duration = Constants.GAME_TIMEOUT;
    private ArrayList<Map> availableMaps = new ArrayList<>();
    // initialized to users size, when 0 start market
    private int turnCounter =0;
    private HashMap<User,Boolean> marketHashMap = new HashMap<>();
    private ArrayList<User> users = new ArrayList<>();
    private boolean sellPhase=false;
    private boolean buyPhase = false;
    private int nextUser;
    private int lastUser = -1;

    public GameController() {
    }

    public GameController(Game game) {
        this.game = game;
        this.timer = new Timer();
        try {
            availableMaps = Map.readAllMap();
        } catch (MapsNotFoundException e) {
            System.out.println(e);
        }
    }

    public void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                notifyStarted();
            }
        };
    }

    /**
     * Called when init game
     */
    public void notifyStarted() {
        users = new ArrayList<>(game.getUsers());
        game.setStarted(true);
        setDefaultStuff();
        if (game.getUsers().size() == 2){
            configurationForTwoPlayers();
        }
        // send map to first user
        for(User user: users) {
            if(user.isConnected()) {
                sendAvailableMap(user);
                break;
            }
        }

    }

    private void configurationForTwoPlayers() {
        ArrayList<PermitCard> permitCardArray = new ArrayList<>();
        for (java.util.Map.Entry<RegionName, PermitDeck> permitDeck : game.getPermitDecks().entrySet()) {
            permitCardArray.add(permitDeck.getValue().getAndRemoveRandomPermitCard());
        }
        FakeUser fakeUser = new FakeUser();
        for (PermitCard permitCard : permitCardArray) {
            for (Character character : permitCard.getCityAcronimous()){
                //TODO regola
            }
        }

    }

    private void setDefaultStuff() {
        int userCounter = 0;
        for (User user: users){
            user.setHelpers(Constants.DEFAULT_HELPER_COUNTER + userCounter);
            user.setCoinPathPosition(Constants.FIRST_INITIAL_POSITION_ON_MONEY_PATH + userCounter);
            user.setNobilityPathPosition(game.getNobilityPath().getPosition()[Constants.INITIAL_POSITION_ON_NOBILITY_PATH]);
            user.setVictoryPathPosition(Constants.INITIAL_POSITION_ON_VICTORY_PATH);

            ArrayList<PoliticCard> politicCardArrayList = new ArrayList<>();
            for(int cont = 0; cont < Constants.DEFAULT_POLITIC_CARD_HAND; cont++){
                politicCardArrayList.add(game.getPoliticCards().drawACard());
            }
            user.setPoliticCards(politicCardArrayList);

            for (int i = 0; i<2;i++){
                user.addPermitCard(game.getPermitDeck(RegionName.HILL).getPermitCardVisible(i));
            }


            userCounter++;
        }
    }

    public void cancelTimeout() {
        timer.cancel();
    }

    public void setTimeout() {
        if(timer==null){
            timer = new Timer();
        }
        else{
            timer.cancel();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    notifyStarted();
                }
            };
            timer = new Timer();
        }
        timer.schedule(timerTask,duration);
    }


    /**
     * create snapshot and change round
     * @param user user that has finished round
     */
    public void onFinishRound(User user) {
        turnCounter--;
        System.out.println("on finish round called");
        user.getBaseCommunication().finishTurn();

        for(int cont = 0; cont < users.size(); cont++){
            System.out.println("GAMECONTROLLER <- Sending Snapshot to :" + users.get(cont).getUsername());
            SnapshotToSend snapshotToSend = new SnapshotToSend(game, user);
            user.getBaseCommunication().sendSnapshot(snapshotToSend);
            if(user.equals(users.get(cont))){
                nextUser = cont+1;
                while (!users.get((nextUser)%game.getUsers().size()).isConnected() && nextUser%game.getUsers().size()!=cont){
                    System.out.println("user not connected "+ users.get((nextUser)%game.getUsers().size()));
                    turnCounter--;
                    nextUser++;
                }
                if (lastUser != nextUser) {
                    if ((nextUser % game.getUsers().size()) == cont) {
                        onAllUserDisconnected();
                    } else {
                        if (turnCounter <= 0) {
                            System.out.println("Starting market");
                            startMarket();
                        } else {
                            System.out.println("change round : " + nextUser);
                            changeRound(nextUser);
                        }
                    }
                } else {
                    checkUserWhoWin();
                }
            }
        }
    }

    private void changeRound(int nextUser) {
        ArrayList<User> userArrayList = new ArrayList<>(game.getUsers());
        userArrayList.get((nextUser) % game.getUsers().size()).setMainActionCounter(Constants.MAIN_ACTION_POSSIBLE);
        userArrayList.get((nextUser) % game.getUsers().size()).setFastActionCounter(Constants.FAST_ACTION_POSSIBLE);
        userArrayList.get((nextUser)%game.getUsers().size()).drawCard();
        userArrayList.get((nextUser) % game.getUsers().size()).getBaseCommunication().changeRound();
        startRoundTimer();
    }

    private void startRoundTimer() {
        //// TODO: 17/06/2016 start timer for user round
    }

    private void startMarket() {
        sellPhase = true;
        marketHashMap.clear();

        users.forEach(user -> {
            Runnable runnable = ()-> {
                sendStartMarket(user);
            };
            new Thread(runnable).start();

        });

    }

    private void sendStartMarket(User user) {
        user.getBaseCommunication().sendStartMarket();
    }

    private void onAllUserDisconnected() {
        System.out.println("All user Disconnected! I don't know what to do");
    }

    public void doAction(Action action, User user) throws ActionNotPossibleException {
        action.doAction(game,user);
    }

    // send available map
    private void sendAvailableMap(User userToAdd) {
        userToAdd.getBaseCommunication().sendAvailableMap(availableMaps);
    }

    /* set map and init game*/
    public void setMap(Map map) {
        if(availableMaps.contains(map)){
            Map mapToFind = findMap(map);
            game.setMap(mapToFind);
            game.setKing(new King(map.getCity().get(0),game.getBank()));
            for (User user: game.getUsers()) {
                SnapshotToSend snapshotToSend = new SnapshotToSend(game,user);
                // init game
                user.getBaseCommunication().sendSelectedMap(snapshotToSend);
            }
            sendFinishMarketToAll();
            selectFirstPlayer();
        }
        else{
            System.out.println("MAP NOT PRESENT");
        }
    }

    private Map findMap(Map map) {
        for (Map mapToSelect: availableMaps){
            if(map.equals(mapToSelect)){
                return mapToSelect;
            }
        }
        return null;
    }

    /** disable market phase in all user */
    private void sendFinishMarketToAll() {
        new Thread(()->{
            for (User user:users){
                user.getBaseCommunication().disableMarketPhase();
            }
        }).start();

    }

    private void selectFirstPlayer() {

        ArrayList<User> users = new ArrayList<>(game.getUsers());
        turnCounter=users.size();
       // select first user

        for(User user: users){
            if(user.isConnected()){
                changeRound(users.indexOf(user));
                nextUser = users.indexOf(user);
                break;
            }
        }


        for(int i = 1;i< users.size();i++){
            users.get(i).getBaseCommunication().finishTurn();
        }

        sendSnapshotToAll();
    }

    public synchronized void sendSnapshotToAll() {
        new Thread(()-> {
            for (User user : game.getUsers()) {
                SnapshotToSend snapshotToSend = new SnapshotToSend(game, user);
                user.getBaseCommunication().sendSnapshot(snapshotToSend);
            }
        }).start();
    }

    public boolean onReceiveBuyableObject(ArrayList<BuyableWrapper> buyableWrappers) {
        for (BuyableWrapper buyableWrapper: buyableWrappers) {
            try {
                game.addBuyableWrapper(buyableWrapper);
            } catch (AlreadyPresentException e) {
                System.out.println("L'oggetto è già in vendita!");
            }
        }
        sendSnapshotToAll();
        return true;

    }

    public boolean onBuyObject(User user, ArrayList<BuyableWrapper> buyableWrappers) {
        int counter = 0;
        for (BuyableWrapper buyableWrapper : buyableWrappers) {
            try {
                game.getMoneyPath().goAhead(user, -buyableWrapper.getCost());
                game.getMoneyPath().goAhead(game.getUser(buyableWrapper.getUsername()),buyableWrapper.getCost());
                game.removeFromMarketList(buyableWrapper);
                if(buyableWrapper.getBuyableObject() instanceof PermitCard){
                    System.out.println("found permit card");
                    game.getUser(buyableWrapper.getUsername()).removePermitCardDefinitevely((PermitCard) buyableWrapper.getBuyableObject());
                    user.addPermitCard((PermitCard) buyableWrapper.getBuyableObject());
                }
                else if(buyableWrapper.getBuyableObject() instanceof PoliticCard){
                    System.out.println("found politic card");
                    game.getUser(buyableWrapper.getUsername()).removePoliticCard((PoliticCard) buyableWrapper.getBuyableObject());
                    user.addPoliticCard((PoliticCard)buyableWrapper.getBuyableObject());
                }
                else if(buyableWrapper.getBuyableObject() instanceof Helper){
                    System.out.println("found helper");
                    game.getUser(buyableWrapper.getUsername()).removeHelper((Helper)buyableWrapper.getBuyableObject());
                    user.addHelper();
                }
                counter++;
            } catch (ActionNotPossibleException e) {

            }
        }
        sendSnapshotToAll();
        System.out.println("after starting thread in game controller");

        if(counter==buyableWrappers.size()){
            return true;
        }
        else return false;
    }

    public void onRemoveItem(BuyableWrapper item) {
        game.removeFromMarketList(item);
        sendSnapshotToAll();
    }

    public void onFinishSellPhase(User user) {
        long finishedUser=0;
        if(sellPhase) {
            marketHashMap.put(user, true);

            for (Boolean value : marketHashMap.values()) {
                if (value) {
                    finishedUser++;
                }
            }
            finishedUser = marketHashMap.entrySet().stream()
                    .filter(java.util.Map.Entry::getValue)
                    .count();

            if (finishedUser == users.size()) {
                sellPhase=false;
                marketHashMap.clear();
                startBuyPhase();

            }
        }
    }

    private void startBuyPhase() {
        sendSnapshotToAll();
        buyPhase = true;
        selectRandomUser();
    }

    private void selectRandomUser() {
        Random random = new Random();
        int userNumber =0;
        boolean found = false;
        while (!found) {
            userNumber = random.nextInt(users.size());
            if(!marketHashMap.containsKey(users.get(userNumber)) || !marketHashMap.get(users.get(userNumber))){
                found = true;
            }
        }
        users.get(userNumber).getBaseCommunication().sendStartBuyPhase();
    }

    public void onFinishBuyPhase(User user) {
        if(buyPhase){
            sendSnapshotToAll();
            marketHashMap.put(user,true);

            long finishedUser = marketHashMap.entrySet().stream()
                    .filter(java.util.Map.Entry::getValue)
                    .count();
            if(finishedUser<users.size()) {
                selectRandomUser();
            }
            else {
                marketHashMap.clear();
                buyPhase=false;
                sendFinishMarketToAll();
                turnCounter=users.size();
                changeRound(nextUser);
            }
        }
    }

    public void getCityRewardBonus(City city1, User user) {
        try {
            City city = game.getCity(city1);

            city.getBonus(user,game);

            user.decrementOptionalActionCounter();

            sendSnapshotToAll();

        } catch (ActionNotPossibleException e) {
            e.printStackTrace();
        }
    }

    public void onSelectPermitCard(PermitCard permitCard, User user) {
        PermitDeck permitDeck = game.getPermitDeck(permitCard.getRetroType());
        try {
            PermitCard permitCardTrue = permitDeck.getAndRemovePermitCardVisible(permitCard);
            permitCardTrue.getBonus().getBonus(user,game);
            user.addPermitCard(permitCardTrue);
            user.decrementOptionalActionCounter();
        } catch (ActionNotPossibleException e) {
            e.printStackTrace();
        }
        sendSnapshotToAll();
    }

    public void changeMasterUser() {
        // send map to first user
        for (User user : users) {
            if (user.isConnected()) {
                sendAvailableMap(user);
            }
        }
    }
    public void startingLastRound() {
        lastUser = nextUser;
    }

    public void checkUserWhoWin(){
        ArrayList<User> firstNobilityPathUserToReward = new ArrayList<>();
        ArrayList<User> secondNobilityPathUserToReward = new ArrayList<>();
        User userToRewardMaxPermitCard = new User();
        User userWithMaxHelperAndPoliticCard = new User();
        for (User user : users) {
            if (user.getNobilityPathPosition().getPosition() > firstNobilityPathUserToReward.get(0).getNobilityPathPosition().getPosition()){
                firstNobilityPathUserToReward.clear();
                firstNobilityPathUserToReward.add(user);
            }
            if (user.getNobilityPathPosition().getPosition() == firstNobilityPathUserToReward.get(0).getNobilityPathPosition().getPosition()){
                firstNobilityPathUserToReward.add(user);
            } else {
                if (user.getNobilityPathPosition().getPosition() > secondNobilityPathUserToReward.get(0).getNobilityPathPosition().getPosition())
                    secondNobilityPathUserToReward.clear();
                    secondNobilityPathUserToReward.add(user);
                if (user.getNobilityPathPosition().getPosition() == secondNobilityPathUserToReward.get(0).getNobilityPathPosition().getPosition())
                    secondNobilityPathUserToReward.add(user);
            }
            if (user.getPermitCards().size() > userToRewardMaxPermitCard.getPermitCards().size()){
                userToRewardMaxPermitCard = user;
            }
            if (user.getHelpers().size() + user.getPoliticCardSize() > userWithMaxHelperAndPoliticCard.getHelpers().size() + userWithMaxHelperAndPoliticCard.getPoliticCardSize()){
                userWithMaxHelperAndPoliticCard = user;
            }
        }
        firstNobilityPathUserToReward.forEach(user -> user.setVictoryPathPosition(user.getVictoryPathPosition()+5));
        secondNobilityPathUserToReward.forEach(user -> user.setVictoryPathPosition(user.getVictoryPathPosition() + 2));
        userToRewardMaxPermitCard.setVictoryPathPosition(userToRewardMaxPermitCard.getVictoryPathPosition()+3);
        ArrayList<User> userWhoWin = new ArrayList<>();
        userWhoWin = checkFirst();

        if (userWhoWin.size()>1){
            for (User user : users) {
                if (user.equals(userWithMaxHelperAndPoliticCard)){
                    user.setVictoryPathPosition(user.getVictoryPathPosition()+3);
                }
            }
        }
        userWhoWin.clear();
        userWhoWin = checkFirst();
        users.forEach(user -> {

        });

        ArrayList<User> finalUserWhoWin = userWhoWin;
        users.forEach(user -> {
            if(isAWinner(finalUserWhoWin, user))
                user.getBaseCommunication().sendMatchFinishedWithWin(true);
            else
                user.getBaseCommunication().sendMatchFinishedWithWin(false);
        });
    }

    private boolean isAWinner(ArrayList<User> userWhoWin, User userToCheck){
        for (User user : userWhoWin) {
            if (user.equals(userToCheck))
                return true;
        }
        return false;
    }

    private ArrayList<User> checkFirst() {
        ArrayList<User> userWhoWin = new ArrayList<>();
        users.forEach(user -> {
            if (user.getVictoryPathPosition() > userWhoWin.get(0).getVictoryPathPosition()) {
                userWhoWin.clear();
                userWhoWin.add(user);
            }
            if (user.getVictoryPathPosition() == userWhoWin.get(0).getVictoryPathPosition()) {
                userWhoWin.add(user);
            }
        });
        return userWhoWin;
    }


    public void onUserPass(User user) {

        if(users.indexOf(user) == (nextUser%users.size()) && !buyPhase && !sellPhase) {
            user.setMainActionCounter(0);
            user.setFastActionCounter(0);
            user.decrementOptionalActionCounter();
        }
    }
}
