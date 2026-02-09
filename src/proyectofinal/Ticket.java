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
import java.util.UUID;

public class Ticket {
    private final String id;
    private final LocalDateTime fechaCreacion;

    private final Cliente cliente;       // 1
    private final Domicilio domicilio;   // 1

    private String tipoProblema;
    private String descripcion;
    private EstadoTicket estado;

    // Composición: el Ticket "posee" visitas y eventos
    private final List<VisitaTecnica> visitas = new ArrayList<>();
    private final List<EventoTicket> eventos = new ArrayList<>();

    public Ticket(Cliente cliente, Domicilio domicilio, String tipoProblema, String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.fechaCreacion = LocalDateTime.now();
        this.cliente = cliente;
        this.domicilio = domicilio;
        this.tipoProblema = tipoProblema;
        this.descripcion = descripcion;
        this.estado = EstadoTicket.ABIERTO;

        agregarEvento(TipoEvento.CREACION, "Ticket creado", "Sistema");
    }

    public String getId() { return id; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public Cliente getCliente() { return cliente; }
    public Domicilio getDomicilio() { return domicilio; }

    public String getTipoProblema() { return tipoProblema; }
    public String getDescripcion() { return descripcion; }
    public EstadoTicket getEstado() { return estado; }

    public List<VisitaTecnica> getVisitas() { return visitas; }
    public List<EventoTicket> getEventos() { return eventos; }

    public VisitaTecnica programarVisita(LocalDateTime fecha, String responsable) {
        VisitaTecnica visita = new VisitaTecnica(fecha);
        visitas.add(visita);

        if (estado == EstadoTicket.ABIERTO) estado = EstadoTicket.EN_PROCESO;

        agregarEvento(TipoEvento.PROGRAMACION, "Visita programada para " + fecha, responsable);
        return visita;
    }

    public void agregarTecnicoAVisita(VisitaTecnica visita, Tecnico tecnico, String responsable) {
        visita.agregarTecnico(tecnico);
        agregarEvento(TipoEvento.ASIGNACION, "Técnico agregado a visita: " + tecnico.getNombre(), responsable);
    }

    public VisitaTecnica reagendarVisita(VisitaTecnica visitaActual, LocalDateTime nuevaFecha, String motivo, String responsable) {
        visitaActual.setEstado(EstadoVisita.REAGENDADA);
        visitaActual.setMotivoReagendamiento(motivo);

        agregarEvento(TipoEvento.REAGENDAMIENTO, "Visita reagendada. Motivo: " + motivo, responsable);

        // crea una nueva visita (historial)
        return programarVisita(nuevaFecha, responsable);
    }

    public void cancelarVisita(VisitaTecnica visita, String motivo, String responsable) {
        visita.setEstado(EstadoVisita.CANCELADA);
        visita.setMotivoCancelacion(motivo);
        agregarEvento(TipoEvento.CANCELACION, "Visita cancelada. Motivo: " + motivo, responsable);
    }

    public void completarVisita(VisitaTecnica visita, String resultado, double ingreso, String responsable) {
        visita.setEstado(EstadoVisita.COMPLETADA);
        visita.setResultado(resultado);
        visita.setIngresoGenerado(ingreso);
        agregarEvento(TipoEvento.CIERRE, "Visita completada. Resultado: " + resultado + " | Ingreso: " + ingreso, responsable);
    }

    public void cerrarTicket(String responsable) {
        this.estado = EstadoTicket.CERRADO;
        agregarEvento(TipoEvento.CIERRE, "Ticket cerrado", responsable);
    }

    private void agregarEvento(TipoEvento tipo, String descripcion, String responsable) {
        eventos.add(new EventoTicket(tipo, descripcion, responsable));
    }
    
    
    
   public void actualizarDatos(String tipoProblema, String descripcion, String responsable) {
    this.tipoProblema = tipoProblema;
    this.descripcion = descripcion;
    agregarEvento(TipoEvento.ACTUALIZACION, "Ticket actualizado", responsable);
}
   

    @Override
    public String toString() {
        return "Ticket{id=" + id + ", estado=" + estado + ", cliente=" + cliente.getNombre() + "}";
    }
}