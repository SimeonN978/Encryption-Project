package Java.Model.price;

import java.util.HashMap;


public abstract class PriceFactory {

    private static final HashMap <Integer, Price> priceMap= new HashMap<>();

    public static Price makePrice(int value){
        if(!PriceFactory.priceMap.containsKey(value)){
            Price p = new Price(value);
            PriceFactory.priceMap.put(value, p);
        }
        return PriceFactory.priceMap.get(value);
    }

    public static Price makePrice (String stringValueIn) throws InvalidPriceException{
        //parse string for errors and convert to int.

       checkPriceError(stringValueIn);
       int finalPrice = stringToInt(stringValueIn);
       if(!PriceFactory.priceMap.containsKey(finalPrice)){
           Price p = new Price(finalPrice);
          PriceFactory.priceMap.put(finalPrice, p);
       }
       return PriceFactory.priceMap.get(finalPrice);

    }

    private static void checkPriceError(String stringValueIn) throws InvalidPriceException{
        checkEmpty(stringValueIn);
        checkNumerics(stringValueIn);
        checkDots(stringValueIn);
        checkMinusSign(stringValueIn);
        checkDollarSign(stringValueIn);
    }

    private static void checkDots(String priceString) throws InvalidPriceException {
        int dotCount =0;
        for (char c : priceString.toCharArray()) {
            if(c == '.'){
                dotCount++;
            }
        }
        if(dotCount >1){
            throw new InvalidPriceException("Invalid Price, too many dots" + " "+ priceString);
        }
        if(dotCount == 1){
            String postDots = priceString.substring(priceString.indexOf("."), priceString.length()-1);
            if(postDots.length()> 2){
                throw new InvalidPriceException("Invalid Price, too many decimals" + " "+ priceString);
            }
            else if(postDots.length() < 2 && priceString.indexOf(".") != priceString.length() -1){
                throw new InvalidPriceException("Invalid Price, too few decimals" + " " +priceString);
            }

        }
    }

    private static void checkNumerics(String priceString) throws InvalidPriceException{
        for (char c : priceString.toCharArray()) { //parse the string and do some checks on character amounts.
            if(!Character.isDigit(c) && c != '$' && c != '.' && c !=',' && c!= '-'){ //Check for non-numeric characters, make sure dots, dollar signs, and minus are not included.
                throw new InvalidPriceException("Invalid Input, non-numeric characters are not allowed" + " "+ priceString);
            }
        }

    }

    private static void checkMinusSign(String priceString) throws InvalidPriceException{
        if(priceString.contains("-")){
            if ( priceString.indexOf("-") >1 ) {
                throw new InvalidPriceException("Invalid Price, improper use of - sign"+ " "+ priceString);
            }
        }
    }


    private static void checkDollarSign(String priceString) throws InvalidPriceException{
        int dollarSignCount = 0;
        for (char c : priceString.toCharArray()) {
            if(c == '$'){
                dollarSignCount++;
            }
        }
        if(dollarSignCount ==1 && priceString.charAt(0) != '$'){
            throw new InvalidPriceException("Invalid Price, improper use of $ sign"+ " "+ priceString);
        }
    }


    private static void checkEmpty(String priceString) throws InvalidPriceException{
        if(priceString.trim().isEmpty()){
            throw new InvalidPriceException("Price can not be an empty value.");
        }
    }

    private static int stringToInt(String priceString){
        String finalString = priceString.replaceAll("[$.,]","");
        int finalInt =  Integer.parseInt(finalString);
        if(  (!priceString.contains(".") || (priceString.indexOf(".") == priceString.length() -1)) ){
            return  finalInt * 100;
        }
        return  finalInt;
    }

}

