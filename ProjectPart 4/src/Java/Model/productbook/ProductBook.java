package Java.Model.productbook;


import Java.Model.currentMarket.CurrentMarketTracker;
import Java.Model.currentMarket.InvalidPublisherInput;
import Java.Model.currentMarket.InvalidVolumeException;
import Java.Model.price.*;
import Java.Model.tradable.InvalidTradableInputException;
import Java.Model.tradable.Quote;
import Java.Model.tradable.Tradable;
import Java.Model.tradable.TradableDTO;
import Java.Model.user.InvalidUserException;

import java.util.ArrayList;
import java.util.TreeMap;

public class ProductBook {

    private String product;
    private final ProductBookSide buySide;
    private final ProductBookSide sellSide;

    public ProductBook(String product) throws InvalidTradableInputException {
        setProduct(product);
        TreeMap<Price, ArrayList<Tradable>> buySideMap = new TreeMap<Price, ArrayList<Tradable>>(java.util.Collections.reverseOrder());
        TreeMap<Price, ArrayList<Tradable>> sellSideMap = new TreeMap<Price, ArrayList<Tradable>>();

        this.buySide = new ProductBookSide(BookSide.BUY, buySideMap);
        this.sellSide = new ProductBookSide(BookSide.SELL, sellSideMap);
    }

    private void setProduct(String product) throws InvalidTradableInputException{
        if( product == null ){
            throw new InvalidTradableInputException("Can not accept null input");
        }
        if(product.isEmpty() || product.length() > 5){
            throw new InvalidTradableInputException("Length of Product is less than 1 or greater than 5" + product);
        }
        product = product.trim();
        for (char c : product.toCharArray()){
            if(!Character.isLetter(c) && c != '.'){
                throw new InvalidTradableInputException("Product can only be letters or letters with dot. " + product);
            }
        }
        this.product = product;
    }



    public TradableDTO add(Tradable t) throws InvalidTradableInputException, InvalidPriceException, InvalidUserException, InvalidPublisherInput, InvalidVolumeException {
       // System.out.println("**ADD: " + t);
        if(t == null){
            throw new InvalidTradableInputException("Invalid Tradable");
        }
        if(t.getSide() == BookSide.BUY){
            TradableDTO add = buySide.add(t);
            this.tryTrade();
            this.updateMarket();
            return add;
        }
        TradableDTO add = sellSide.add(t);
        this.tryTrade();
        this.updateMarket();
        return add;
    }

    public TradableDTO[] add(Quote qte) throws InvalidTradableInputException, InvalidPriceException, InvalidUserException, InvalidVolumeException, InvalidPublisherInput {
        if(qte == null){
            throw new InvalidTradableInputException("Invalid Quote");

        }

        this.removeQuotesForUser(qte.getUser());
        //System.out.println("**ADD: " + qte.getQuoteSide(BookSide.BUY));
        //System.out.println("**ADD: " + qte.getQuoteSide(BookSide.SELL));
        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(BookSide.BUY));
        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(BookSide.SELL));
        this.tryTrade();
        this.updateMarket();
        TradableDTO[] dtoArray = new TradableDTO[2];
        dtoArray[0] = buyDTO;
        dtoArray[1] = sellDTO;
        return dtoArray;

    }

    public TradableDTO cancel(BookSide side, String orderId) throws InvalidTradableInputException, InvalidUserException, InvalidPriceException, InvalidVolumeException, InvalidPublisherInput {

        if(orderId == null){
            throw new InvalidTradableInputException("Invalid orderId");
        }
        TradableDTO tDTO;
        if(side == BookSide.SELL){
            tDTO = sellSide.cancel(orderId);
            this.updateMarket();
            return tDTO;
        }
        tDTO = buySide.cancel(orderId);
        this.updateMarket();
        return tDTO;
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws InvalidTradableInputException, InvalidUserException, InvalidPriceException, InvalidVolumeException, InvalidPublisherInput {
        if(userName == null){
            throw new InvalidTradableInputException("Invalid userName");
        }
        TradableDTO buyDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellDTO = sellSide.removeQuotesForUser(userName);
        TradableDTO[] dtoArray = new TradableDTO[2];
        dtoArray[0] = buyDTO;
        dtoArray[1] = sellDTO;
        this.updateMarket();
        return dtoArray;

    }

    public void tryTrade() throws InvalidPriceException, InvalidUserException{

        Price topBuy = buySide.topOfBookPrice();
        Price topSell = sellSide.topOfBookPrice();

        if(topSell == null || topBuy == null){
            return;
        }
        if(topBuy.lessThan(topSell)){
            return;
        }
        int totalToTrade = Math.max(buySide.topOfBookVolume(),sellSide.topOfBookVolume());

        while (totalToTrade >0) {
            Price topBuyPrice = buySide.topOfBookPrice();
            Price topSellPrice = sellSide.topOfBookPrice();
            if(topSellPrice == null || topBuyPrice == null || topSellPrice.greaterThan(topBuyPrice)){
                return;
            }

            int toTrade = Math.min(buySide.topOfBookVolume(), sellSide.topOfBookVolume());
            buySide.tradeOut(topBuyPrice, toTrade);
            sellSide.tradeOut(topBuyPrice, toTrade);
            totalToTrade -= toTrade;
        }

   }


    public String getTopOfBookString(BookSide side) {
        if (side == null) {
            return "Top of " + side + " book: $0.00 x 0";
        }

        Price topPrice = null;
        int topVolume = 0;

        if (side == BookSide.BUY) {
            topPrice = buySide.topOfBookPrice();
            if (topPrice != null) {
                topVolume = buySide.topOfBookVolume();
            }
        }
        else if (side == BookSide.SELL) {
            topPrice = sellSide.topOfBookPrice();
            if (topPrice != null) {
                topVolume = sellSide.topOfBookVolume();
            }
        }

        if (topPrice == null || topVolume == 0) {
            return "Top of " + side + " book: $0.00 x 0";
        }
        else {
            return "Top of " + side + " book: " + topPrice.toString() + " x " + topVolume;
        }
    }

    private void updateMarket() throws InvalidPriceException, InvalidVolumeException, InvalidPublisherInput {
        Price buyPrice = buySide.topOfBookPrice();
        Price sellPrice = sellSide.topOfBookPrice();
        int buyVolume = buySide.topOfBookVolume();
        int sellVolume = sellSide.topOfBookVolume();
       CurrentMarketTracker.getInstance().updateMarket(product,buyPrice,buyVolume,sellPrice,sellVolume);

    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------\n");
        sb.append("Product Book: ").append(product).append("\n");
        sb.append(buySide.toString()).append("\n");
        sb.append(sellSide.toString());
        sb.append("--------------------------------------------\n");
        return sb.toString();
    }
}