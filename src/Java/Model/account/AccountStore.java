package Java.Model.account;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStore {
    private static AccountStore instance;
    private static Map<String, String> accounts;

    private final String FILENAME = "accounts.json";

    private AccountStore() {
        accounts = new ConcurrentHashMap<>();
        load();
    }

    // Singleton (accountStore)
    public static AccountStore getInstance() {
        if(instance == null){
            instance = new AccountStore();
        }

        return instance;
    }

    // Public methods >>>
    // get the hashed password associated with the given username
    public synchronized String getPasswordHash(String username) {
        return accounts.get(username);
    }

    // add a new account to the map and json
    public synchronized void add(String username, String password) {
        accounts.put(username, password);
        save(); // update to map -> update json
    }

    // check if username is in the json of accounts
    public synchronized boolean exists(String username){
        return accounts.containsKey(username);
    }

    // Private class methods
    // Save all accounts from map to file
    private void save(){
        JSONObject json = new JSONObject();
        json.putAll(accounts);

        try(FileWriter writer = new FileWriter(FILENAME)){
            writer.write(json.toJSONString());
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Load all accounts from file into the map
    private void load(){
        try(FileReader reader = new FileReader(FILENAME)){
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(reader);

            for(Object key : json.keySet()){
                String username = (String) key;
                String hash = (String) json.get(key);
                accounts.put(username, hash);
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse accounts.json");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("accounts.json not found, starting empty");
            e.printStackTrace();
        }
    }
}
