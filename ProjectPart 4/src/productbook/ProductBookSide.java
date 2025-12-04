package productbook;

import price.*;
import tradable.InvalidTradableInputException;
import tradable.Tradable;
import tradable.TradableDTO;
import user.InvalidUserException;
import user.UserManager;

import java.util.ArrayList;
import java.util.TreeMap;


public class ProductBookSide {

    private BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide side, TreeMap<Price, ArrayList<Tradable>> bookEntries) throws InvalidTradableInputException {
        setSide(side);

        if (bookEntries == null) {

            if (side == BookSide.BUY) {
                this.bookEntries = new TreeMap<>(java.util.Collections.reverseOrder());
            } else {
                this.bookEntries = new TreeMap<>();
            }
        } else {

            this.bookEntries = bookEntries;
        }
    }



    private void setSide(BookSide side) throws InvalidTradableInputException {
        if (side ==null ) {
            throw new InvalidTradableInputException("Please enter a valid side");
        }
        this.side = side;
    }

    public TradableDTO add(Tradable o) throws InvalidTradableInputException, InvalidUserException {
        if (o == null) {
            throw new InvalidTradableInputException("Invalid null tradable");
        }
        if (!bookEntries.containsKey(o.getPrice())) {
            ArrayList<Tradable> tradables = new ArrayList<>();
            bookEntries.put(o.getPrice(), tradables);
        }
        bookEntries.get(o.getPrice()).add(o);
        TradableDTO tDTO = o.makeTradableDTO();
        UserManager.getInstance().updateTradable(o.getUser(),tDTO);
        return tDTO;

    }

    public TradableDTO cancel(String tradableId) throws InvalidTradableInputException, InvalidUserException {
        if (tradableId == null) {
            throw new InvalidTradableInputException("Invalid tradableId");
        }
        for (Price p : bookEntries.keySet()) {
            for (Tradable t : bookEntries.get(p)) {
                if (t.getId().equals(tradableId)) {
                    System.out.println("**CANCEL: " + t);
                    bookEntries.get(p).remove(t);
                    int remainingBeforeCancel = t.getRemainingVolume();
                    t.setCancelledVolume(t.getCancelledVollume() + remainingBeforeCancel);
                    t.setRemainingVolume(0);
                    if (bookEntries.get(p).isEmpty()) {
                        bookEntries.remove( p);
                    }
                    TradableDTO tDTO = t.makeTradableDTO();
                    UserManager.getInstance().updateTradable(t.getUser(),tDTO);
                    return tDTO;

                }

            }
        }
        return null;

    }



    public TradableDTO removeQuotesForUser(String userName) throws InvalidTradableInputException, InvalidUserException{
        if (userName == null) {
            throw new InvalidTradableInputException("Invalid tradableId");
        }
        for (Price p : bookEntries.keySet()) {
            for (Tradable t : bookEntries.get(p)) {
                if (t.getUser().equals(userName)) {
                    UserManager.getInstance().updateTradable(t.getUser(),t.makeTradableDTO());
                    TradableDTO tDTO =this.cancel(t.getId());
                    return tDTO;

                }

            }
        }
        return null;
    }

    public Price topOfBookPrice() {
        if ( bookEntries.isEmpty()){
            return null;
        }

        return bookEntries.firstKey();
    }

    public int topOfBookVolume() {
        Price topPrice = this.topOfBookPrice();
        if (topPrice == null) {
            return 0;
        }

        ArrayList<Tradable> tradables = bookEntries.get(topPrice);
        if (tradables == null || tradables.isEmpty()) {
            return 0;
        }

        int totalVolume = 0;
        for (Tradable t : tradables) {
            totalVolume += t.getRemainingVolume();
        }
        return totalVolume;
    }

    public void tradeOut(Price price, int volToTrade) throws InvalidPriceException, InvalidUserException {

        if (price == null || volToTrade <= 0) {
            return;
        }

        Price topPrice = topOfBookPrice();
        if (topPrice == null || topPrice.greaterThan(price)) {
            return;
        }

        ArrayList<Tradable> tradablesAtPrice = bookEntries.get(topPrice);
        if (tradablesAtPrice == null || tradablesAtPrice.isEmpty()) {
            return;
        }

        int totalVol = this.topOfBookVolume();

        if(volToTrade >= totalVol){
            for(Tradable t : tradablesAtPrice){
                int rv = t.getRemainingVolume();
                t.setFilledVolume(t.getOriginalVolume() );
                t.setRemainingVolume(0);
                System.out.println("Full Fill: " + t);
                UserManager.getInstance().updateTradable(t.getUser(),t.makeTradableDTO()); //check these lines out later
            }
            bookEntries.remove(topPrice);

            return;
        }
        int remainder = volToTrade;
        for(Tradable t : tradablesAtPrice) {

            double ratio = (double) t.getRemainingVolume()/totalVol;
            int toTrade = (int) Math.ceil(ratio * volToTrade);
            toTrade = Math.min(toTrade, remainder);
            t.setFilledVolume(t.getFilledVolume() + toTrade);
            t.setRemainingVolume(t.getRemainingVolume() - toTrade);
            System.out.println("Partial Fill: " + t);
            remainder = remainder - toTrade;
            UserManager.getInstance().updateTradable(t.getUser(),t.makeTradableDTO());

        }
        return;


    }





    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Side: ").append(side.toString()).append("\n");

        if (bookEntries.isEmpty()) {
            sb.append("\t<Empty>\n");
        } else {
            for (Price p : bookEntries.keySet()) {
                Price price = p;
                ArrayList<Tradable> tradables = bookEntries.get(price);

                sb.append("\t").append(price.toString()).append(":\n");
                for (Tradable t : tradables) {
                    sb.append("\t\t").append(t.toString()).append("\n");
                }
            }
        }
        return sb.toString();
    }
}