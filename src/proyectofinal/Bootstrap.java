/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import proyectofinal.persistencia.*;
import java.util.ArrayList;

public final class Bootstrap {
    private Bootstrap() {}

    public static AppContext init() {

        MySQL db = new MySQL("localhost", 3306, "solar_swing", "admin", "P@ssw0rd");

        PersonaRepoMySQL personaRepo = new PersonaRepoMySQL(db);
        DomicilioRepoMySQL domicilioRepo = new DomicilioRepoMySQL(db);
        TicketRepoMySQL ticketRepo = new TicketRepoMySQL(db, personaRepo, domicilioRepo);

        TicketService ticketService = new TicketService(ticketRepo); // modo BD
        ReporteService reporteService = new ReporteService();

        // Seed SOLO si BD vacía
        if (!personaRepo.hayPersonas()) {
            personaRepo.upsertCliente(new Cliente("Jaime Perez", "999-111-222", "jaime@email.com"));
            personaRepo.upsertCliente(new Cliente("Maria Gonzales", "999-333-444", "maria@email.com"));

            personaRepo.upsertTecnico(new Tecnico("Luis Soto", "888-222-333", "luis@company.com", "CTs / Gateway"));
            personaRepo.upsertTecnico(new Tecnico("Ana Ruiz", "888-444-555", "ana@company.com", "Inverters / Monitoring"));
        }

        ArrayList<Cliente> clientes = new ArrayList<>(personaRepo.listarClientes());
        ArrayList<Tecnico> tecnicos = new ArrayList<>(personaRepo.listarTecnicos());

        // Seed tickets SOLO si no hay tickets
        if (!ticketRepo.hayTickets() && !clientes.isEmpty()) {
            Cliente c1 = clientes.get(0);
            Cliente c2 = clientes.size() > 1 ? clientes.get(1) : clientes.get(0);

            Ticket tk1 = ticketService.crearTicket(
                    c1,
                    new Domicilio("Av. Primavera 123", "Trujillo", "Frente al parque"),
                    "Gateway/Envoy",
                    "El Envoy aparece offline desde ayer."
            );

            ticketService.crearTicket(
                    c2,
                    new Domicilio("Jr. Los Girasoles 456", "Trujillo", "Casa azul"),
                    "Electrico",
                    "El sistema baja producción en horas pico."
            );

            var visita1 = ticketService.programarVisita(
                    tk1.getId(),
                    java.time.LocalDateTime.now().plusDays(1),
                    "Sistema"
            );

            if (!tecnicos.isEmpty()) {
                ticketService.agregarTecnicoAVisita(
                        tk1.getId(),
                        visita1.getId(),
                        tecnicos.get(0),
                        "Sistema"
                );
            }
        }

        // AQUÍ estaba el error: faltaba personaRepo
        return new AppContext(ticketService, reporteService, clientes, tecnicos, personaRepo);
    }
}
