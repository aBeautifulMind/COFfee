package Server.Model;

import CommonModel.GameImmutable;
import CommonModel.GameModel.*;
import CommonModel.GameModel.Bonus.ColorBonusCard;
import CommonModel.GameModel.Bonus.KingBonusCard;
import CommonModel.GameModel.Bonus.RegionBonusCard;
import CommonModel.GameModel.Card.Deck.PermitDeck;
import CommonModel.GameModel.Card.PermitCard;
import CommonModel.GameModel.Card.PoliticCard;
import CommonModel.GameModel.Card.PoliticColor;
import CommonModel.GameModel.City.City;
import CommonModel.GameModel.City.CityName;
import CommonModel.GameModel.City.Color;
import CommonModel.GameModel.City.Region;
import CommonModel.GameModel.Path.MoneyPath;
import CommonModel.GameModel.Path.NobilityPath;
import CommonModel.GameModel.Path.VictoryPath;
import Server.Controller.GameController;
import Server.UserClasses.User;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.sql.Statement;
import java.util.*;

/**
 * Created by Emanuele on 11/05/2016.
 */
public class Game implements GameImmutable{

    /**
     * True if game is full (There game is started and the players are playing)
     */
    private boolean started;


    /**
     * All users in the game with their name
     */
    private HashMap<String,User> usersInGame = new HashMap<>();

    /**
     * All cities in undirectedgraph
     */
    private UndirectedGraph<City,DefaultEdge> cities;

    private HashMap<String,Region> regions = new HashMap<>();

    /**
     * King with his cities
     */
    private King king;

    // PATHS
    private VictoryPath victoryPath;

    private NobilityPath nobilityPath;

    private MoneyPath moneyPath;

    private GameController gameController;

    // PERMIT DECK
    private PermitDeck mountainDeck;

    private PermitDeck hillDeck;

    private PermitDeck coastDeck;

    // POLITIC CARD
    private ArrayList<PoliticCard> politicCards = new ArrayList<>();
    private UndirectedGraph<City, DefaultEdge> graph;

    private HashMap<String,RegionBonusCard> regionBonusCard = new HashMap<>();
    private HashMap<String,ColorBonusCard> colorBonusCard = new HashMap<>();
    private Stack<KingBonusCard> kingBonusCard = new Stack<>();


    public Game() {
        this.started = false;
        gameController = new GameController(this);
        gameController.startTimer();
        cities = new SimpleGraph<City, DefaultEdge>(DefaultEdge.class);
        // create permit card decks
        createDecks();
        // create nobility, victory and moneyPath
        createPaths();
        // create city Graph
        createCityGraph();
        //create regionBonusCard, kingBonusCard, colorBonusCard
        createBonusDeck();
        //create Politic Card Deck
        createPoliticCards();
        //create Region
        createRegion();
        //create king
        king = new King();
    }

    private void createRegion() {
        for (Region region:Region.values()) {
            regions.put(region.getRegion(),region);
            System.out.println(region);
        }
    }

    private void createPoliticCards() {
        // foreach color create thirteen cards
        for (PoliticColor politicColor:PoliticColor.values()) {

            for (int i = 0; i< 13; i++){
                politicCards.add(new PoliticCard(politicColor));
            }

        }
        //TODO: Add multicolor card
    }

    private void createBonusDeck() {
        //region bonus
        for (Region region:Region.values()) {
            regionBonusCard.put(region.getRegion(),new RegionBonusCard(region));
        }

        // color bonus
        for (Color color:Color.values()) {
            colorBonusCard.put(color.getColor(),new ColorBonusCard(color));
        }

        //king bonus
        for (int i= 1; i<6;i++){
            kingBonusCard.add(new KingBonusCard(i));
        }


    }

    private void createCityGraph() {
        //TODO: create graph and connection between city
    }

    private void createPaths() {
        nobilityPath = new NobilityPath();
        moneyPath = new MoneyPath();
        victoryPath = new VictoryPath();
    }


    @Override
    public boolean isStarted() {
        return started;
    }


    public boolean addUserToGame(User userToAdd) {
        System.out.println("ADDING A USER TO A GAME "+userToAdd);
        if(!usersInGame.containsKey(userToAdd.getUsername())){
            usersInGame.put(userToAdd.getUsername(),userToAdd);
            if(usersInGame.size()>=2 && usersInGame.size()<4){
                gameController.setTimeout();
            }
            else if(usersInGame.size()>=2){
                gameController.cancelTimeout();
                gameController.notifyStarted();
            }
            return true;
        }
        return false;
    }





    public void setStarted(boolean started) {
        // inizializzazione partita
        
        this.started = started;
    }

    private void createDecks() {
        //create mountainDeck
        mountainDeck = new PermitDeck(Region.MOUNTAIN);
        hillDeck = new PermitDeck(Region.HILL);
        coastDeck = new PermitDeck(Region.COAST);
    }

    public Collection<User> getUsers() {
        return usersInGame.values();
    }

    public MoneyPath getMoneyPath() {
        return moneyPath;
    }

    public VictoryPath getVictoryPath() {
        return victoryPath;
    }


    public Region getRegion(String region){
        if(regions.containsKey(region))
            return regions.get(region);
        return null;
    }

    public City getCity(City city) {
        if(cities.containsVertex(city)){
            for (City cityToSearch: cities.vertexSet()) {
                if (cityToSearch.equals(city)){
                    return cityToSearch;
                }
            }
        }
        return null;
    }

    public void setGraph(UndirectedGraph<City, DefaultEdge> graph) {
        this.graph = graph;
    }

    public UndirectedGraph<City, DefaultEdge> getGraph() {
        return graph;
    }

    public RegionBonusCard getRegionBonusCard(String region) {
        return regionBonusCard.get(region);
    }

    public ColorBonusCard getColorBonusCard(String color) {
        return colorBonusCard.get(color);
    }

    public King getKing() {
        return king;
    }

    public KingBonusCard getKingBonusCard() {
        try{
            return kingBonusCard.peek();
        } catch (EmptyStackException e){
            return null;
        }
    }

    public void popKingBonusCard(){
        try {
            kingBonusCard.pop();
        } catch (EmptyStackException e){
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "started=" + started +
                ", cities=" + cities +
                ", regions=" + regions +
                ", king=" + king +
                ", victoryPath=" + victoryPath +
                ", nobilityPath=" + nobilityPath +
                ", moneyPath=" + moneyPath +
                ", gameController=" + gameController +
                ", mountainDeck=" + mountainDeck +
                ", hillDeck=" + hillDeck +
                ", coastDeck=" + coastDeck +
                ", politicCards=" + politicCards +
                ", graph=" + graph +
                ", regionBonusCard=" + regionBonusCard +
                ", colorBonusCard=" + colorBonusCard +
                ", kingBonusCard=" + kingBonusCard +
                '}';
    }
}
