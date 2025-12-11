package Java.Model.currentMarket;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrentMarketPublisher {

    private static CurrentMarketPublisher instance;
    private HashMap<String, ArrayList<CurrentMarketObserver>> filters;

    private CurrentMarketPublisher(){
        this.filters = new HashMap<>();
    }

    public static CurrentMarketPublisher getInstance(){
        if(instance == null){
            instance = new CurrentMarketPublisher();
        }
        return instance;
    }

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) throws InvalidPublisherInput{
        if(symbol == null){
            throw new InvalidPublisherInput("Null symbol not accepted");
        }
        if(!filters.containsKey(symbol)){
            filters.put(symbol, new ArrayList<>());

        }
        filters.get(symbol).add(cmo);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) throws InvalidPublisherInput {
        if(symbol == null){
            throw new InvalidPublisherInput("Null symbol not accepted");
        }
        if(!filters.containsKey(symbol)){
            return;
        }
        filters.get(symbol).remove(cmo);
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) throws InvalidPublisherInput {
        if(symbol == null){
            throw new InvalidPublisherInput("Null symbol not accepted");
        }
        if(!filters.containsKey(symbol)){
            return;
        }
        ArrayList <CurrentMarketObserver> cmoList = filters.get(symbol);
        for(CurrentMarketObserver o : cmoList){
            o.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}
