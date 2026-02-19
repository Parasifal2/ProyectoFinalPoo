/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import java.util.List;
import proyectofinal.persistencia.PersonaRepoMySQL;

public class AppContext {
    public final TicketService ticketService;
    public final ReporteService reporteService;
    public final List<Cliente> clientes;
    public final List<Tecnico> tecnicos;

    // NUEVO
    public final PersonaRepoMySQL personaRepo;

    public AppContext(TicketService ticketService, ReporteService reporteService,
                      List<Cliente> clientes, List<Tecnico> tecnicos,
                      PersonaRepoMySQL personaRepo) {
        this.ticketService = ticketService;
        this.reporteService = reporteService;
        this.clientes = clientes;
        this.tecnicos = tecnicos;
        this.personaRepo = personaRepo;
    }
}


