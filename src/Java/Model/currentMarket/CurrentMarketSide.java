package Java.Model.currentMarket;

import Java.Model.price.*;

public class CurrentMarketSide {


    private Price price;
    private int volume;

    public CurrentMarketSide(Price p, int volume) throws InvalidPriceException, InvalidVolumeException {
        setPrice(p);
        setVolume(volume);
    }

    private void setPrice(Price p) throws InvalidPriceException{
        if(p == null){
            throw new InvalidPriceException("Null Java.Model.price object");
        }
        this.price = p;
    }

    private void setVolume(int v) throws InvalidVolumeException{
        if(v <0){
            throw new InvalidVolumeException("Volume is less than 0");
        }
        this.volume = v;
    }

    @Override
    public String toString(){
        String output;
        output = price.toString() + "x" + volume;
        return output;
    }



}
