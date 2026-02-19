/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal.persistencia;

/**
 *
 * @author Carlo Clases
 */
import java.sql.*;

public class MySQL {
    private final String url;
    private final String user;
    private final String pass;

    public MySQL(String host, int port, String db, String user, String pass) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        this.user = user;
        this.pass = pass;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
