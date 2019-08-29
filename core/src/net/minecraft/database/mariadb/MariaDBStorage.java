package net.minecraft.database.mariadb;

import net.minecraft.database.Storage;
import net.minecraft.database.Table;
import net.minecraft.logging.Log;

import java.sql.*;

public class MariaDBStorage implements Storage {
    private Connection connection;
    private Statement statement;
    private final String url;

    public MariaDBStorage(String ip, int port, String user, String pass, String database){
        this.url = "jdbc:mariadb://" + ip + ":" + port + "/" +
                database + "?user=" + user + "&password=" + pass;
        try{
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
        }catch (SQLException ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }catch (ClassNotFoundException ex){
            Log.MAIN.error("JDBC driver not found");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Table get(String name) {
        return new MariaDBTable(this, name);
    }

    @Override
    public Table create(String name) {
        sql("CREATE TABLE " + name + "(ONE VARCHAR(16) NOT NULL, TWO MEDIUMBLOB NOT NULL, PRIMARY KEY(ONE))");
        sql("CREATE INDEX ONE ON " + name + " (ONE)");
        return new MariaDBTable(this, name);
    }

    @Override
    public void remove(String name) {
        sql("DROP TABLE " + name);
    }

    @Override
    public void close() {
        try{
            connection.close();
            statement.close();
        }catch (SQLException ex){
            //This errors not fatal
        }
    }

    boolean sql(String sql){
        try{
            getStatement();
            return statement.execute(sql);
        }catch (SQLException ex){
            return false;
        }
    }

    byte[] sqlQuery(String sql){
        try{
            getStatement();
            ResultSet set = statement.executeQuery(sql);
            if(!set.next())return null;
            Blob blob = set.getBlob("TWO");
            byte array[] = blob.getBytes(1, (int)blob.length());
            set.close();
            return array;
        }catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
    }

    Connection connection(){
        return connection;
    }

    private void getStatement() throws SQLException{
        if(connection.isClosed())
            connection = DriverManager.getConnection(url);
        if(statement.isClosed())
            statement = connection.createStatement();
    }
}
