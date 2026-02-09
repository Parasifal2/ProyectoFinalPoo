/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Carlo Clases
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketService {
    private final List<Ticket> tickets = new ArrayList<>();

    public Ticket crearTicket(Cliente cliente, Domicilio domicilio, String tipoProblema, String descripcion) {
        Ticket t = new Ticket(cliente, domicilio, tipoProblema, descripcion);
        tickets.add(t);
        return t;
    }

    public List<Ticket> listarTickets() {
        return tickets;
    }

    public Optional<Ticket> buscarTicketPorId(String id) {
        return tickets.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public VisitaTecnica programarVisita(String ticketId, LocalDateTime fecha, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        return t.programarVisita(fecha, responsable);
    }

    public void agregarTecnicoAVisita(String ticketId, String visitaId, Tecnico tecnico, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);
        t.agregarTecnicoAVisita(v, tecnico, responsable);
    }

    public VisitaTecnica reagendarVisita(String ticketId, String visitaId, LocalDateTime nuevaFecha, String motivo, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);
        return t.reagendarVisita(v, nuevaFecha, motivo, responsable);
    }

    public void cancelarVisita(String ticketId, String visitaId, String motivo, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);
        t.cancelarVisita(v, motivo, responsable);
    }

    public void completarVisita(String ticketId, String visitaId, String resultado, double ingreso, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);
        t.completarVisita(v, resultado, ingreso, responsable);
    }

    public void cerrarTicket(String ticketId, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        t.cerrarTicket(responsable);
    }

    private VisitaTecnica encontrarVisita(Ticket ticket, String visitaId) {
        return ticket.getVisitas().stream()
                .filter(v -> v.getId().equals(visitaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Visita no encontrada: " + visitaId));
    }
}