package Model.account;

public class AccountDetails {
    private String hash;
    private String email;

    public AccountDetails(String hash, String email){
        this.hash = hash;
        this.email = email;
    }

    public String getEmail(){return email;}
    public String getPasswordHash(){return hash;}
}