/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal.persistencia;

import java.sql.*;

public class TestMySQL {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/solar_swing?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "admin";
        String pass = "P@ssw0rd";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Conexión OK");

            // Inserta 1 ticket de prueba
            String insert = """
                INSERT INTO tickets(code, site_id, customer, issue, status, priority)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, "T-0001");
                ps.setString(2, "SITE-123");
                ps.setString(3, "John Doe");
                ps.setString(4, "CT reversed polarity");
                ps.setString(5, "OPEN");
                ps.setString(6, "HIGH");
                ps.executeUpdate();
            }

            // Lista tickets
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, code, status, priority FROM tickets ORDER BY id DESC")) {

                System.out.println("Tickets:");
                while (rs.next()) {
                    System.out.println(
                        rs.getLong("id") + " | " +
                        rs.getString("code") + " | " +
                        rs.getString("status") + " | " +
                        rs.getString("priority")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
