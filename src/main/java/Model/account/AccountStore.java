package Model.account;

import java.sql.*;

public class AccountStore {
    private static final String DB_URL = System.getenv("DB_URL"); //TODO: Issues saying Not Null
    private static final String DB_USER = "root";
    private static final String DB_PWD = System.getenv("DB_PASSWORD");

    public AccountStore(){
        System.out.println("DB_URL = " + DB_URL);
        createTableIfMissing();
    }

    public synchronized String getPasswordHash(String username){
        String sql = "SELECT password_hash FROM ACCOUNTS WHERE username = ?";

        //try to open connection
        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);

            // try to execute sql query
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getString("password_hash");
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public synchronized void add(String username, String passwordHash, String email){
        String sql = "INSERT INTO ACCOUNTS (username, password_hash, email) VALUES(?, ?, ?)";

        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            PreparedStatement stmt = conn.prepareStatement(sql)){
                
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, email);
            stmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    // check if account exists
    public synchronized boolean exists(String username){
        String sql = "SELECT 1 FROM ACCOUNTS WHERE username = ?";

        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);

            try(ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }

        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // On initiation create Accounts table if it doesnt exist
    private void createTableIfMissing(){
        String sql = """
                CREATE TABLE IF NOT EXISTS ACCOUNTS(
                    username VARCHAR(32) PRIMARY KEY,
                    password_hash VARCHAR(100) NOT NULL,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """;

        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            Statement stmt = conn.createStatement()){
                stmt.execute(sql);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
