/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Carlo Clases
 */
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final Scanner sc = new Scanner(System.in);

    private static final TicketService ticketService = new TicketService();
    private static final ReporteService reporteService = new ReporteService();

    // Clientes y técnicos en memoria (ArrayList)
    private static final List<Cliente> clientes = new ArrayList<>();
    private static final List<Tecnico> tecnicos = new ArrayList<>();

    public static void main(String[] args) {
        seedData();

        int op;
        do {
            menu();
            op = leerInt("Opcion: ");

            try {
                switch (op) {
                    case 1 -> registrarCliente();
                    case 2 -> registrarTecnico();
                    case 3 -> crearTicket();
                    case 4 -> listarTickets();
                    case 5 -> programarVisita();
                    case 6 -> agregarTecnicoAVisita();
                    case 7 -> reagendarVisita();
                    case 8 -> cancelarVisita();
                    case 9 -> completarVisita();
                    case 10 -> cerrarTicket();
                    case 11 -> menuReportes();
                    case 0 -> System.out.println("Saliendo... ✅");
                    default -> System.out.println("Opción invalida.");
                }
            } catch (Exception e) {
                System.out.println("⚠ Error: " + e.getMessage());
            }

            System.out.println();
        } while (op != 0);
    }

    private static void menu() {
        System.out.println("=====================================");
        System.out.println("  SISTEMA TICKETS - ELEVATION SOLAR");
        System.out.println("=====================================");
        System.out.println("1) Registrar cliente");
        System.out.println("2) Registrar tecnico");
        System.out.println("3) Crear ticket");
        System.out.println("4) Listar tickets");
        System.out.println("5) Programar visita");
        System.out.println("6) Agregar tecnico a visita (1..*)");
        System.out.println("7) Reagendar visita");
        System.out.println("8) Cancelar visita");
        System.out.println("9) Completar visita");
        System.out.println("10) Cerrar ticket");
        System.out.println("11) Reportes");
        System.out.println("0) Salir");
    }

    // ----------- REGISTROS -----------

    private static void registrarCliente() {
        String nombre = leerTexto("Nombre cliente: ");
        String telefono = leerTexto("Telefono: ");
        String email = leerTexto("Email: ");
        clientes.add(new Cliente(nombre, telefono, email));
        System.out.println("✅ Cliente registrado.");
    }

    private static void registrarTecnico() {
        String nombre = leerTexto("Nombre tecnico: ");
        String telefono = leerTexto("Telefono: ");
        String email = leerTexto("Email: ");
        String esp = leerTexto("Especialidad: ");
        tecnicos.add(new Tecnico(nombre, telefono, email, esp));
        System.out.println("✅ Tecnico registrado.");
    }

    // ----------- TICKETS -----------

    private static void crearTicket() {
        if (clientes.isEmpty()) throw new IllegalArgumentException("No hay clientes. Registre uno primero.");

        Cliente cliente = seleccionarCliente();

        System.out.println("Domicilio:");
        String direccion = leerTexto("Direccion: ");
        String ciudad = leerTexto("Ciudad: ");
        String referencia = leerTexto("Referencia: ");
        Domicilio domicilio = new Domicilio(direccion, ciudad, referencia);

        String tipoProblema = leerTexto("Tipo de problema: ");
        String descripcion = leerTexto("Descripcion: ");

        Ticket t = ticketService.crearTicket(cliente, domicilio, tipoProblema, descripcion);
        System.out.println("✅ Ticket creado. ID: " + t.getId());
    }

    private static void listarTickets() {
        var tickets = ticketService.listarTickets();
        if (tickets.isEmpty()) {
            System.out.println("No hay tickets.");
            return;
        }

        for (Ticket t : tickets) {
            System.out.println("-------------------------------------");
            System.out.println("Ticket ID: " + t.getId());
            System.out.println("Cliente: " + t.getCliente().getNombre());
            System.out.println("Domicilio: " + t.getDomicilio().getDireccion() + " (" + t.getDomicilio().getCiudad() + ")");
            System.out.println("Estado: " + t.getEstado());
            System.out.println("Tipo: " + t.getTipoProblema());
            System.out.println("Visitas: " + t.getVisitas().size() + " | Eventos: " + t.getEventos().size());
        }
    }

    // ----------- VISITAS -----------

    private static void programarVisita() {
        Ticket t = seleccionarTicket();
        LocalDateTime fecha = leerFechaHora("Fecha y hora (YYYY-MM-DDTHH:MM) ej. 2026-01-25T15:30: ");
        String responsable = leerTexto("Responsable: ");

        VisitaTecnica v = ticketService.programarVisita(t.getId(), fecha, responsable);
        System.out.println("✅ Visita programada. ID visita: " + v.getId());
    }

    private static void agregarTecnicoAVisita() {
        if (tecnicos.isEmpty()) throw new IllegalArgumentException("No hay tecnicos. Registre uno primero.");

        Ticket t = seleccionarTicket();
        VisitaTecnica v = seleccionarVisita(t);
        Tecnico tecnico = seleccionarTecnico();
        String responsable = leerTexto("Responsable: ");

        ticketService.agregarTecnicoAVisita(t.getId(), v.getId(), tecnico, responsable);
        System.out.println("✅ Tecnico agregado. Ahora la visita tiene: " + v.getTecnicosAsignados().size() + " tecnico(s).");
    }

    private static void reagendarVisita() {
        Ticket t = seleccionarTicket();
        VisitaTecnica v = seleccionarVisita(t);
        LocalDateTime nuevaFecha = leerFechaHora("Nueva fecha/hora (YYYY-MM-DDTHH:MM): ");
        String motivo = leerTexto("Motivo reprogramacion: ");
        String responsable = leerTexto("Responsable: ");

        VisitaTecnica nueva = ticketService.reagendarVisita(t.getId(), v.getId(), nuevaFecha, motivo, responsable);
        System.out.println("✅ Reagendada. Nueva visita ID: " + nueva.getId());
    }

    private static void cancelarVisita() {
        Ticket t = seleccionarTicket();
        VisitaTecnica v = seleccionarVisita(t);
        String motivo = leerTexto("Motivo cancelacion: ");
        String responsable = leerTexto("Responsable: ");

        ticketService.cancelarVisita(t.getId(), v.getId(), motivo, responsable);
        System.out.println("✅ Visita cancelada.");
    }

    private static void completarVisita() {
        Ticket t = seleccionarTicket();
        VisitaTecnica v = seleccionarVisita(t);

        if (v.getTecnicosAsignados().isEmpty()) {
            System.out.println("⚠ Nota: esta visita no tiene tecnicos asignados. Igual se puede completar.");
        }

        String resultado = leerTexto("Resultado: ");
        double ingreso = leerDouble("Ingreso generado (0 si no aplica): ");
        String responsable = leerTexto("Responsable: ");

        ticketService.completarVisita(t.getId(), v.getId(), resultado, ingreso, responsable);
        System.out.println("✅ Visita completada.");
    }

    private static void cerrarTicket() {
        Ticket t = seleccionarTicket();
        String responsable = leerTexto("Responsable: ");
        ticketService.cerrarTicket(t.getId(), responsable);
        System.out.println("✅ Ticket cerrado.");
    }

    // ----------- REPORTES -----------

    private static void menuReportes() {
        var tickets = ticketService.listarTickets();
        System.out.println("------ REPORTES ------");
        System.out.println("1) Total tickets / total visitas");
        System.out.println("2) Reagendados (conteo por ticket)");
        System.out.println("3) Cancelaciones por motivo");
        System.out.println("4) Ingresos por rango");
        int op = leerInt("Opcion: ");

        switch (op) {
            case 1 -> {
                System.out.println("Total tickets: " + reporteService.totalTickets(tickets));
                System.out.println("Total visitas: " + reporteService.totalVisitas(tickets));
            }
            case 2 -> {
                var mapa = reporteService.ticketsReagendadosConteo(tickets);
                mapa.forEach((id, count) -> {
                    if (count > 0) System.out.println("Ticket " + id + " -> Reagendamientos: " + count);
                });
            }
            case 3 -> {
                var mapa = reporteService.cancelacionesPorMotivo(tickets);
                if (mapa.isEmpty()) System.out.println("No hay cancelaciones.");
                else mapa.forEach((motivo, count) -> System.out.println(motivo + " -> " + count));
            }
            case 4 -> {
                LocalDate desde = leerFecha("Desde (YYYY-MM-DD): ");
                LocalDate hasta = leerFecha("Hasta (YYYY-MM-DD): ");
                double total = reporteService.ingresosEnRango(tickets, desde, hasta);
                System.out.println("Ingreso total en rango: " + total);
            }
            default -> System.out.println("Opción inválida.");
        }
    }

    // ----------- SELECTORES -----------

    private static Cliente seleccionarCliente() {
        System.out.println("Seleccione cliente:");
        for (int i = 0; i < clientes.size(); i++) {
            System.out.println((i + 1) + ") " + clientes.get(i).getNombre());
        }
        int idx = leerInt("Numero: ") - 1;
        if (idx < 0 || idx >= clientes.size()) throw new IllegalArgumentException("Cliente invalido.");
        return clientes.get(idx);
    }

    private static Tecnico seleccionarTecnico() {
        System.out.println("Seleccione tecnico:");
        for (int i = 0; i < tecnicos.size(); i++) {
            System.out.println((i + 1) + ") " + tecnicos.get(i).getNombre() + " (" + tecnicos.get(i).getEspecialidad() + ")");
        }
        int idx = leerInt("Numero: ") - 1;
        if (idx < 0 || idx >= tecnicos.size()) throw new IllegalArgumentException("Tecnico invalido.");
        return tecnicos.get(idx);
    }

    private static Ticket seleccionarTicket() {
        var tickets = ticketService.listarTickets();
        if (tickets.isEmpty()) throw new IllegalArgumentException("No hay tickets. Cree uno primero.");

        System.out.println("Seleccione ticket:");
        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            System.out.println((i + 1) + ") " + t.getId() + " | " + t.getCliente().getNombre() + " | " + t.getEstado());
        }
        int idx = leerInt("Numero: ") - 1;
        if (idx < 0 || idx >= tickets.size()) throw new IllegalArgumentException("Ticket invalido.");
        return tickets.get(idx);
    }

    private static VisitaTecnica seleccionarVisita(Ticket t) {
        var visitas = t.getVisitas();
        if (visitas.isEmpty()) throw new IllegalArgumentException("El ticket no tiene visitas. Programe una primero.");

        System.out.println("Seleccione visita:");
        for (int i = 0; i < visitas.size(); i++) {
            VisitaTecnica v = visitas.get(i);
            System.out.println((i + 1) + ") " + v.getId() + " | " + v.getEstado() + " | " +
                    v.getFechaProgramada() + " | tecnicos=" + v.getTecnicosAsignados().size());
        }
        int idx = leerInt("Numero: ") - 1;
        if (idx < 0 || idx >= visitas.size()) throw new IllegalArgumentException("Visita invalida.");
        return visitas.get(idx);
    }

    // ----------- INPUT HELPERS -----------

    private static String leerTexto(String msg) {
        System.out.print(msg);
        String s = sc.nextLine().trim();
        while (s.isEmpty()) {
            System.out.print("No puede estar vacío. " + msg);
            s = sc.nextLine().trim();
        }
        return s;
    }

    private static int leerInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private static double leerDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un numero valido (ej. 150.0).");
            }
        }
    }

    private static LocalDate leerFecha(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException ex) {
                System.out.println("Formato invalido. Use YYYY-MM-DD.");
            }
        }
    }

    private static LocalDateTime leerFechaHora(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return LocalDateTime.parse(sc.nextLine().trim());
            } catch (DateTimeParseException ex) {
                System.out.println("Formato invalido. Use YYYY-MM-DDTHH:MM (ej. 2026-01-25T15:30).");
            }
        }
    }

    // ----------- DATA DEMO -----------

    private static void seedData() {
        clientes.add(new Cliente("Jaime Perez", "999-111-222", "jaime@email.com"));
        clientes.add(new Cliente("Maria Gonzales", "999-333-444", "maria@email.com"));

        tecnicos.add(new Tecnico("Luis Soto", "888-222-333", "luis@company.com", "CTs / Gateway"));
        tecnicos.add(new Tecnico("Ana Ruiz", "888-444-555", "ana@company.com", "Inverters / Monitoring"));
    }
}