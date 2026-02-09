
package proyectofinal.igu;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;


public class Reportes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reportes.class.getName());
    private DefaultTableModel modeloReagendados;
    private DefaultTableModel modeloCancelaciones;
    private DefaultTableModel modeloIngresos;

    private final DateTimeFormatter fmtFechaHora = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");


    private proyectofinal.AppContext ctx;

    public Reportes() {
        initComponents();
        setLocationRelativeTo(null);

        configurarTablas();
        configurarSpinners();
        configurarEventos();
}

public Reportes(proyectofinal.AppContext ctx) {
        this();
        this.ctx = ctx;

    // carga inicial
        refrescarTodo();
}

    private void configurarTablas() {

    modeloReagendados = new DefaultTableModel(
            new Object[]{"Ticket ID", "Cliente", "Reagendamientos"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblReagendadosReportes.setModel(modeloReagendados);

    modeloCancelaciones = new DefaultTableModel(
            new Object[]{"Motivo", "Cantidad"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblCancelacionesReportes.setModel(modeloCancelaciones);

    modeloIngresos = new DefaultTableModel(
            new Object[]{"Fecha", "Ticket ID", "Cliente", "Ingreso", "Tecnicos", "Resultado"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblIngresosReportes.setModel(modeloIngresos);
}

private void configurarSpinners() {
    // Si quieres que por defecto aparezca algo normal:
    spnDesdeIngresosReportes.setModel(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
    spnHastaReportes.setModel(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));

    // Editor más “humano” (sin segundos)
    spnDesdeIngresosReportes.setEditor(new JSpinner.DateEditor(spnDesdeIngresosReportes, "dd/MM/yyyy HH:mm"));
    spnHastaReportes.setEditor(new JSpinner.DateEditor(spnHastaReportes, "dd/MM/yyyy HH:mm"));
}

private void configurarEventos() {

    btnMenuReportes.addActionListener(e -> {
        new VentanaPrincipal(ctx).setVisible(true);
        dispose();
    });

    // Reagendados: filtro por texto + checkbox
    txtBuscarReagendadosReportes.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { aplicarFiltroReagendados(); }
        public void removeUpdate(DocumentEvent e) { aplicarFiltroReagendados(); }
        public void changedUpdate(DocumentEvent e) { aplicarFiltroReagendados(); }
    });

    checkSoloReagendadosReportes.addActionListener(e -> aplicarFiltroReagendados());

    // Ingresos: botón calcular
    btnCalcularIngresosReportes.addActionListener(e -> calcularIngresos());
}

private void refrescarTodo() {
    if (ctx == null) return;
    cargarTotales();
    cargarReagendados();
    cargarCancelaciones();
    calcularIngresos(); // opcional: para que no quede en blanco
}

/* =======================
   TAB 1: TOTALES
   ======================= */
private void cargarTotales() {
    var tickets = ctx.ticketService.listarTickets();

    long totalTickets = ctx.reporteService.totalTickets(tickets);
    long totalVisitas = ctx.reporteService.totalVisitas(tickets);

    long abiertos = ctx.reporteService.contarPorEstado(tickets, proyectofinal.EstadoTicket.ABIERTO);
    long enProceso = ctx.reporteService.contarPorEstado(tickets, proyectofinal.EstadoTicket.EN_PROCESO);
    long cerrados = ctx.reporteService.contarPorEstado(tickets, proyectofinal.EstadoTicket.CERRADO);

    lblTotalTicketsReportes.setText(String.valueOf(totalTickets));
    lblTotalVisitasReportes.setText(String.valueOf(totalVisitas));
    lblTicketsAbiertoReportes.setText(String.valueOf(abiertos));
    lblEnProcesoReportes.setText(String.valueOf(enProceso));
    lblCerradosReportes.setText(String.valueOf(cerrados));
}

/* =======================
   TAB 2: REAGENDADOS
   ======================= */
private void cargarReagendados() {
    if (ctx == null) return;

    modeloReagendados.setRowCount(0);

    var tickets = ctx.ticketService.listarTickets();
    Map<String, Long> conteo = ctx.reporteService.ticketsReagendadosConteo(tickets);

    for (var t : tickets) {
        long c = conteo.getOrDefault(t.getId(), 0L);
        modeloReagendados.addRow(new Object[]{ t.getId(), t.getCliente().getNombre(), c });
    }

    aplicarFiltroReagendados();
}

private void aplicarFiltroReagendados() {
    if (modeloReagendados == null) return;

    String q = txtBuscarReagendadosReportes.getText() == null ? "" : txtBuscarReagendadosReportes.getText().trim().toLowerCase();
    boolean soloMayor0 = checkSoloReagendadosReportes.isSelected();

    // Re-cargar desde fuente real para aplicar filtro limpio:
    modeloReagendados.setRowCount(0);

    var tickets = ctx.ticketService.listarTickets();
    Map<String, Long> conteo = ctx.reporteService.ticketsReagendadosConteo(tickets);

    for (var t : tickets) {
        String id = t.getId();
        String cliente = t.getCliente().getNombre();
        long c = conteo.getOrDefault(id, 0L);

        if (soloMayor0 && c <= 0) continue;

        String haystack = (id + " " + cliente).toLowerCase();
        if (!q.isEmpty() && !haystack.contains(q)) continue;

        modeloReagendados.addRow(new Object[]{ id, cliente, c });
    }
}

/* =======================
   TAB 3: CANCELACIONES
   ======================= */
private void cargarCancelaciones() {
    if (ctx == null) return;

    modeloCancelaciones.setRowCount(0);

    var tickets = ctx.ticketService.listarTickets();
    Map<String, Long> map = ctx.reporteService.cancelacionesPorMotivo(tickets);

    // ordena por cantidad desc (bonito)
    map.entrySet().stream()
            .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
            .forEach(e -> modeloCancelaciones.addRow(new Object[]{ e.getKey(), e.getValue() }));
}

/* =======================
   TAB 4: INGRESOS
   ======================= */
private void calcularIngresos() {
    if (ctx == null) return;

    LocalDate desde = leerFechaSpinnerComoLocalDate(spnDesdeIngresosReportes);
    LocalDate hasta = leerFechaSpinnerComoLocalDate(spnHastaReportes);

    if (desde.isAfter(hasta)) {
        JOptionPane.showMessageDialog(this, "Rango inválido: 'Desde' no puede ser mayor que 'Hasta'.");
        return;
    }

    var tickets = ctx.ticketService.listarTickets();

    double total = ctx.reporteService.ingresosEnRango(tickets, desde, hasta);
    lblIngresoTotalReportes.setText(String.format(java.util.Locale.US, "%.2f", total));

    // tabla detalle
    modeloIngresos.setRowCount(0);
    var filas = ctx.reporteService.detalleIngresosEnRango(tickets, desde, hasta);

    for (Object[] row : filas) {
        // row[0] es LocalDateTime (según el método)
        LocalDateTime f = (LocalDateTime) row[0];
        row[0] = (f == null) ? "" : f.format(fmtFechaHora);
        modeloIngresos.addRow(row);
    }

    lblFilasIngresosReportes.setText("Filas: " + filas.size());
}

private LocalDate leerFechaSpinnerComoLocalDate(JSpinner spn) {
    Date d = (Date) spn.getValue();
    return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTotalTicketsReportes = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblCerradosReportes = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblEnProcesoReportes = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblTicketsAbiertoReportes = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblTotalVisitasReportes = new javax.swing.JLabel();
        lblReportes = new javax.swing.JLabel();
        btnMenuReportes = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblBuscarReagendadosReportes = new javax.swing.JLabel();
        txtBuscarReagendadosReportes = new javax.swing.JTextField();
        checkSoloReagendadosReportes = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReagendadosReportes = new javax.swing.JTable();
        lblReagendadosReportes = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lblCancelacionesReportes = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCancelacionesReportes = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        lblDesdeIngresosReportes = new javax.swing.JLabel();
        spnDesdeIngresosReportes = new javax.swing.JSpinner();
        lblHastaIngresosReportes = new javax.swing.JLabel();
        spnHastaReportes = new javax.swing.JSpinner();
        lblIngresoTotalIngresosReportes = new javax.swing.JLabel();
        btnCalcularIngresosReportes = new javax.swing.JButton();
        lblIngresoTotalReportes = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblIngresosReportes = new javax.swing.JTable();
        lblFilasIngresosReportes = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Total Tickets:");

        lblTotalTicketsReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotalTicketsReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalTicketsReportes.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(lblTotalTicketsReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(lblTotalTicketsReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("Cerrados");

        lblCerradosReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblCerradosReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCerradosReportes.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(89, 89, 89))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblCerradosReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(lblCerradosReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("En proceso");

        lblEnProcesoReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblEnProcesoReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEnProcesoReportes.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEnProcesoReportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(lblEnProcesoReportes)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Tickets Abiertos");

        lblTicketsAbiertoReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTicketsAbiertoReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTicketsAbiertoReportes.setText("0");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(lblTicketsAbiertoReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTicketsAbiertoReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Total Visitas:");

        lblTotalVisitasReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotalVisitasReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalVisitasReportes.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(lblTotalVisitasReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalVisitasReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addGap(21, 21, 21))
        );

        lblReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReportes.setText("Reportes");

        btnMenuReportes.setText("Menu");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 224, Short.MAX_VALUE)
        );

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/elevation 2.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(367, 367, 367)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(383, 383, 383)
                        .addComponent(lblReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMenuReportes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(378, 378, 378)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(378, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(38, Short.MAX_VALUE)
                        .addComponent(lblReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMenuReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(238, 238, 238)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(238, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Totales", jPanel2);

        lblBuscarReagendadosReportes.setText("Buscar");

        checkSoloReagendadosReportes.setText("Solo > 0");

        tblReagendadosReportes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Ticket ID", "Cliente", "Reagendamientos"
            }
        ));
        jScrollPane1.setViewportView(tblReagendadosReportes);

        lblReagendadosReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblReagendadosReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReagendadosReportes.setText("Reagendados");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(lblBuscarReagendadosReportes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscarReagendadosReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkSoloReagendadosReportes)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(366, 366, 366)
                .addComponent(lblReagendadosReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(400, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblReagendadosReportes)
                .addGap(50, 50, 50)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscarReagendadosReportes)
                    .addComponent(txtBuscarReagendadosReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSoloReagendadosReportes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Reagendados", jPanel9);

        lblCancelacionesReportes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblCancelacionesReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCancelacionesReportes.setText("Cancelaciones");

        tblCancelacionesReportes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Motivo", "Cantidad"
            }
        ));
        jScrollPane2.setViewportView(tblCancelacionesReportes);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(410, 410, 410)
                .addComponent(lblCancelacionesReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(444, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblCancelacionesReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cancelaciones", jPanel10);

        lblDesdeIngresosReportes.setText("Desde");

        spnDesdeIngresosReportes.setModel(new javax.swing.SpinnerDateModel());

        lblHastaIngresosReportes.setText("Hasta");

        spnHastaReportes.setModel(new javax.swing.SpinnerDateModel());

        lblIngresoTotalIngresosReportes.setText("Ingreso Total:");

        btnCalcularIngresosReportes.setText("Calcular");

        lblIngresoTotalReportes.setText("0.00");

        tblIngresosReportes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Ticket ID", "Cliente", "Ingreso", "Tecnicos", "Resultado"
            }
        ));
        jScrollPane3.setViewportView(tblIngresosReportes);

        lblFilasIngresosReportes.setText("Filas: 0");

        jLabel20.setText("S/.");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Ingresos");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(lblDesdeIngresosReportes)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spnDesdeIngresosReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                                    .addComponent(btnCalcularIngresosReportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(lblIngresoTotalIngresosReportes)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblIngresoTotalReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(lblHastaIngresosReportes)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spnHastaReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblFilasIngresosReportes)))
                        .addGap(0, 224, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(384, 384, 384)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDesdeIngresosReportes)
                    .addComponent(spnDesdeIngresosReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHastaIngresosReportes)
                    .addComponent(spnHastaReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIngresoTotalIngresosReportes)
                    .addComponent(btnCalcularIngresosReportes)
                    .addComponent(lblIngresoTotalReportes)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFilasIngresosReportes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Ingresos", jPanel11);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Reportes(null).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcularIngresosReportes;
    private javax.swing.JButton btnMenuReportes;
    private javax.swing.JCheckBox checkSoloReagendadosReportes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscarReagendadosReportes;
    private javax.swing.JLabel lblCancelacionesReportes;
    private javax.swing.JLabel lblCerradosReportes;
    private javax.swing.JLabel lblDesdeIngresosReportes;
    private javax.swing.JLabel lblEnProcesoReportes;
    private javax.swing.JLabel lblFilasIngresosReportes;
    private javax.swing.JLabel lblHastaIngresosReportes;
    private javax.swing.JLabel lblIngresoTotalIngresosReportes;
    private javax.swing.JLabel lblIngresoTotalReportes;
    private javax.swing.JLabel lblReagendadosReportes;
    private javax.swing.JLabel lblReportes;
    private javax.swing.JLabel lblTicketsAbiertoReportes;
    private javax.swing.JLabel lblTotalTicketsReportes;
    private javax.swing.JLabel lblTotalVisitasReportes;
    private javax.swing.JSpinner spnDesdeIngresosReportes;
    private javax.swing.JSpinner spnHastaReportes;
    private javax.swing.JTable tblCancelacionesReportes;
    private javax.swing.JTable tblIngresosReportes;
    private javax.swing.JTable tblReagendadosReportes;
    private javax.swing.JTextField txtBuscarReagendadosReportes;
    // End of variables declaration//GEN-END:variables
}
