package proyectofinal.igu;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;


public class Tickets extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tickets.class.getName());

    private proyectofinal.AppContext ctx;
    private String ticketIdSeleccionado = null;
    private javax.swing.table.DefaultTableModel modeloTickets;



public Tickets() {
    initComponents();
    setLocationRelativeTo(null);

    configurarTabla();
    configurarEventos();
}


public Tickets(proyectofinal.AppContext ctx) {
    this();
    this.ctx = ctx;

    cargarComboClientes();
    cargarTablaTickets();
    limpiarFormulario();
}



private void configurarTabla() {
    modeloTickets = new javax.swing.table.DefaultTableModel(
        new Object[]{"ID", "Cliente", "Estado", "Tipo Problema", "Fecha de creacion", "#Visitas"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    tblTickets.setModel(modeloTickets);
    tblTickets.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
}

private void configurarEventos() {
    // Selección en tabla (click)
    tblTickets.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            cargarTicketSeleccionadoDesdeTabla();
        }
    });

    // Botones
    btnNuevoTickets.addActionListener(e -> limpiarFormulario());

    btnGuardarTickets.addActionListener(e -> crearTicket());

    btnActualizarTickets.addActionListener(e -> actualizarTicket());

    btnLimpiarTickets.addActionListener(e -> limpiarFormulario());

    btnMenuTickets.addActionListener(e -> {
        new VentanaPrincipal(ctx).setVisible(true);
        dispose();
    });

    btnNuevoClienteTickets.addActionListener(e -> {
    new Clientes(ctx).setVisible(true);
    this.dispose(); 
});
}

private void cargarComboClientes() {
    if (ctx == null) return;

    cboClienteTickets.removeAllItems();
    cboClienteTickets.addItem("Seleccione");

    for (var c : ctx.clientes) {
        // Guardamos el objeto completo como item (ideal)
        cboClienteTickets.addItem(c.getNombre());
    }
}

private proyectofinal.Cliente getClienteSeleccionado() {
    if (ctx == null) return null;

    int idx = cboClienteTickets.getSelectedIndex();
    if (idx <= 0) return null; // 0 = "Seleccione"

    // porque el combo solo tiene nombres, buscamos el objeto real por nombre
    String nombre = String.valueOf(cboClienteTickets.getSelectedItem());
    for (var c : ctx.clientes) {
        if (c.getNombre().equals(nombre)) return c;
    }
    return null;
}

private void cargarTablaTickets() {
    if (ctx == null) return;

    modeloTickets.setRowCount(0);

    for (var t : ctx.ticketService.listarTickets()) {
        modeloTickets.addRow(new Object[]{
            t.getId(),
            t.getCliente().getNombre(),
            t.getEstado().name(),
            t.getTipoProblema(),
            t.getFechaCreacion().toString(),
            t.getVisitas().size()
        });
    }
}

private void cargarTicketSeleccionadoDesdeTabla() {
    int row = tblTickets.getSelectedRow();
    if (row < 0) return;

    ticketIdSeleccionado = String.valueOf(modeloTickets.getValueAt(row, 0));

    var opt = ctx.ticketService.buscarTicketPorId(ticketIdSeleccionado);
    if (opt.isEmpty()) return;

    var t = opt.get();

    txtIdTicketTickets.setText(t.getId());
    txtFechaTickets.setText(t.getFechaCreacion().toString());
    txtEstadoTickets.setText(t.getEstado().name());

    // Cliente
    seleccionarClienteEnCombo(t.getCliente().getNombre());

    // Domicilio (readonly / editable según quieras)
    txtDireccionTickets.setText(t.getDomicilio().getDireccion());
    txtCiudadTickets.setText(t.getDomicilio().getCiudad());
    txtReferenciaTickets.setText(t.getDomicilio().getReferencia());

    // Problema/Descripción
    cboProblemaTickets.setSelectedItem(t.getTipoProblema());
    txtDescripcionTickets.setText(t.getDescripcion());
}

private void seleccionarClienteEnCombo(String nombreCliente) {
    for (int i = 0; i < cboClienteTickets.getItemCount(); i++) {
        if (String.valueOf(cboClienteTickets.getItemAt(i)).equals(nombreCliente)) {
            cboClienteTickets.setSelectedIndex(i);
            return;
        }
    }
}

private void limpiarFormulario() {
    ticketIdSeleccionado = null;

    txtIdTicketTickets.setText("Automatico");
    txtFechaTickets.setText("Automatico");
    txtEstadoTickets.setText("Automatico");

    if (cboClienteTickets.getItemCount() > 0) cboClienteTickets.setSelectedIndex(0);
    cboProblemaTickets.setSelectedIndex(0);

    txtDireccionTickets.setText("");
    txtCiudadTickets.setText("");
    txtReferenciaTickets.setText("");
    txtDescripcionTickets.setText("");

    tblTickets.clearSelection();
}

private void crearTicket() {
    if (ctx == null) return;

    var cliente = getClienteSeleccionado();
    if (cliente == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
        return;
    }

    String direccion = txtDireccionTickets.getText().trim();
    String ciudad = txtCiudadTickets.getText().trim();
    String referencia = txtReferenciaTickets.getText().trim();

    if (direccion.isBlank() || ciudad.isBlank()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ingrese dirección y ciudad.");
        return;
    }

    String tipoProblema = String.valueOf(cboProblemaTickets.getSelectedItem());
    String descripcion = txtDescripcionTickets.getText().trim();

    if (descripcion.isBlank()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ingrese una descripción.");
        return;
    }

    var domicilio = new proyectofinal.Domicilio(direccion, ciudad, referencia);

    ctx.ticketService.crearTicket(cliente, domicilio, tipoProblema, descripcion);

    javax.swing.JOptionPane.showMessageDialog(this, "Ticket creado.");
    cargarTablaTickets();
    limpiarFormulario();
}

private void actualizarTicket() {
    if (ctx == null) return;

    if (ticketIdSeleccionado == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un ticket de la tabla primero.");
        return;
    }

    var opt = ctx.ticketService.buscarTicketPorId(ticketIdSeleccionado);
    if (opt.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ticket no encontrado.");
        return;
    }

    var t = opt.get();

    String tipoProblema = String.valueOf(cboProblemaTickets.getSelectedItem());
    String descripcion = txtDescripcionTickets.getText().trim();

    if (descripcion.isBlank()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ingrese una descripción.");
        return;
    }

    // Actualización segura según tu modelo
    t.actualizarDatos(tipoProblema, descripcion, "Sistema");

    javax.swing.JOptionPane.showMessageDialog(this, "Ticket actualizado.");
    cargarTablaTickets();
    t.getDomicilio().setDireccion(txtDireccionTickets.getText().trim());
    t.getDomicilio().setCiudad(txtCiudadTickets.getText().trim());
    t.getDomicilio().setReferencia(txtReferenciaTickets.getText().trim());

}



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblIdTicketTickets = new javax.swing.JLabel();
        lblFechaDeCreacionTickets = new javax.swing.JLabel();
        lblEstadoTickets = new javax.swing.JLabel();
        txtIdTicketTickets = new javax.swing.JTextField();
        txtFechaTickets = new javax.swing.JTextField();
        txtEstadoTickets = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblClienteTickets = new javax.swing.JLabel();
        cboClienteTickets = new javax.swing.JComboBox<>();
        btnNuevoClienteTickets = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblDireccionTickets = new javax.swing.JLabel();
        lblCiudadTickets = new javax.swing.JLabel();
        lblReferenciaTickets = new javax.swing.JLabel();
        txtDireccionTickets = new javax.swing.JTextField();
        txtCiudadTickets = new javax.swing.JTextField();
        txtReferenciaTickets = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        lblProblemaDescripcionTickets = new javax.swing.JLabel();
        cboProblemaTickets = new javax.swing.JComboBox<>();
        lblTipoProblemaTickets = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcionTickets = new javax.swing.JTextArea();
        lblDescripcionTickets = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTickets = new javax.swing.JTable();
        btnNuevoTickets = new javax.swing.JButton();
        btnGuardarTickets = new javax.swing.JButton();
        btnActualizarTickets = new javax.swing.JButton();
        btnLimpiarTickets = new javax.swing.JButton();
        btnMenuTickets = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tickets");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Informacion del Ticket");

        lblIdTicketTickets.setText("ID Ticket:");

        lblFechaDeCreacionTickets.setText("Fecha de creacion:");

        lblEstadoTickets.setText("Estado:");

        txtIdTicketTickets.setEditable(false);
        txtIdTicketTickets.setText("Automatico");

        txtFechaTickets.setEditable(false);
        txtFechaTickets.setText("Automatico");

        txtEstadoTickets.setEditable(false);
        txtEstadoTickets.setText("Automatico");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblIdTicketTickets)
                            .addComponent(lblFechaDeCreacionTickets)
                            .addComponent(lblEstadoTickets))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdTicketTickets, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                            .addComponent(txtFechaTickets)
                            .addComponent(txtEstadoTickets))))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIdTicketTickets)
                    .addComponent(txtIdTicketTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaDeCreacionTickets)
                    .addComponent(txtFechaTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstadoTickets)
                    .addComponent(txtEstadoTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Cliente");

        lblClienteTickets.setText("Cliente:");

        cboClienteTickets.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));

        btnNuevoClienteTickets.setText("+ Nuevo Cliente");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jLabel6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblClienteTickets)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNuevoClienteTickets)
                            .addComponent(cboClienteTickets, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClienteTickets)
                    .addComponent(cboClienteTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNuevoClienteTickets)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Domicilio");

        lblDireccionTickets.setText("Direccion");

        lblCiudadTickets.setText("Ciudad:");

        lblReferenciaTickets.setText("Referencia");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(112, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(118, 118, 118))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDireccionTickets)
                    .addComponent(lblCiudadTickets)
                    .addComponent(lblReferenciaTickets))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDireccionTickets)
                    .addComponent(txtCiudadTickets)
                    .addComponent(txtReferenciaTickets))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDireccionTickets)
                    .addComponent(txtDireccionTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCiudadTickets)
                    .addComponent(txtCiudadTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReferenciaTickets)
                    .addComponent(txtReferenciaTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblProblemaDescripcionTickets.setText("Problema + Descripcion");

        cboProblemaTickets.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CTs", "Gateway/Envoy", "Hardware Techo", "Hardware Suelo", "Electrico", "Bateria", "Comunicaciones", "Otro" }));

        lblTipoProblemaTickets.setText("Tipo de Problema:");

        txtDescripcionTickets.setColumns(20);
        txtDescripcionTickets.setRows(5);
        jScrollPane1.setViewportView(txtDescripcionTickets);

        lblDescripcionTickets.setText("Descripcion:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTipoProblemaTickets)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboProblemaTickets, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(lblDescripcionTickets)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblProblemaDescripcionTickets)
                .addGap(95, 95, 95))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProblemaDescripcionTickets)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboProblemaTickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipoProblemaTickets))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDescripcionTickets)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblTickets.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Cliente", "Estado", "Tipo Problema", "Fecha de creacion", "#Visitas"
            }
        ));
        jScrollPane2.setViewportView(tblTickets);

        btnNuevoTickets.setText("Nuevo");

        btnGuardarTickets.setText("Guardar");

        btnActualizarTickets.setText("Actualizar");

        btnLimpiarTickets.setText("Limpiar");

        btnMenuTickets.setText("Menu");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnNuevoTickets)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGuardarTickets)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnActualizarTickets))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnLimpiarTickets)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMenuTickets)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNuevoTickets)
                            .addComponent(btnGuardarTickets)
                            .addComponent(btnActualizarTickets))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpiarTickets)
                            .addComponent(btnMenuTickets)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        java.awt.EventQueue.invokeLater(() -> new Tickets().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarTickets;
    private javax.swing.JButton btnGuardarTickets;
    private javax.swing.JButton btnLimpiarTickets;
    private javax.swing.JButton btnMenuTickets;
    private javax.swing.JButton btnNuevoClienteTickets;
    private javax.swing.JButton btnNuevoTickets;
    private javax.swing.JComboBox<String> cboClienteTickets;
    private javax.swing.JComboBox<String> cboProblemaTickets;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCiudadTickets;
    private javax.swing.JLabel lblClienteTickets;
    private javax.swing.JLabel lblDescripcionTickets;
    private javax.swing.JLabel lblDireccionTickets;
    private javax.swing.JLabel lblEstadoTickets;
    private javax.swing.JLabel lblFechaDeCreacionTickets;
    private javax.swing.JLabel lblIdTicketTickets;
    private javax.swing.JLabel lblProblemaDescripcionTickets;
    private javax.swing.JLabel lblReferenciaTickets;
    private javax.swing.JLabel lblTipoProblemaTickets;
    private javax.swing.JTable tblTickets;
    private javax.swing.JTextField txtCiudadTickets;
    private javax.swing.JTextArea txtDescripcionTickets;
    private javax.swing.JTextField txtDireccionTickets;
    private javax.swing.JTextField txtEstadoTickets;
    private javax.swing.JTextField txtFechaTickets;
    private javax.swing.JTextField txtIdTicketTickets;
    private javax.swing.JTextField txtReferenciaTickets;
    // End of variables declaration//GEN-END:variables
}
