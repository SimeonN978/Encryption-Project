package Java.Model.currentMarket;

import Java.Model.price.InvalidPriceException;
import Java.Model.price.Price;
import Java.Model.price.PriceFactory;

public class CurrentMarketTracker {

    private static CurrentMarketTracker instance;

    private CurrentMarketTracker(){

    }

    public static CurrentMarketTracker getInstance(){
        if(instance == null){
            instance = new CurrentMarketTracker();
        }
        return instance;
    }


    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidPriceException, InvalidVolumeException, InvalidPublisherInput {

        Price marketWidth;
        if(buyPrice ==null || sellPrice == null){
            if(buyPrice == null){
                buyPrice = PriceFactory.makePrice(0);
            }
            if(sellPrice == null){
                sellPrice = PriceFactory.makePrice(0);
            }
            marketWidth = PriceFactory.makePrice(0);
        }
        else {
            marketWidth = sellPrice.subtract(buyPrice);

        }
        CurrentMarketSide buySide = new CurrentMarketSide(buyPrice,buyVolume);
        CurrentMarketSide sellSide = new CurrentMarketSide(sellPrice,sellVolume);
        StringBuilder sb = new StringBuilder();
        sb.append("*********** Current Market ***********");
        sb.append(String.format("\n  %s %s - %s Market width:[%s]", symbol, buySide, sellSide, marketWidth));
        sb.append("\n  ************************************** ");
        System.out.println(sb);
        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }

}
