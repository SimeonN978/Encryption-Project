package Java.Model.tradable;

import Java.Model.productbook.BookSide;
import Java.Model.price.InvalidPriceException;
import Java.Model.price.Price;

public class QuoteSide implements Tradable {

    private  String user;
    private  String product;
    private Price price;
    private BookSide side;
    private  int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private  String id;


    public QuoteSide(String user, String product, Price price, BookSide side, int originalVolume) throws InvalidTradableInputException, InvalidPriceException {
        setUser(user);
        setProduct(product);
        setPrice(price);
        setSide(side);
        setOriginalVolume(originalVolume);
        this.remainingVolume = this.originalVolume;
        this.cancelledVolume = 0;
        this.filledVolume = 0;
        setId(this.user, this.product, this.price);
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

    private void setPrice(Price price) throws InvalidPriceException{
        if( price == null){
            throw new InvalidPriceException("Price can not be null");
        }
        this.price = price;
    }

    private  void setSide(BookSide side) throws InvalidTradableInputException {
        if(side ==null){
            throw new InvalidTradableInputException("Null BookSide");
        }
        this.side = side;
    }

    private void setOriginalVolume(int volume) throws InvalidTradableInputException {
        if(volume <= 0){
            throw new InvalidTradableInputException("Volume can not be 0 or less than 0");
        }
        if(volume >= 10000){
            throw new InvalidTradableInputException("Volume can not be greater than or equal to 10,000");
        }
        this.originalVolume = volume;
    }




    private void setId(String user, String product, Price price) throws InvalidPriceException, InvalidTradableInputException {
        if( user == null || product == null ){
            throw new InvalidTradableInputException("Invalid Java.Model.user and product");
        }
        if(price == null){
            throw new InvalidPriceException("Price can not be null");
        }
        this.id = user + product + price.toString() + System.nanoTime();
    }






    public String getId(){
        return id;
    }

    public int getRemainingVolume(){
        return remainingVolume;
    }

    public void setCancelledVolume(int newVol)  {
        if( newVol < 0 || newVol > this.originalVolume){
            System.out.println("Volume input is less than 0 or greater than original");

        }
        this.cancelledVolume = newVol;
    }

    public int getCancelledVollume(){
        return cancelledVolume;
    }

    public void setRemainingVolume(int newVol) {
        if( newVol < 0 || newVol > this.originalVolume){
            System.out.println("Volume input is less than 0 or greater than original");

        }
        this.remainingVolume = newVol;
    }

    public TradableDTO makeTradableDTO(){
        return new TradableDTO(this);
    }

    public Price getPrice(){
        return price;
    }

    public void setFilledVolume(int newVol)  {
        if( newVol < 0 || newVol > this.originalVolume){
            System.out.println("Volume input is less than 0 or greater than original");
        }
        this.filledVolume = newVol;
    }

    public int getFilledVolume(){
        return filledVolume;
    }

    public BookSide getSide(){
        return side;
    }

    public String getUser(){
        return user;
    }

    public String getProduct(){
        return product;
    }

    public int getOriginalVolume(){
        return originalVolume;
    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume,
                filledVolume, cancelledVolume, id);
    }


}
