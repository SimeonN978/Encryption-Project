package Java.Model.price;

import java.util.Objects;

 public class Price implements Comparable <Price> {

    private final int cents;



     Price(int cents){
        this.cents = cents ;

    }

    public boolean isNegative(){

        return cents < 0;
    }

    public boolean greaterThan(Price p) throws InvalidPriceException{
        if(p == null){
            throw new InvalidPriceException("Can not compare Price with an invalid input");
        }
        return cents > p.cents;
    }

    public boolean lessThan(Price p) throws InvalidPriceException{

        if(p == null){
            throw new InvalidPriceException("Can not compare Price with an invalid input");
        }

        return cents < p.cents;
    }


    public boolean greaterOrEqual(Price p) throws InvalidPriceException{

        if(p == null){
            throw new InvalidPriceException("Can not compare Price with an invalid input");
        }
        return cents >= p.cents;
    }

    public boolean lessOrEqual(Price p) throws InvalidPriceException{

        if(p == null){
            throw new InvalidPriceException("Can not compare Price with an invalid input");
        }

        return cents <= p.cents;
    }

    public Price add(Price p) throws InvalidPriceException {

        if(p == null){
            throw new InvalidPriceException("Can not add Price with an invalid input");
        }
        //Check "p" for null -if so throw InvalidPriceError.

        int value = cents + p.cents;
        return new Price(value);

    }
    public Price subtract (Price p) throws InvalidPriceException {
        if(p == null){
            throw new InvalidPriceException("Can not subtract Price with an invalid input");
        }

        int value = cents - p.cents;
        return new Price(value);
    }

    public Price multiply (int n) {


        int value = cents * n;
        return new Price(value);
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return cents == price.cents;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cents);
    }

    @Override //override because it is an interface method. Can not throw and exception because it would override the compare able interface.
    public int compareTo(Price o){
        if( o==null){
            return -1; //current object is always greater than null
        }
        return this.cents - o.cents;
    }

    @Override
    public String toString(){
        int plainCents = Math.abs(cents);
        String notInteger = String.format("%03d", plainCents);
        String centCharacters = notInteger.substring(notInteger.length() - 2);
        String wholeValues = notInteger.substring(0, notInteger.length() - 2);
        int wholeValue = Integer.parseInt(wholeValues);
        String withCommas = String.format("%,d", wholeValue);
        String sign = "";
        if(cents <0){
            sign = "-";
        }
        return  "$" + sign + withCommas + "." + centCharacters;


    }



}
