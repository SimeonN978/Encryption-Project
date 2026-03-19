package Model.account;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStore {
    private final Map<String, AccountDetails> accounts = new ConcurrentHashMap<>();
    private final String FILENAME = "accounts.json";

    public AccountStore() {
        load(); // fill accounts map from JSON if it exists
    }

    // Public methods >>>
    // get the hashed password associated with the given username
    public synchronized String getPasswordHash(String username) {
        AccountDetails accountDetails = accounts.get(username);

        return accountDetails.getPasswordHash();
    }

    // add a new account to the map and json
    public synchronized void add(String username, String password, String email) {
        accounts.put(username, new AccountDetails(password, email));
        save(); // update to map -> update json
    }

    // check if username is in the json of accounts
    public synchronized boolean exists(String username){
        return accounts.containsKey(username);
    }

    // Private class methods
    // Save all accounts from map to file
    private void save(){
        JSONObject root = new JSONObject();

        for(Map.Entry<String, AccountDetails> entry: accounts.entrySet()){
            JSONObject details = new JSONObject();
            details.put("hash", entry.getValue().getPasswordHash());
            details.put("email", entry.getValue().getEmail());

            root.put(entry.getKey(), details);
        }

        try(FileWriter writer = new FileWriter(FILENAME)){
            writer.write(root.toJSONString());
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
                JSONObject details = (JSONObject) json.get(username);

                String hash = (String) details.get("hash");
                String email = (String) details.get("email");

                accounts.put(username, new AccountDetails(hash, email));
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
