package org.maxgamer.quickshop.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.jetbrains.annotations.*;

public class MySQLCore implements DatabaseCore {
    private String url;
    /** The connection properties... user, pass, autoReconnect.. */
    private Properties info;
    private static final int MAX_CONNECTIONS = 8;
    private static ArrayList<Connection> pool = new ArrayList<>();

    public MySQLCore(@NotNull String host, @NotNull String user, @NotNull String pass, @NotNull String database, @NotNull String port, boolean useSSL) {
        info = new Properties();
        info.setProperty("autoReconnect", "true");
        info.setProperty("user", user);
        info.setProperty("password", pass);
        info.setProperty("useUnicode", "true");
        info.setProperty("characterEncoding", "utf8");
        info.setProperty("useSSL", String.valueOf(useSSL));
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        for (int i = 0; i < MAX_CONNECTIONS; i++)
            pool.add(null);
    }

    /**
     * Gets the database connection for executing queries on.
     *
     * @return The database connection
     */
    public Connection getConnection() {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            Connection connection = pool.get(i);
            try {
                // If we have a current connection, fetch it
                if (connection != null && !connection.isClosed()) {
                    if (connection.isValid(10)) {
                        return connection;
                    }
                    // Else, it is invalid, so we return another connection.
                }
                connection = DriverManager.getConnection(this.url, info);
                pool.set(i, connection);
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void queue(@NotNull BufferStatement bs) {
        try {
            Connection con = this.getConnection();
            while (con == null) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    //ignore
                }
                // Try again
                con = this.getConnection();
            }
            PreparedStatement ps = bs.prepareStatement(con);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        // Nothing, because queries are executed immediately for MySQL
    }

    @Override
    public void flush() {
        // Nothing, because queries are executed immediately for MySQL
    }
}