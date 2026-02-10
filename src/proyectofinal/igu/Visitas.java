package proyectofinal.igu;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;



public class Visitas extends javax.swing.JFrame {
    
 private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Visitas.class.getName());
 private final DateTimeFormatter fmtFechaHora = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
 private proyectofinal.AppContext ctx;
 private String ticketIdSeleccionado = null;
 private String visitaIdSeleccionada = null;

private DefaultTableModel modeloVisitas;
private DefaultListModel<String> modeloTecnicosAsignados = new DefaultListModel<>();

public Visitas() {
    initComponents();
    setLocationRelativeTo(null);
    spnFechaVisitas.setEditor(new javax.swing.JSpinner.DateEditor(spnFechaVisitas, "dd MMM yyyy, HH:mm"));
    configurarTabla();
    configurarListaTecnicosAsignados();
    configurarEventos();
}

public Visitas(proyectofinal.AppContext ctx) {
    this();
    this.ctx = ctx;

    cargarComboTickets();
    cargarComboTecnicos();
    limpiarPantalla();
}

private void configurarTabla() {
    modeloVisitas = new DefaultTableModel(
            new Object[]{"ID Visita", "Fecha Programada", "Estado", "Tecnicos", "Ingreso"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    tblVisitasVisitas.setModel(modeloVisitas);
    tblVisitasVisitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
}

private void configurarListaTecnicosAsignados() {
    lstListaDeTecnicosVisitas.setModel(modeloTecnicosAsignados);
}

private void configurarEventos() {

    // SOLO UN listener para el combo
    cboSeleccionarTicketVisitas.addActionListener(e -> cargarTicketSeleccionado());

    // click en tabla = selecciona visita
    tblVisitasVisitas.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override public void mouseClicked(java.awt.event.MouseEvent e) {
            cargarVisitaSeleccionadaDesdeTabla();
        }
    });

    btnProgramarVisitas.addActionListener(e -> programarVisita());

    btnAgregarTecnicosVisitas.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Selecciona una visita y usa '+ Agregar' abajo.")
    );

    btnAgregarVisitas.addActionListener(e -> agregarTecnicoAVisitaSeleccionada());
    btnReagendarVisitas.addActionListener(e -> reagendarVisita());
    btnCancelarVisitas.addActionListener(e -> cancelarVisita());
    btnCompletarVisitas.addActionListener(e -> completarVisita());
    btnCerrarTicketVisitas.addActionListener(e -> cerrarTicket());

    btnMenuVisitas.addActionListener(e -> {
        new VentanaPrincipal(ctx).setVisible(true);
        dispose();
    });
}

private void cargarComboTickets() {
    if (ctx == null) return;

    cboSeleccionarTicketVisitas.removeAllItems();
    cboSeleccionarTicketVisitas.addItem("Seleccionar Ticket");

    for (var t : ctx.ticketService.listarTickets()) {
        cboSeleccionarTicketVisitas.addItem(t.getId() + " | " + t.getCliente().getNombre());
    }
}

private void cargarComboTecnicos() {
    if (ctx == null) return;

    cboElijaUnTecnicoVisitas.removeAllItems();
    cboElijaUnTecnicoVisitas.addItem("Elija un tecnico");

    for (var tec : ctx.tecnicos) {
        cboElijaUnTecnicoVisitas.addItem(tec.getNombre() + " | " + tec.getEspecialidad());
    }
}

private void limpiarPantalla() {
    ticketIdSeleccionado = null;
    visitaIdSeleccionada = null;

    lblDetallesDelClienteVisitas.setText("Detalles del Cliente");
    lblEstadoVisitas.setText("Estado");
    lblDomicilioCiudadVisitas.setText("Domicilio / Ciudad");

    modeloVisitas.setRowCount(0);
    modeloTecnicosAsignados.clear();

    txtEscribaUnResultadoVisitas.setText("");
    txtIngresoVisitas.setText("0.00");
    txtMotivoCancelacionVisitas.setText("");
}

private String extraerTicketIdDelCombo() {
    int idx = cboSeleccionarTicketVisitas.getSelectedIndex();
    if (idx <= 0) return null;

    String item = String.valueOf(cboSeleccionarTicketVisitas.getSelectedItem()); // "id | nombre"
    int pipe = item.indexOf("|");
    if (pipe <= 0) return null;
    return item.substring(0, pipe).trim();
}

private proyectofinal.Ticket getTicketSeleccionado() {
    if (ctx == null) return null;
    if (ticketIdSeleccionado == null) return null;
    return ctx.ticketService.buscarTicketPorId(ticketIdSeleccionado).orElse(null);
}

private void cargarTicketSeleccionado() {
    ticketIdSeleccionado = extraerTicketIdDelCombo();
    visitaIdSeleccionada = null;
    modeloTecnicosAsignados.clear();

    if (ticketIdSeleccionado == null) {
        limpiarPantalla();
        return;
    }

    var t = getTicketSeleccionado();
    if (t == null) {
        JOptionPane.showMessageDialog(this, "Ticket no encontrado.");
        limpiarPantalla();
        return;
    }

    lblDetallesDelClienteVisitas.setText(
            t.getCliente().getNombre() + " | " + t.getCliente().getTelefono() + " | " + t.getCliente().getEmail()
    );
    lblEstadoVisitas.setText(t.getEstado().name());
    lblDomicilioCiudadVisitas.setText(
            t.getDomicilio().getDireccion() + " / " + t.getDomicilio().getCiudad()
    );

    cargarTablaVisitasDeTicket(t);
}

private void cargarTablaVisitasDeTicket(proyectofinal.Ticket t) {
    modeloVisitas.setRowCount(0);

    for (var v : t.getVisitas()) {
        String tecnicosStr = "";
        var listaTec = v.getTecnicosAsignados();
        if (listaTec != null && !listaTec.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (var tec : listaTec) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(tec.getNombre());
            }
            tecnicosStr = sb.toString();
        }

        modeloVisitas.addRow(new Object[]{
                v.getId(),
                (v.getFechaProgramada() == null ? "" : v.getFechaProgramada().format(fmtFechaHora)),
                v.getEstado().name(),
                tecnicosStr,
                v.getIngresoGenerado()
        });
    }
}

private void cargarVisitaSeleccionadaDesdeTabla() {
    int row = tblVisitasVisitas.getSelectedRow();
    if (row < 0) return;

    visitaIdSeleccionada = String.valueOf(modeloVisitas.getValueAt(row, 0));

    var t = getTicketSeleccionado();
    if (t == null) return;

    var vOpt = t.getVisitas().stream().filter(x -> x.getId().equals(visitaIdSeleccionada)).findFirst();
    if (vOpt.isEmpty()) return;

    var v = vOpt.get();

    modeloTecnicosAsignados.clear();
    for (var tec : v.getTecnicosAsignados()) {
        modeloTecnicosAsignados.addElement(tec.getNombre() + " | " + tec.getEspecialidad());
    }

    txtEscribaUnResultadoVisitas.setText(v.getResultado() == null ? "" : v.getResultado());
    txtIngresoVisitas.setText(String.valueOf(v.getIngresoGenerado()));
    txtMotivoCancelacionVisitas.setText(v.getMotivoCancelacion() == null ? "" : v.getMotivoCancelacion());
}

private LocalDateTime leerFechaSpinner() {
    Date d = (Date) spnFechaVisitas.getValue();
    return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
}

private proyectofinal.Tecnico getTecnicoSeleccionado() {
    if (ctx == null) return null;

    int idx = cboElijaUnTecnicoVisitas.getSelectedIndex();
    if (idx <= 0) return null;

    String item = String.valueOf(cboElijaUnTecnicoVisitas.getSelectedItem()); // "nombre | esp"
    int pipe = item.indexOf("|");
    String nombre = (pipe > 0) ? item.substring(0, pipe).trim() : item.trim();

    for (var tec : ctx.tecnicos) {
        if (tec.getNombre().equals(nombre)) return tec;
    }
    return null;
}

private void programarVisita() {
    if (ctx == null) return;
    if (ticketIdSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
        return;
    }

    LocalDateTime fecha = leerFechaSpinner();
    ctx.ticketService.programarVisita(ticketIdSeleccionado, fecha, "Sistema");

    JOptionPane.showMessageDialog(this, "Visita programada.");
    cargarTicketSeleccionado();
}

private void agregarTecnicoAVisitaSeleccionada() {
    if (ctx == null) return;
    if (ticketIdSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
        return;
    }
    if (visitaIdSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Seleccione una visita de la tabla.");
        return;
    }

    var tec = getTecnicoSeleccionado();
    if (tec == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un técnico.");
        return;
    }

    ctx.ticketService.agregarTecnicoAVisita(ticketIdSeleccionado, visitaIdSeleccionada, tec, "Sistema");
    JOptionPane.showMessageDialog(this, "Técnico agregado.");

    cargarTicketSeleccionado();
}

private void reagendarVisita() {
    if (ctx == null) return;
    if (ticketIdSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
        return;
    }
    if (visitaIdSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Seleccione una visita de la tabla.");
        return;
    }

    LocalDateTime nuevaFecha = leerFechaSpinner();
    String motivo = txtMotivoCancelacionVisitas.getText().trim();
    if (motivo.isBlank()) motivo = "Reagendado sin motivo";

    ctx.ticketService.reagendarVisita(ticketIdSeleccionado, visitaIdSeleccionada, nuevaFecha, motivo, "Sistema");

    JOptionPane.showMessageDialog(this, "Visita reagendada.");
    cargarTicketSeleccionado();
}

private void cancelarVisita() {
    if (ctx == null) return;
    if (ticketIdSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
        return;
    }
    if (visitaIdSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Seleccione una visita de la tabla.");
        return;
    }

    String motivo = txtMotivoCancelacionVisitas.getText().trim();
    if (motivo.isBlank()) {
        JOptionPane.showMessageDialog(this, "Ingrese un motivo de cancelación.");
        return;
    }

    ctx.ticketService.cancelarVisita(ticketIdSeleccionado, visitaIdSeleccionada, motivo, "Sistema");

    JOptionPane.showMessageDialog(this, "Visita cancelada.");
    cargarTicketSeleccionado();
}

private void completarVisita() {
    if (ctx == null) return;
    if (ticketIdSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
        return;
    }
    if (visitaIdSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Seleccione una visita de la tabla.");
        return;
    }

    String resultado = txtEscribaUnResultadoVisitas.getText().trim();
    if (resultado.isBlank()) {
        JOptionPane.showMessageDialog(this, "Ingrese un resultado.");
        return;
    }

    double ingreso;
    try {
        ingreso = Double.parseDouble(txtIngresoVisitas.getText().trim());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Ingreso inválido.");
        return;
    }

    ctx.ticketService.completarVisita(ticketIdSeleccionado, visitaIdSeleccionada, resultado, ingreso, "Sistema");

    JOptionPane.showMessageDialog(this, "Visita completada.");
    cargarTicketSeleccionado();
}

private void cerrarTicket() {
    if (ctx == null) return;
    if (ticketIdSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
        return;
    }

    ctx.ticketService.cerrarTicket(ticketIdSeleccionado, "Sistema");
    JOptionPane.showMessageDialog(this, "Ticket cerrado.");

    cargarTicketSeleccionado();
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblSeleccionarTicketVisitasTecnicas = new javax.swing.JLabel();
        lblTicketVisitas = new javax.swing.JLabel();
        cboSeleccionarTicketVisitas = new javax.swing.JComboBox<>();
        lblClienteVisitas = new javax.swing.JLabel();
        lblDetallesDelClienteVisitas = new javax.swing.JLabel();
        lblEstadoTicketVisitas = new javax.swing.JLabel();
        lblEstadoVisitas = new javax.swing.JLabel();
        lblDomicilioVisitas = new javax.swing.JLabel();
        lblDomicilioCiudadVisitas = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVisitasVisitas = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        btnProgramarVisitas = new javax.swing.JButton();
        btnAgregarTecnicosVisitas = new javax.swing.JButton();
        btnReagendarVisitas = new javax.swing.JButton();
        btnCancelarVisitas = new javax.swing.JButton();
        btnCompletarVisitas = new javax.swing.JButton();
        btnCerrarTicketVisitas = new javax.swing.JButton();
        btnMenuVisitas = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lblFechaYHoraParaProgramarVisitas = new javax.swing.JLabel();
        lblFechayHoraVisitas = new javax.swing.JLabel();
        spnFechaVisitas = new javax.swing.JSpinner();
        lblResultadoVisitas = new javax.swing.JLabel();
        txtEscribaUnResultadoVisitas = new javax.swing.JTextField();
        lblTecnicosVisitas = new javax.swing.JLabel();
        cboElijaUnTecnicoVisitas = new javax.swing.JComboBox<>();
        btnAgregarVisitas = new javax.swing.JButton();
        lblIngresoVisitas = new javax.swing.JLabel();
        txtIngresoVisitas = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstListaDeTecnicosVisitas = new javax.swing.JList<>();
        txtMotivoCancelacionVisitas = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Visitas Tecnicas");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblSeleccionarTicketVisitasTecnicas.setText("Seleccionar Ticket");

        lblTicketVisitas.setText("Ticket");

        cboSeleccionarTicketVisitas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar Ticket" }));

        lblClienteVisitas.setText("Cliente:");

        lblDetallesDelClienteVisitas.setText("Detalles del Cliente");

        lblEstadoTicketVisitas.setText("Estado Ticket:");

        lblEstadoVisitas.setText("Estado");

        lblDomicilioVisitas.setText("Domicilio:");

        lblDomicilioCiudadVisitas.setText("Domicilio / Ciudad");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblDomicilioVisitas)
                        .addGap(18, 18, 18)
                        .addComponent(lblDomicilioCiudadVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTicketVisitas)
                        .addGap(18, 18, 18)
                        .addComponent(cboSeleccionarTicketVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblClienteVisitas)
                        .addGap(18, 18, 18)
                        .addComponent(lblDetallesDelClienteVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblEstadoTicketVisitas)
                        .addGap(18, 18, 18)
                        .addComponent(lblEstadoVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSeleccionarTicketVisitasTecnicas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblSeleccionarTicketVisitasTecnicas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTicketVisitas)
                    .addComponent(cboSeleccionarTicketVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClienteVisitas)
                    .addComponent(lblDetallesDelClienteVisitas)
                    .addComponent(lblEstadoTicketVisitas)
                    .addComponent(lblEstadoVisitas))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDomicilioVisitas)
                    .addComponent(lblDomicilioCiudadVisitas))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("Visitas");

        tblVisitasVisitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Visita", "Fecha Programada", "Estado", "Tecnicos", "Ingreso"
            }
        ));
        jScrollPane1.setViewportView(tblVisitasVisitas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setText("Acciones:");

        btnProgramarVisitas.setText("Programar");

        btnAgregarTecnicosVisitas.setText("Agregar Tecnicos");

        btnReagendarVisitas.setText("Reagendar");

        btnCancelarVisitas.setText("Cancelar");

        btnCompletarVisitas.setText("Completar");

        btnCerrarTicketVisitas.setText("Cerrar Ticket");

        btnMenuVisitas.setText("Menu");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnProgramarVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnCerrarTicketVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCompletarVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancelarVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnReagendarVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAgregarTecnicosVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(btnMenuVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(9, 9, 9)
                .addComponent(btnProgramarVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregarTecnicosVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReagendarVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelarVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCompletarVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCerrarTicketVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMenuVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setText("Detalles");

        lblFechaYHoraParaProgramarVisitas.setText("Fecha y Hora (Para programar / Reagendar");

        lblFechayHoraVisitas.setText("Fecha y Hora");

        spnFechaVisitas.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.MINUTE));

        lblResultadoVisitas.setText("Resultado:");

        txtEscribaUnResultadoVisitas.setText("Escriba un resultado");

        lblTecnicosVisitas.setText("Tecnicos");

        cboElijaUnTecnicoVisitas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Elija un tecnico" }));

        btnAgregarVisitas.setText("+ Agregar");

        lblIngresoVisitas.setText("Ingreso:");

        txtIngresoVisitas.setText("0.00");

        lstListaDeTecnicosVisitas.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Lista de tecnicos asignados" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstListaDeTecnicosVisitas);

        jLabel18.setText("Motivo Cancelacion");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(lblFechaYHoraParaProgramarVisitas)
                                .addGap(270, 270, 270)
                                .addComponent(lblTecnicosVisitas))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFechayHoraVisitas)
                                    .addComponent(lblResultadoVisitas))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(spnFechaVisitas, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                                    .addComponent(txtEscribaUnResultadoVisitas))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblIngresoVisitas)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboElijaUnTecnicoVisitas, 0, 183, Short.MAX_VALUE)
                            .addComponent(btnAgregarVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIngresoVisitas))
                        .addGap(98, 98, 98)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                .addComponent(txtMotivoCancelacionVisitas)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFechaYHoraParaProgramarVisitas)
                            .addComponent(lblTecnicosVisitas)
                            .addComponent(cboElijaUnTecnicoVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblFechayHoraVisitas)
                                    .addComponent(spnFechaVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(btnAgregarVisitas))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblResultadoVisitas)
                            .addComponent(txtEscribaUnResultadoVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtMotivoCancelacionVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblIngresoVisitas)
                                    .addComponent(txtIngresoVisitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(55, 55, 55))))))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(426, 426, 426)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
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
        java.awt.EventQueue.invokeLater(() -> new Visitas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarTecnicosVisitas;
    private javax.swing.JButton btnAgregarVisitas;
    private javax.swing.JButton btnCancelarVisitas;
    private javax.swing.JButton btnCerrarTicketVisitas;
    private javax.swing.JButton btnCompletarVisitas;
    private javax.swing.JButton btnMenuVisitas;
    private javax.swing.JButton btnProgramarVisitas;
    private javax.swing.JButton btnReagendarVisitas;
    private javax.swing.JComboBox<String> cboElijaUnTecnicoVisitas;
    private javax.swing.JComboBox<String> cboSeleccionarTicketVisitas;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblClienteVisitas;
    private javax.swing.JLabel lblDetallesDelClienteVisitas;
    private javax.swing.JLabel lblDomicilioCiudadVisitas;
    private javax.swing.JLabel lblDomicilioVisitas;
    private javax.swing.JLabel lblEstadoTicketVisitas;
    private javax.swing.JLabel lblEstadoVisitas;
    private javax.swing.JLabel lblFechaYHoraParaProgramarVisitas;
    private javax.swing.JLabel lblFechayHoraVisitas;
    private javax.swing.JLabel lblIngresoVisitas;
    private javax.swing.JLabel lblResultadoVisitas;
    private javax.swing.JLabel lblSeleccionarTicketVisitasTecnicas;
    private javax.swing.JLabel lblTecnicosVisitas;
    private javax.swing.JLabel lblTicketVisitas;
    private javax.swing.JList<String> lstListaDeTecnicosVisitas;
    private javax.swing.JSpinner spnFechaVisitas;
    private javax.swing.JTable tblVisitasVisitas;
    private javax.swing.JTextField txtEscribaUnResultadoVisitas;
    private javax.swing.JTextField txtIngresoVisitas;
    private javax.swing.JTextField txtMotivoCancelacionVisitas;
    // End of variables declaration//GEN-END:variables
}
