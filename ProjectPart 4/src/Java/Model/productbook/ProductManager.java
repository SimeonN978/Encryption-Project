package Java.Model.productbook;

import Java.Model.currentMarket.InvalidPublisherInput;
import Java.Model.currentMarket.InvalidVolumeException;
import Java.Model.price.InvalidPriceException;
import Java.Model.tradable.*;
import Java.Model.user.InvalidUserException;
import Java.Model.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ProductManager {



    private static ProductManager instance;
    private HashMap<String, ProductBook> productMap;

    private ProductManager(){
        productMap = new HashMap<>();
    }

    public static ProductManager getInstance(){
        if(instance == null){
            instance = new ProductManager();
        }
        return instance;
    }

    public void addProduct(String symbol) throws InvalidTradableInputException {
        if(symbol == null || !setSymbol(symbol)){
            throw new InvalidTradableInputException("Invalid symbol");
        }
        ProductBook book = new ProductBook(symbol);
        productMap.put(symbol.toUpperCase(), book);

    }

    private boolean setSymbol(String product) throws InvalidTradableInputException{
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
        return true;
    }

    public ProductBook getProductBook(String symbol) throws InvalidTradableInputException{
        if(symbol == null || !setSymbol(symbol)){
            throw new InvalidTradableInputException("Product does not exist");
        }
        return productMap.get(symbol);
    }

    public String getRandomProduct() throws InvalidProductBookException{
        if(productMap.isEmpty()){
            throw new InvalidProductBookException("No products collection");
        }
        return randomProduct(productMap);
    }

    private String randomProduct(HashMap<String, ProductBook> map){
        ArrayList <String> stringList = new ArrayList<>(productMap.keySet());
        Random random = new Random();
        int index = random.nextInt(stringList.size());
        return stringList.get(index);
    }

    public TradableDTO addTradable(Tradable o) throws InvalidTradableInputException, InvalidPriceException, InvalidUserException, InvalidPublisherInput, InvalidVolumeException {
        if(o == null){
            throw new InvalidTradableInputException("Null tradableDTO");
        }
        String product = o.getProduct();
        if(!productMap.containsKey(product)){
            throw new InvalidTradableInputException("No product in map");
        }
        TradableDTO tDTO = productMap.get(product).add(o);
        UserManager.getInstance().updateTradable(o.getUser(), tDTO);
        return tDTO;
    }

    public TradableDTO[] addQuote(Quote q) throws InvalidQuoteException, InvalidPriceException, InvalidUserException, InvalidTradableInputException, InvalidPublisherInput, InvalidVolumeException {
        if(q == null){
            throw new InvalidQuoteException("Invalid quote");
        }
        ProductBook book = productMap.get(q.getSymbol());
        book.removeQuotesForUser(q.getUser());
        TradableDTO[] tDTO = new TradableDTO[2];
        TradableDTO buyTradable = addTradable(q.getQuoteSide(BookSide.BUY));
        TradableDTO sellTradable = addTradable(q.getQuoteSide(BookSide.SELL));
        tDTO[0] = buyTradable;
        tDTO[1] = sellTradable;
        return tDTO;

    }

    public TradableDTO cancel(TradableDTO o) throws InvalidTradableInputException, InvalidUserException, InvalidPriceException, InvalidPublisherInput, InvalidVolumeException {
        if(o == null){
            throw new InvalidTradableInputException("Null tradableDTO");
        }
        String product = o.product();
        if(!productMap.containsKey(product)){
            throw new InvalidTradableInputException("No product in map");
        }
        ProductBook book = productMap.get(product);
        TradableDTO tDTO;
        tDTO = book.cancel(o.side(),o.tradableId());
        if(tDTO == null){
            System.out.println("Failed to cancel");
            return null;
        }
        return tDTO;
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws InvalidTradableInputException, InvalidUserException, InvalidPriceException, InvalidPublisherInput, InvalidVolumeException {
        if (symbol == null || user == null) {
            throw new InvalidTradableInputException("Invlaid symbol or Java.Model.user");
        }
        if (!productMap.containsKey(symbol)) {
            throw new InvalidTradableInputException("No product in map");
        }
        ProductBook book = productMap.get(symbol);
        return book.removeQuotesForUser(user);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(ProductBook b : productMap.values()){
            sb.append(b);
            sb.append("\n");
        }
        return sb.toString();
    }



}
