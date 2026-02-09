/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import java.util.List;

public class AppContext {
    public final TicketService ticketService;
    public final ReporteService reporteService;
    public final List<Cliente> clientes;
    public final List<Tecnico> tecnicos;

    public AppContext(TicketService ticketService, ReporteService reporteService,
                    List<Cliente> clientes, List<Tecnico> tecnicos) {
        this.ticketService = ticketService;
        this.reporteService = reporteService;
        this.clientes = clientes;
        this.tecnicos = tecnicos;
    }
}

