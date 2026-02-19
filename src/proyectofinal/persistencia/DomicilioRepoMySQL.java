/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal.persistencia;

import proyectofinal.Domicilio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DomicilioRepoMySQL {
    private final MySQL db;
    public DomicilioRepoMySQL(MySQL db) { this.db = db; }

    // Útil si alguna vez lo llamas fuera de una transacción
    public void upsert(Domicilio d) {
        try (Connection c = db.getConnection()) {
            upsert(c, d);
        } catch (SQLException e) {
            throw new RuntimeException("upsert domicilio", e);
        }
    }

    // ✅ Este es el importante: usa la MISMA Connection del repositorio principal
    public void upsert(Connection c, Domicilio d) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO domicilios(id,direccion,ciudad,referencia)
            VALUES(?,?,?,?)
            ON DUPLICATE KEY UPDATE
              direccion=VALUES(direccion),
              ciudad=VALUES(ciudad),
              referencia=VALUES(referencia)
        """)) {
            ps.setString(1, d.getId());
            ps.setString(2, d.getDireccion());
            ps.setString(3, d.getCiudad());
            ps.setString(4, d.getReferencia());
            ps.executeUpdate();
        }
    }
}
