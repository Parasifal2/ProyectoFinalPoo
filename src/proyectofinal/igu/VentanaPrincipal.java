
package proyectofinal.igu;


public class VentanaPrincipal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    private proyectofinal.AppContext ctx;

    
    
    public VentanaPrincipal() {
    initComponents();
    setLocationRelativeTo(null);
}

public VentanaPrincipal(proyectofinal.AppContext ctx) {
    this();
    this.ctx = ctx;
}

    
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnTecnicos = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnTickets = new javax.swing.JButton();
        btnVisitas = new javax.swing.JButton();
        btnReportes = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnTecnicos.setText("2. Tecnicos");
        btnTecnicos.addActionListener(this::btnTecnicosActionPerformed);

        btnClientes.setText("1. Clientes");
        btnClientes.addActionListener(this::btnClientesActionPerformed);

        btnTickets.setText("3. Tickets");
        btnTickets.addActionListener(this::btnTicketsActionPerformed);

        btnVisitas.setText("4. Visitas");
        btnVisitas.addActionListener(this::btnVisitasActionPerformed);

        btnReportes.setText("5. Reportes");
        btnReportes.addActionListener(this::btnReportesActionPerformed);

        jButton6.setText("6. Salir");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTickets, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTickets, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Reportes Elevation Solar");

        jLabel2.setIcon(new javax.swing.ImageIcon("C:\\Users\\Elevation Solar\\Downloads\\elevation 2.png")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 253, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(196, 196, 196))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(330, 330, 330))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jLabel2)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
       
        new Clientes(ctx).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTecnicosActionPerformed
        // TODO add your handling code here:
        new Tecnicos(ctx).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTecnicosActionPerformed

    private void btnTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketsActionPerformed
        new Tickets(ctx).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTicketsActionPerformed

    private void btnVisitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisitasActionPerformed
        new Visitas(ctx).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnVisitasActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        new Reportes(ctx).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnReportesActionPerformed

    
public static void main(String[] args) {

    var ticketService = new proyectofinal.TicketService();
    var reporteService = new proyectofinal.ReporteService();
    var clientes = new java.util.ArrayList<proyectofinal.Cliente>();
    var tecnicos = new java.util.ArrayList<proyectofinal.Tecnico>();

    // Semillas (Clientes)
    var c1 = new proyectofinal.Cliente("Jaime Perez", "999-111-222", "jaime@email.com");
    var c2 = new proyectofinal.Cliente("Maria Gonzales", "999-333-444", "maria@email.com");
    clientes.add(c1);
    clientes.add(c2);

    // Semillas (Técnicos)
    var t1 = new proyectofinal.Tecnico("Luis Soto", "888-222-333", "luis@company.com", "CTs / Gateway");
    var t2 = new proyectofinal.Tecnico("Ana Ruiz", "888-444-555", "ana@company.com", "Inverters / Monitoring");
    tecnicos.add(t1);
    tecnicos.add(t2);

    // Contexto
    var ctx = new proyectofinal.AppContext(ticketService, reporteService, clientes, tecnicos);

    // -------------------------------
    // ✅ Semillas (Tickets)
    // -------------------------------
    var tk1 = ticketService.crearTicket(
            c1,
            new proyectofinal.Domicilio("Av. Primavera 123", "Trujillo", "Frente al parque"),
            "Gateway/Envoy",
            "El Envoy aparece offline desde ayer."
    );

    var tk2 = ticketService.crearTicket(
            c2,
            new proyectofinal.Domicilio("Jr. Los Girasoles 456", "Trujillo", "Casa azul"),
            "Electrico",
            "El sistema baja producción en horas pico."
    );

    // -------------------------------
    // Semillas (Visitas)
    // -------------------------------
    var visita1 = ticketService.programarVisita(
            tk1.getId(),
            java.time.LocalDateTime.now().plusDays(1),
            "Sistema"
    );

    ticketService.agregarTecnicoAVisita(
            tk1.getId(),
            visita1.getId(),
            t1,
            "Sistema"
    );

    // Arranque UI
    java.awt.EventQueue.invokeLater(() -> new proyectofinal.igu.VentanaPrincipal(ctx).setVisible(true));
}


   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnTecnicos;
    private javax.swing.JButton btnTickets;
    private javax.swing.JButton btnVisitas;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
