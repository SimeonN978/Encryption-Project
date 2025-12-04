package tradable;

import price.*;
import productbook.BookSide;

public class Quote {

    private  String user;
    private  String product;
    private QuoteSide buySide;
    private  QuoteSide sellSide;


    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName ) throws InvalidPriceException, InvalidTradableInputException {
        setProduct(symbol);
        setUser(userName);
        setBuyside(this.user, this.product, buyPrice, buyVolume, BookSide.BUY);
        setSellSide(this.user, this.product, sellPrice, sellVolume, BookSide.SELL);
    }


    private void setProduct(String product) throws InvalidTradableInputException {
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

    private void setUser(String user) throws InvalidTradableInputException {
        if( user == null || user.trim().isEmpty()){
            throw new InvalidTradableInputException("Can not accept null input or zero input");
        }
        if(user.length() != 3){
            throw new InvalidTradableInputException("Length of User is not equal to three " + user);
        }
        user = user.trim();
        for (char c : user.toCharArray()){
            if(!Character.isLetter(c)){
                throw new InvalidTradableInputException("User can only be letters ");
            }
        }
        user = user.toUpperCase();
        this.user = user;
    }

    private void setBuyside(String userName, String symbol, Price buyPrice, int buyVolume, BookSide side) throws InvalidPriceException, InvalidTradableInputException {
        if(userName == null || symbol == null || buyVolume ==0){
            throw new InvalidTradableInputException("Null inputs provided");
        }
        if (buyPrice == null){
            throw new InvalidPriceException("Invalid Price Value");
        }
        if( side == BookSide.SELL || side == null){
            throw new InvalidTradableInputException("Invalid side provided");
        }
        this.buySide = new QuoteSide(userName, symbol, buyPrice, BookSide.BUY, buyVolume);
    }

    private void setSellSide(String userName, String symbol, Price sellPrice, int sellVolume, BookSide side) throws InvalidPriceException, InvalidTradableInputException {
        if(userName == null || symbol == null || sellVolume ==0){
            throw new InvalidTradableInputException("Null inputs provided");
        }
        if (sellPrice == null){
            throw new InvalidPriceException("Invalid Price Value");
        }
        if( side == BookSide.BUY || side == null){
            throw new InvalidTradableInputException("Invalid side provided");
        }
        this.sellSide = new QuoteSide(userName, symbol, sellPrice,BookSide.SELL, sellVolume );
    }

    public QuoteSide getQuoteSide(BookSide sideIn) throws InvalidTradableInputException {
        if (sideIn == null){
            throw new InvalidTradableInputException("Invalid side Input");
        }
        if(sideIn == BookSide.BUY){
            return this.buySide;
        }
        return this.sellSide;
    }

    public String getSymbol(){
        return this.product;
    }

    public String getUser(){
        return this.user;
    }


}
