package tradable;

import productbook.BookSide;
import price.Price;

public interface Tradable {

    String getId();
    int getRemainingVolume();
    void setCancelledVolume(int newVol);
    int getCancelledVollume();
    void setRemainingVolume(int newVol);
    TradableDTO makeTradableDTO();
    Price getPrice();
    void setFilledVolume(int newVol);
    int getFilledVolume();
    BookSide getSide();
    String getUser();
    String getProduct();
    int getOriginalVolume();

}
