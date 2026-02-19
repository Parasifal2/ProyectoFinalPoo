/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import proyectofinal.persistencia.TicketRepoMySQL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketService {

    // modo memoria (para MainMenu viejo)
    private final List<Ticket> tickets;

    // modo BD (para GUI)
    private final TicketRepoMySQL repo;

    // Constructor legacy (NO rompe MainMenu)
    public TicketService() {
        this.tickets = new ArrayList<>();
        this.repo = null;
    }

    // Constructor BD (GUI)
    public TicketService(TicketRepoMySQL repo) {
        this.tickets = null;
        this.repo = repo;
    }

    private boolean dbMode() {
        return repo != null;
    }

    public Ticket crearTicket(Cliente cliente, Domicilio domicilio, String tipoProblema, String descripcion) {
        Ticket t = new Ticket(cliente, domicilio, tipoProblema, descripcion);
        if (!dbMode()) {
            tickets.add(t);
        } else {
            repo.guardarNuevo(t);
        }
        return t;
    }

    public List<Ticket> listarTickets() {
        return dbMode() ? repo.listarCompletos() : tickets;
    }

    public Optional<Ticket> buscarTicketPorId(String id) {
        return dbMode()
                ? repo.cargarCompleto(id)
                : tickets.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public VisitaTecnica programarVisita(String ticketId, LocalDateTime fecha, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));

        int evBefore = t.getEventos().size();
        VisitaTecnica v = t.programarVisita(fecha, responsable);
        List<EventoTicket> nuevosEventos = t.getEventos().subList(evBefore, t.getEventos().size());

        if (dbMode()) repo.persistProgramarVisita(t, v, nuevosEventos);

        return v;
    }

    public void agregarTecnicoAVisita(String ticketId, String visitaId, Tecnico tecnico, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);

        int evBefore = t.getEventos().size();
        t.agregarTecnicoAVisita(v, tecnico, responsable);
        List<EventoTicket> nuevosEventos = t.getEventos().subList(evBefore, t.getEventos().size());

        if (dbMode()) repo.persistAsignarTecnico(ticketId, visitaId, tecnico, nuevosEventos);
    }

    public VisitaTecnica reagendarVisita(String ticketId, String visitaId, LocalDateTime nuevaFecha, String motivo, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica vActual = encontrarVisita(t, visitaId);

        int evBefore = t.getEventos().size();
        VisitaTecnica vNueva = t.reagendarVisita(vActual, nuevaFecha, motivo, responsable);
        List<EventoTicket> nuevosEventos = t.getEventos().subList(evBefore, t.getEventos().size());

        if (dbMode()) repo.persistReagendar(t, vActual, vNueva, nuevosEventos);

        return vNueva;
    }

    public void cancelarVisita(String ticketId, String visitaId, String motivo, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);

        int evBefore = t.getEventos().size();
        t.cancelarVisita(v, motivo, responsable);
        List<EventoTicket> nuevosEventos = t.getEventos().subList(evBefore, t.getEventos().size());

        if (dbMode()) repo.persistActualizarVisita(t, v, nuevosEventos);
    }

    public void completarVisita(String ticketId, String visitaId, String resultado, double ingreso, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
        VisitaTecnica v = encontrarVisita(t, visitaId);

        int evBefore = t.getEventos().size();
        t.completarVisita(v, resultado, ingreso, responsable);
        List<EventoTicket> nuevosEventos = t.getEventos().subList(evBefore, t.getEventos().size());

        if (dbMode()) repo.persistActualizarVisita(t, v, nuevosEventos);
    }

    public void cerrarTicket(String ticketId, String responsable) {
        Ticket t = buscarTicketPorId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));

        int evBefore = t.getEventos().size();
        t.cerrarTicket(responsable);
        List<EventoTicket> nuevosEventos = t.getEventos().subList(evBefore, t.getEventos().size());

        if (dbMode()) repo.persistActualizarTicket(t, nuevosEventos);
    }

    private VisitaTecnica encontrarVisita(Ticket ticket, String visitaId) {
        return ticket.getVisitas().stream()
                .filter(v -> v.getId().equals(visitaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Visita no encontrada: " + visitaId));
    }
    
    public void actualizarTicket(String ticketId,
                             String tipoProblema,
                             String descripcion,
                             String direccion,
                             String ciudad,
                             String referencia,
                             String responsable) {

    Ticket t = buscarTicketPorId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));

    int eventosAntes = t.getEventos().size();

    // ✅ Esto agrega EventoTicket(TipoEvento.ACTUALIZACION,...)
    t.actualizarDatos(tipoProblema, descripcion, responsable);

    // Actualiza domicilio en el mismo objeto
    t.getDomicilio().setDireccion(direccion);
    t.getDomicilio().setCiudad(ciudad);
    t.getDomicilio().setReferencia(referencia);

    // ✅ Capturar solo eventos nuevos para persistirlos
    List<EventoTicket> nuevosEventos = new ArrayList<>(
            t.getEventos().subList(eventosAntes, t.getEventos().size())
    );

    if (repo != null) {
        repo.persistActualizarTicket(t, nuevosEventos);
    }
}
   
}

