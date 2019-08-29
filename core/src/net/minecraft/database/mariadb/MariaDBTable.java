package net.minecraft.database.mariadb;

import net.minecraft.database.Table;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class MariaDBTable implements Table {
    private final MariaDBStorage storage;
    private final String table;

    MariaDBTable(MariaDBStorage storage, String table){
        this.storage = storage;
        this.table = table;
    }

    @Override
    public void write(String key, byte[] write) {
        try {
            PreparedStatement statement = storage.connection().prepareStatement(
                    "INSERT INTO " + table + " (ONE, TWO) VALUES(?, ?) ON DUPLICATE KEY UPDATE TWO = ?");
            statement.setString(1, key);
            statement.setBytes(2, write);
            statement.setBytes(3, write);
            statement.executeUpdate();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(String key) {
        storage.sql("DELETE FROM " + table + " WHERE ONE='" + key + "'");
    }

    @Override
    public byte[] read(String key) {
        return storage.sqlQuery("SELECT * FROM " + table + " WHERE ONE='" + key + "'");
    }
}
