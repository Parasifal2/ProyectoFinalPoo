
package proyectofinal.igu;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Tecnicos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tecnicos.class.getName());

    private proyectofinal.AppContext ctx;
    private DefaultTableModel modeloTecnicos;
    private String idSeleccionado = null;

public Tecnicos() {
    initComponents();
    setLocationRelativeTo(null);

    configurarTabla();
    configurarEventos();
    cargarTablaTecnicos();
    limpiarFormulario();
}

public Tecnicos(proyectofinal.AppContext ctx) {
    initComponents();
    setLocationRelativeTo(null);

    this.ctx = ctx;

    configurarTabla();
    configurarEventos();
    cargarTablaTecnicos();
    limpiarFormulario();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblTecnicos = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblTelefono = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblEspecialidad = new javax.swing.JLabel();
        txtIdTecnicos = new javax.swing.JTextField();
        txtNombreTecnicos = new javax.swing.JTextField();
        txtTelefonoTecnicos = new javax.swing.JTextField();
        txtEmailTecnicos = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTecnicos = new javax.swing.JTable();
        cboEspecialidadTecnicos = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnNuevoTecnicos = new javax.swing.JButton();
        btnGuardarTecnicos = new javax.swing.JButton();
        btnActualizarTecnicos = new javax.swing.JButton();
        btnLimpiarTecnicos = new javax.swing.JButton();
        btnEliminarTecnicos = new javax.swing.JButton();
        btnMenuTecnicos = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Clientes");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTecnicos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTecnicos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTecnicos.setText("Tecnicos");

        lblId.setText("ID:");

        lblNombre.setText("Nombre:");

        lblTelefono.setText("Telefono:");

        lblEmail.setText("Email:");

        lblEspecialidad.setText("Especialidad:");

        txtIdTecnicos.setEditable(false);
        txtIdTecnicos.setText("Automatico");

        tblTecnicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Telefono", "Email", "Especialidad"
            }
        ));
        jScrollPane1.setViewportView(tblTecnicos);

        cboEspecialidadTecnicos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione...", "Electricista", "Instalador", "Bateria", "Todo: No baterias", "Todo: Baterias" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNuevoTecnicos.setText("Nuevo");
        btnNuevoTecnicos.addActionListener(this::btnNuevoTecnicosActionPerformed);

        btnGuardarTecnicos.setText("Guardar");
        btnGuardarTecnicos.addActionListener(this::btnGuardarTecnicosActionPerformed);

        btnActualizarTecnicos.setText("Actualizar");
        btnActualizarTecnicos.addActionListener(this::btnActualizarTecnicosActionPerformed);

        btnLimpiarTecnicos.setText("Limpiar");
        btnLimpiarTecnicos.addActionListener(this::btnLimpiarTecnicosActionPerformed);

        btnEliminarTecnicos.setText("Eliminar");
        btnEliminarTecnicos.addActionListener(this::btnEliminarTecnicosActionPerformed);

        btnMenuTecnicos.setText("Menu");
        btnMenuTecnicos.addActionListener(this::btnMenuTecnicosActionPerformed);

        jLabel2.setText("Acciones");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGuardarTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnNuevoTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnLimpiarTecnicos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnActualizarTecnicos, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(btnEliminarTecnicos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(60, 60, 60)
                                .addComponent(btnMenuTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnNuevoTecnicos))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardarTecnicos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnActualizarTecnicos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLimpiarTecnicos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarTecnicos))
                    .addComponent(btnMenuTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/elevation 2.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblNombre)
                            .addComponent(lblId)
                            .addComponent(lblTelefono)
                            .addComponent(lblEmail)
                            .addComponent(lblEspecialidad))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtIdTecnicos, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                .addComponent(txtNombreTecnicos)
                                .addComponent(txtTelefonoTecnicos)
                                .addComponent(txtEmailTecnicos))
                            .addComponent(cboEspecialidadTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(lblTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(56, 56, 56)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 955, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblId)
                                    .addComponent(txtIdTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblNombre)
                                    .addComponent(txtNombreTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTelefono)
                                    .addComponent(txtTelefonoTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblEmail)
                                    .addComponent(txtEmailTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblEspecialidad)
                                    .addComponent(cboEspecialidadTecnicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoTecnicosActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoTecnicosActionPerformed

    private void btnGuardarTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarTecnicosActionPerformed
        // TODO add your handling code here:
        if (ctx == null) {
        JOptionPane.showMessageDialog(this, "CTX no inicializado. Abra desde el menú principal.");
        return;
    }

    String nombre = txtNombreTecnicos.getText().trim();
    String tel = txtTelefonoTecnicos.getText().trim();
    String email = txtEmailTecnicos.getText().trim();
    String esp = especialidadSeleccionada();

    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nombre no puede estar vacío.");
        return;
    }
    if (esp.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
        return;
    }

    proyectofinal.Tecnico t = new proyectofinal.Tecnico(nombre, tel, email, esp);
    ctx.tecnicos.add(t);

    cargarTablaTecnicos();
    limpiarFormulario();
    JOptionPane.showMessageDialog(this, "Tecnico guardado.");
    }//GEN-LAST:event_btnGuardarTecnicosActionPerformed

    private void btnActualizarTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarTecnicosActionPerformed
        // TODO add your handling code here:
        if (ctx == null) {
        JOptionPane.showMessageDialog(this, "CTX no inicializado. Abra desde el menú principal.");
        return;
    }

    if (idSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un técnico en la tabla primero.");
        return;
    }

    proyectofinal.Tecnico t = buscarTecnicoPorId(idSeleccionado);
    if (t == null) {
        JOptionPane.showMessageDialog(this, "Técnico no encontrado.");
        return;
    }

    String nombre = txtNombreTecnicos.getText().trim();
    String tel = txtTelefonoTecnicos.getText().trim();
    String email = txtEmailTecnicos.getText().trim();
    String esp = especialidadSeleccionada();

    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nombre no puede estar vacío.");
        return;
    }
    if (esp.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
        return;
    }

    t.setNombre(nombre);
    t.setTelefono(tel);
    t.setEmail(email);
    t.setEspecialidad(esp);

    cargarTablaTecnicos();
    JOptionPane.showMessageDialog(this, "Tecnico actualizado.");
    }//GEN-LAST:event_btnActualizarTecnicosActionPerformed

    private void btnLimpiarTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarTecnicosActionPerformed
        // TODO add your handling code here:
            limpiarFormulario();

    }//GEN-LAST:event_btnLimpiarTecnicosActionPerformed

    private void btnEliminarTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTecnicosActionPerformed
        // TODO add your handling code here:
            if (ctx == null) {
        JOptionPane.showMessageDialog(this, "CTX no inicializado. Abra desde el menú principal.");
        return;
    }

    if (idSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un técnico en la tabla primero.");
        return;
    }

    int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar técnico seleccionado?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);

    if (ok != JOptionPane.YES_OPTION) return;

    ctx.tecnicos.removeIf(t -> t.getId().equals(idSeleccionado));

    cargarTablaTecnicos();
    limpiarFormulario();
    JOptionPane.showMessageDialog(this, "Tecnico eliminado.");
    }//GEN-LAST:event_btnEliminarTecnicosActionPerformed

    private void btnMenuTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuTecnicosActionPerformed
        // TODO add your handling code here:
            new VentanaPrincipal(ctx).setVisible(true);
            this.dispose();
    }//GEN-LAST:event_btnMenuTecnicosActionPerformed
    
    private void configurarTabla() {
    modeloTecnicos = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Telefono", "Email", "Especialidad"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tblTecnicos.setModel(modeloTecnicos);
}

private void cargarTablaTecnicos() {
    if (modeloTecnicos == null) return;

    modeloTecnicos.setRowCount(0);

    if (ctx == null || ctx.tecnicos == null) return;

    for (proyectofinal.Tecnico t : ctx.tecnicos) {
        modeloTecnicos.addRow(new Object[]{
                t.getId(),
                t.getNombre(),
                t.getTelefono(),
                t.getEmail(),
                t.getEspecialidad()
        });
    }
}

private void limpiarFormulario() {
    idSeleccionado = null;
    txtIdTecnicos.setText("Automatico");
    txtNombreTecnicos.setText("");
    txtTelefonoTecnicos.setText("");
    txtEmailTecnicos.setText("");
    cboEspecialidadTecnicos.setSelectedIndex(0);
    tblTecnicos.clearSelection();
    txtNombreTecnicos.requestFocus();
}

private proyectofinal.Tecnico buscarTecnicoPorId(String id) {
    if (ctx == null || ctx.tecnicos == null) return null;
    for (proyectofinal.Tecnico t : ctx.tecnicos) {
        if (t.getId().equals(id)) return t;
    }
    return null;
}

private String especialidadSeleccionada() {
    Object sel = cboEspecialidadTecnicos.getSelectedItem();
    if (sel == null) return "";
    String esp = sel.toString().trim();
    if (esp.equalsIgnoreCase("Seleccione...")) return "";
    return esp;
}

private void configurarEventos() {
    tblTecnicos.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) return;
        int row = tblTecnicos.getSelectedRow();
        if (row < 0) return;

        String id = String.valueOf(tblTecnicos.getValueAt(row, 0));
        proyectofinal.Tecnico t = buscarTecnicoPorId(id);
        if (t == null) return;

        idSeleccionado = t.getId();
        txtIdTecnicos.setText(t.getId());
        txtNombreTecnicos.setText(t.getNombre());
        txtTelefonoTecnicos.setText(t.getTelefono());
        txtEmailTecnicos.setText(t.getEmail());

       
        cboEspecialidadTecnicos.setSelectedItem(t.getEspecialidad());
    });
}

   
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
        java.awt.EventQueue.invokeLater(() -> new Tecnicos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarTecnicos;
    private javax.swing.JButton btnEliminarTecnicos;
    private javax.swing.JButton btnGuardarTecnicos;
    private javax.swing.JButton btnLimpiarTecnicos;
    private javax.swing.JButton btnMenuTecnicos;
    private javax.swing.JButton btnNuevoTecnicos;
    private javax.swing.JComboBox<String> cboEspecialidadTecnicos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEspecialidad;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTecnicos;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JTable tblTecnicos;
    private javax.swing.JTextField txtEmailTecnicos;
    private javax.swing.JTextField txtIdTecnicos;
    private javax.swing.JTextField txtNombreTecnicos;
    private javax.swing.JTextField txtTelefonoTecnicos;
    // End of variables declaration//GEN-END:variables
}
