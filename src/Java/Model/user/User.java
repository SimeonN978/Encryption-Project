package Java.Model.user;


import Java.Model.currentMarket.CurrentMarketObserver;
import Java.Model.currentMarket.CurrentMarketSide;
import Java.Model.price.InvalidPriceException;
import Java.Model.price.Price;
import Java.Model.price.PriceFactory;
import Java.Model.tradable.TradableDTO;

import java.util.HashMap;

public class User implements CurrentMarketObserver {

    private HashMap<String, TradableDTO> userMap = new HashMap<>();
    private HashMap<String, CurrentMarketSide[]> currentMarkets = new HashMap<>();
    private String userId;

    private Wallet wallet;


    //Create user
    public User(String userId) throws InvalidUserException, InvalidPriceException {

        setUser(userId);
        setWallet(0);
    }

    //Set Wallet value
    private void setWallet(int p) throws InvalidPriceException{
        wallet = new Wallet(PriceFactory.makePrice(p));
    }


    private void setUser(String user) throws InvalidUserException {
        if( user == null || user.trim().isEmpty()){
            throw new InvalidUserException("Can not accept null input or zero input");
        }
        user = user.trim();
//        for (char c : user.toCharArray()){
//            if(!Character.isLetter(c)){
//                throw new InvalidUserException("User can only be letters ");
//            }
//        }

        //Potentially not needed:
        //user = user.toUpperCase();
        this.userId = user;


    }


    //wallet functions
    public void addToWallet(int amount) throws InvalidPriceException {
        wallet.addMoney(amount);
    }

    public void subtractFromWallet(int amount) throws InvalidPriceException {
        wallet.subtractMoney(amount);
    }

    public Price getWalletBalance() {
        return wallet.getWalletValue();
    }

    public String getUserId(){

        return userId;
    }

    public HashMap<String, TradableDTO> getUserMap() {
        return new HashMap<>(userMap); // Returns a copy
    }

    public HashMap<String, CurrentMarketSide[]> getCurrentMarkets() {
        return new HashMap<>(currentMarkets); // Returns a copy
    }


    public void updateTradable(TradableDTO o){
        if(o == null){
            return;
        }
        if(userMap.containsKey(o.tradableId())){
            userMap.replace(o.tradableId(),o);
        }
        userMap.put(o.tradableId(),o);
    }




    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide){
        CurrentMarketSide[] marketArray = new CurrentMarketSide[2];
        marketArray[0] = buySide;
        marketArray[1] = sellSide;
        currentMarkets.put(symbol,marketArray);
    }




    public String getCurrentMarketsString(){
        StringBuilder sb = new StringBuilder();
        for(String s : currentMarkets.keySet()){
            sb.append(s).append("   ").append(currentMarkets.get(s)[0]).append(" - ").append(currentMarkets.get(s)[1]);
            sb.append("\n");
        }
        return sb.toString();
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User Id: ").append(userId).append("\n");

        for (TradableDTO t : userMap.values()) {
            sb.append(String.format("  Product: %s, Price: %s, OriginalVolume: %d, RemainingVolume: %d, CancelledVolume: %d, FilledVolume: %d, User: %s, Side: %s, Id: %s",
                    t.product(), t.price(), t.originalVolume(), t.remainingVolume(),
                    t.cancelledVolume(), t.filledVolume(), t.user(), t.side(), t.tradableId()));
            sb.append("\n");
        }
        return sb.toString();
    }
}
