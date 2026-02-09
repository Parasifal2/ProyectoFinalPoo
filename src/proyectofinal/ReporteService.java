/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Carlo Clases
 */
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

public class ReporteService {

    public long totalTickets(List<Ticket> tickets) {
        return tickets.size();
    }

    public long totalVisitas(List<Ticket> tickets) {
        return tickets.stream().mapToLong(t -> t.getVisitas().size()).sum();
    }

    public Map<String, Long> ticketsReagendadosConteo(List<Ticket> tickets) {
        return tickets.stream().collect(Collectors.toMap(
                Ticket::getId,
                t -> t.getVisitas().stream().filter(v -> v.getEstado() == EstadoVisita.REAGENDADA).count()
        ));
    }

    public Map<String, Long> cancelacionesPorMotivo(List<Ticket> tickets) {
        return tickets.stream()
                .flatMap(t -> t.getVisitas().stream())
                .filter(v -> v.getEstado() == EstadoVisita.CANCELADA)
                .collect(Collectors.groupingBy(
                        v -> Optional.ofNullable(v.getMotivoCancelacion()).orElse("Sin motivo"),
                        Collectors.counting()
                ));
    }

    public double ingresosEnRango(List<Ticket> tickets, LocalDate desde, LocalDate hasta) {
        return tickets.stream()
                .flatMap(t -> t.getVisitas().stream())
                .filter(v -> v.getEstado() == EstadoVisita.COMPLETADA)
                .filter(v -> !v.getFechaProgramada().toLocalDate().isBefore(desde) &&
                             !v.getFechaProgramada().toLocalDate().isAfter(hasta))
                .mapToDouble(VisitaTecnica::getIngresoGenerado)
                .sum();
    }
}
