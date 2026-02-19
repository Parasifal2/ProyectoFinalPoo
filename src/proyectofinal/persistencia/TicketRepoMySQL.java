/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal.persistencia;

import proyectofinal.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketRepoMySQL {
    private final MySQL db;
    private final PersonaRepoMySQL personaRepo;
    private final DomicilioRepoMySQL domicilioRepo;

    public TicketRepoMySQL(MySQL db, PersonaRepoMySQL personaRepo, DomicilioRepoMySQL domicilioRepo) {
        this.db = db;
        this.personaRepo = personaRepo;
        this.domicilioRepo = domicilioRepo;
    }

    public boolean hayTickets() {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM tickets_v1 LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------
    // CREAR ticket nuevo
    // -------------------------
    public void guardarNuevo(Ticket t) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1) asegurar cliente
                personaRepo.upsertCliente(c, t.getCliente());

                // 2) guardar domicilio
                domicilioRepo.upsert(c, t.getDomicilio());

                // 3) ticket
                try (PreparedStatement ps = c.prepareStatement("""
                    INSERT INTO tickets_v1(id, fecha_creacion, cliente_id, domicilio_id, tipo_problema, descripcion, estado)
                    VALUES(?,?,?,?,?,?,?)
                """)) {
                    ps.setString(1, t.getId());
                    ps.setTimestamp(2, Timestamp.valueOf(t.getFechaCreacion()));
                    ps.setString(3, t.getCliente().getId());
                    ps.setString(4, t.getDomicilio().getId());
                    ps.setString(5, t.getTipoProblema());
                    ps.setString(6, t.getDescripcion());
                    ps.setString(7, t.getEstado().name());
                    ps.executeUpdate();
                }

                // 4) eventos (incluye CREACION)
                insertarEventos(c, t.getId(), t.getEventos());

                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception e) {
            throw new RuntimeException("guardarNuevo", e);
        }
    }

    // -------------------------
    // LISTAR / CARGAR
    // -------------------------
    public List<Ticket> listarCompletos() {
        List<Ticket> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id FROM tickets_v1 ORDER BY fecha_creacion DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cargarCompleto(rs.getString("id")).ifPresent(out::add);
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("listarCompletos", e);
        }
    }

    public Optional<Ticket> cargarCompleto(String ticketId) {
        // ⚠️ Para que esto compile y funcione bien, necesitas los constructores “rehidratación”
        // Ticket(String id, LocalDateTime fecha, Cliente(id..), Domicilio(id..), ...)
        try (Connection c = db.getConnection()) {

            Ticket t;

            // ticket + cliente + domicilio
            try (PreparedStatement ps = c.prepareStatement("""
                SELECT tk.id, tk.fecha_creacion, tk.tipo_problema, tk.descripcion, tk.estado,
                       p.id AS cliente_id, p.nombre, p.telefono, p.email,
                       d.id AS dom_id, d.direccion, d.ciudad, d.referencia
                  FROM tickets_v1 tk
                  JOIN personas p ON p.id = tk.cliente_id
                  JOIN domicilios d ON d.id = tk.domicilio_id
                 WHERE tk.id = ?
            """)) {
                ps.setString(1, ticketId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();

                    Cliente cl = new Cliente(
                            rs.getString("cliente_id"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("email")
                    );

                    Domicilio dom = new Domicilio(
                            rs.getString("dom_id"),
                            rs.getString("direccion"),
                            rs.getString("ciudad"),
                            rs.getString("referencia")
                    );

                    t = new Ticket(
                            rs.getString("id"),
                            rs.getTimestamp("fecha_creacion").toLocalDateTime(),
                            cl,
                            dom,
                            rs.getString("tipo_problema"),
                            rs.getString("descripcion"),
                            EstadoTicket.valueOf(rs.getString("estado"))
                    );
                }
            }

            // eventos
            try (PreparedStatement ps = c.prepareStatement("""
                SELECT id, fecha, tipo, descripcion, responsable
                  FROM eventos_ticket_v1
                 WHERE ticket_id = ?
                 ORDER BY fecha ASC, id ASC
            """)) {
                ps.setString(1, ticketId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        t.getEventos().add(new EventoTicket(
                                rs.getString("id"),
                                rs.getTimestamp("fecha").toLocalDateTime(),
                                TipoEvento.valueOf(rs.getString("tipo")),
                                rs.getString("descripcion"),
                                rs.getString("responsable")
                        ));
                    }
                }
            }

            // visitas
            try (PreparedStatement ps = c.prepareStatement("""
                SELECT id, fecha_programada, estado, resultado, motivo_reagendamiento, motivo_cancelacion, ingreso_generado
                  FROM visitas_v1
                 WHERE ticket_id = ?
                 ORDER BY fecha_programada ASC
            """)) {
                ps.setString(1, ticketId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        VisitaTecnica v = new VisitaTecnica(
                                rs.getString("id"),
                                rs.getTimestamp("fecha_programada").toLocalDateTime(),
                                EstadoVisita.valueOf(rs.getString("estado")),
                                rs.getString("resultado"),
                                rs.getString("motivo_reagendamiento"),
                                rs.getString("motivo_cancelacion"),
                                rs.getDouble("ingreso_generado")
                        );
                        t.getVisitas().add(v);

                        // técnicos por visita
                        cargarTecnicosDeVisita(c, v);
                    }
                }
            }

            return Optional.of(t);

        } catch (SQLException e) {
            throw new RuntimeException("cargarCompleto", e);
        }
    }

    // -------------------------
    // MÉTODOS “persistX” que usa TicketService
    // -------------------------
    public void persistProgramarVisita(Ticket t, VisitaTecnica v, List<EventoTicket> nuevosEventos) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                actualizarTicketBase(c, t);
                insertarVisita(c, t.getId(), v);
                insertarEventos(c, t.getId(), nuevosEventos);
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception e) {
            throw new RuntimeException("persistProgramarVisita", e);
        }
    }

    public void persistAsignarTecnico(String ticketId, String visitaId, Tecnico tecnico, List<EventoTicket> nuevosEventos) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                personaRepo.upsertTecnico(c, tecnico);
                vincularTecnicoVisita(c, visitaId, tecnico.getId());
                insertarEventos(c, ticketId, nuevosEventos);
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception e) {
            throw new RuntimeException("persistAsignarTecnico", e);
        }
    }

    public void persistReagendar(Ticket t, VisitaTecnica vActual, VisitaTecnica vNueva, List<EventoTicket> nuevosEventos) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                actualizarVisita(c, vActual);
                insertarVisita(c, t.getId(), vNueva);
                actualizarTicketBase(c, t);
                insertarEventos(c, t.getId(), nuevosEventos);
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception e) {
            throw new RuntimeException("persistReagendar", e);
        }
    }

    public void persistActualizarVisita(Ticket t, VisitaTecnica v, List<EventoTicket> nuevosEventos) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                actualizarVisita(c, v);
                insertarEventos(c, t.getId(), nuevosEventos);
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception e) {
            throw new RuntimeException("persistActualizarVisita", e);
        }
    }

    // Alias por si te quedó el typo en TicketService (Actrualizar)
    public void persistActrualizarVisita(Ticket t, VisitaTecnica v, List<EventoTicket> nuevosEventos) {
        persistActualizarVisita(t, v, nuevosEventos);
    }

    public void persistActualizarTicket(Ticket t, List<EventoTicket> nuevosEventos) {
    try (Connection c = db.getConnection()) {
        c.setAutoCommit(false);
        try {
            // Guardar cambios del domicilio (si el usuario editó dirección/ciudad/referencia)
            domicilioRepo.upsert(c, t.getDomicilio());

            // (Opcional) si algún día editas datos del cliente desde ticket:
            // personaRepo.upsertCliente(c, t.getCliente());

            // Guardar cambios del ticket (tipo, descripcion, estado)
            actualizarTicketBase(c, t);

            // Guardar evento ACTUALIZACION
            insertarEventos(c, t.getId(), nuevosEventos);

            c.commit();
        } catch (Exception ex) {
            c.rollback();
            throw ex;
        }
    } catch (Exception e) {
        throw new RuntimeException("persistActualizarTicket", e);
    }
}


    // -------------------------
    // Helpers SQL
    // -------------------------
    private void actualizarTicketBase(Connection c, Ticket t) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            UPDATE tickets_v1
               SET tipo_problema=?, descripcion=?, estado=?
             WHERE id=?
        """)) {
            ps.setString(1, t.getTipoProblema());
            ps.setString(2, t.getDescripcion());
            ps.setString(3, t.getEstado().name());
            ps.setString(4, t.getId());
            ps.executeUpdate();
        }
    }

    private void insertarVisita(Connection c, String ticketId, VisitaTecnica v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO visitas_v1(id,ticket_id,fecha_programada,estado,resultado,motivo_reagendamiento,motivo_cancelacion,ingreso_generado)
            VALUES(?,?,?,?,?,?,?,?)
        """)) {
            ps.setString(1, v.getId());
            ps.setString(2, ticketId);
            ps.setTimestamp(3, Timestamp.valueOf(v.getFechaProgramada()));
            ps.setString(4, v.getEstado().name());
            ps.setString(5, v.getResultado());
            ps.setString(6, v.getMotivoReagendamiento());
            ps.setString(7, v.getMotivoCancelacion());
            ps.setDouble(8, v.getIngresoGenerado());
            ps.executeUpdate();
        }
    }

    private void actualizarVisita(Connection c, VisitaTecnica v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            UPDATE visitas_v1
               SET fecha_programada=?, estado=?, resultado=?, motivo_reagendamiento=?, motivo_cancelacion=?, ingreso_generado=?
             WHERE id=?
        """)) {
            ps.setTimestamp(1, Timestamp.valueOf(v.getFechaProgramada()));
            ps.setString(2, v.getEstado().name());
            ps.setString(3, v.getResultado());
            ps.setString(4, v.getMotivoReagendamiento());
            ps.setString(5, v.getMotivoCancelacion());
            ps.setDouble(6, v.getIngresoGenerado());
            ps.setString(7, v.getId());
            ps.executeUpdate();
        }
    }

    private void vincularTecnicoVisita(Connection c, String visitaId, String tecnicoId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT IGNORE INTO visita_tecnicos_v1(visita_id, tecnico_id) VALUES(?,?)
        """)) {
            ps.setString(1, visitaId);
            ps.setString(2, tecnicoId);
            ps.executeUpdate();
        }
    }

    private void insertarEventos(Connection c, String ticketId, List<EventoTicket> eventos) throws SQLException {
        if (eventos == null || eventos.isEmpty()) return;
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO eventos_ticket_v1(ticket_id,tipo,descripcion,responsable,fecha)
            VALUES(?,?,?,?,?)
        """)) {
            for (EventoTicket ev : eventos) {
                ps.setString(1, ticketId);
                ps.setString(2, ev.getTipo().name());
                ps.setString(3, ev.getDescripcion());
                ps.setString(4, ev.getResponsable());
                ps.setTimestamp(5, Timestamp.valueOf(ev.getFecha()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void cargarTecnicosDeVisita(Connection c, VisitaTecnica v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
            SELECT p.id, p.nombre, p.telefono, p.email, te.especialidad
              FROM visita_tecnicos_v1 vt
              JOIN personas p ON p.id = vt.tecnico_id
              JOIN tecnicos te ON te.persona_id = p.id
             WHERE vt.visita_id = ?
             ORDER BY p.nombre
        """)) {
            ps.setString(1, v.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    v.getTecnicosAsignados().add(new Tecnico(
                            rs.getString("id"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getString("especialidad")
                    ));
                }
            }
        }
    }
}
