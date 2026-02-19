/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal.persistencia;

import proyectofinal.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaRepoMySQL {
    private final MySQL db;
    public PersonaRepoMySQL(MySQL db) { this.db = db; }

    public boolean hayPersonas() {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM personas LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ----------------------------
    // UPSERT con conexión propia
    // ----------------------------
    public void upsertCliente(Cliente cl) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                upsertCliente(c, cl);
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException("upsertCliente", e);
        }
    }

    public void upsertTecnico(Tecnico t) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                upsertTecnico(c, t);
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException("upsertTecnico", e);
        }
    }

    // ----------------------------
    // UPSERT usando MISMA Connection
    // (para transacciones en TicketRepoMySQL)
    // ----------------------------
    public void upsertCliente(Connection c, Cliente cl) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO personas(id,nombre,telefono,email,tipo)
            VALUES(?,?,?,?, 'CLIENTE')
            ON DUPLICATE KEY UPDATE
              nombre=VALUES(nombre),
              telefono=VALUES(telefono),
              email=VALUES(email),
              tipo='CLIENTE',
              activo=1                                         
        """)) {
            ps.setString(1, cl.getId());
            ps.setString(2, cl.getNombre());
            ps.setString(3, cl.getTelefono());
            ps.setString(4, cl.getEmail());
            ps.executeUpdate();
        }

        try (PreparedStatement ps = c.prepareStatement("""
            INSERT IGNORE INTO clientes(persona_id) VALUES(?)
        """)) {
            ps.setString(1, cl.getId());
            ps.executeUpdate();
        }
    }

    public void upsertTecnico(Connection c, Tecnico t) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO personas(id,nombre,telefono,email,tipo)
            VALUES(?,?,?,?, 'TECNICO')
            ON DUPLICATE KEY UPDATE
              nombre=VALUES(nombre),
              telefono=VALUES(telefono),
              email=VALUES(email),
              tipo='TECNICO',
              activo=1
        """)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getNombre());
            ps.setString(3, t.getTelefono());
            ps.setString(4, t.getEmail());
            ps.executeUpdate();
        }

        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO tecnicos(persona_id, especialidad)
            VALUES(?,?)
            ON DUPLICATE KEY UPDATE especialidad=VALUES(especialidad)
        """)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getEspecialidad());
            ps.executeUpdate();
        }
    }

    // ----------------------------
    // LISTADOS
    // ----------------------------
    public List<Cliente> listarClientes() {
        List<Cliente> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                SELECT p.id, p.nombre, p.telefono, p.email
                  FROM personas p
                  JOIN clientes cl ON cl.persona_id = p.id                                                       
                 ORDER BY p.nombre
             """);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new Cliente(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("listarClientes", e);
        }
    }

    public List<Tecnico> listarTecnicos() {
        List<Tecnico> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                SELECT p.id, p.nombre, p.telefono, p.email, t.especialidad
                  FROM personas p
                  JOIN tecnicos t ON t.persona_id = p.id                  
                 ORDER BY p.nombre
             """);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new Tecnico(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("especialidad")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("listarTecnicos", e);
        }
              
    }
    public void desactivarPersona(String personaId) {
    try (Connection c = db.getConnection();
         PreparedStatement ps = c.prepareStatement("UPDATE personas SET activo=0 WHERE id=?")) {
        ps.setString(1, personaId);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("desactivarPersona", e);
    }
}

}
