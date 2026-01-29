package Java.Model.user;

import Java.Model.price.InvalidPriceException;
import Java.Model.price.Price;
import Java.Model.price.PriceFactory;


//Wallet class that represents value of money held in an account. Separate from user to maintain single responsibility.
 public class Wallet {


    private Price value;


     Wallet(Price p) throws InvalidPriceException{

        value = PriceFactory.makePrice(p.toString());
    }


    public void addMoney(int value) throws InvalidPriceException {

        if(value<=0){
            throw new InvalidPriceException("Can't add negative price");
        }
        Price add = PriceFactory.makePrice(value);
        this.value = this.value.add(add);

    }

    public void subtractMoney(int deduct) throws InvalidPriceException{

        if(deduct <=0 ){
            throw new InvalidPriceException("value to deduct cant be negative or 0");

        }
        Price sub = PriceFactory.makePrice(deduct);
        if(sub.greaterThan(value)){
            throw new InvalidPriceException("value to deduct can not be greater than wallet's value");
        }
        this.value = value.subtract(sub);


    }

    public Price getWalletValue(){
         return value;
     }

}
