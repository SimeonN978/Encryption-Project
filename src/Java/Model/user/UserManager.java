package Java.Model.user;

import Java.Model.price.InvalidPriceException;
import Java.Model.tradable.TradableDTO;

import java.util.TreeMap;

public class UserManager {


    private static UserManager instance;

    private TreeMap<String, User> managerMap;

    private UserManager(){
         this.managerMap = new TreeMap<>();
    }

    public static UserManager getInstance(){
        if(instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(String[] userIn) throws InvalidUserException, InvalidPriceException {
        managerMap.clear();
        if(userIn == null){
            throw new InvalidUserException("Null User array");
        }
        for(String u : userIn){
            if(u == null){
                throw new InvalidUserException("Invalid User");
            }
            if(u.trim().isEmpty()){
                continue;
            }
            if(!managerMap.containsKey(u)){
                managerMap.put(u, new User(u));
            }
        }
    }

    public void updateTradable(String userId, TradableDTO o) throws InvalidUserException{
        if(userId == null || o == null || !managerMap.containsKey(userId)){
            throw new InvalidUserException("Invalid User or Tradable");
        }
        managerMap.get(userId).updateTradable(o);
    }

    public User getUser(String userId) throws InvalidUserException {
        if(userId == null){
            throw new InvalidUserException("User Id is null");
        }
        if(!managerMap.containsKey(userId)){
            throw new InvalidUserException("User not in User Manager");
        }
        return managerMap.get(userId);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        for(String u : managerMap.keySet()){
            sb.append(managerMap.get(u));
            sb.append("\n");
        }
        return sb.toString();
    }


}
