package proyectofinal.igu;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Clientes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Clientes.class.getName());    
    private proyectofinal.AppContext ctx;
    private DefaultTableModel modeloClientes;
    private String idSeleccionado = null;

//Constructores
        public Clientes() {
        initComponents();
        setLocationRelativeTo(null);
        configurarTabla();
        configurarEventos();
        cargarTablaClientes(); 
    }

    public Clientes(proyectofinal.AppContext ctx) {
        initComponents();
        setLocationRelativeTo(null);

        this.ctx = ctx;

        configurarTabla();
        configurarEventos();
        cargarTablaClientes(); // ahora sí carga clientes
    }
 
    //COnfigurar nuestra tabla 
    private void configurarTabla() {
         modeloClientes = new DefaultTableModel(
         new Object[]{"ID", "Nombre", "Telefono", "Email"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // tabla solo lectura
        }
    };
    tblClientes.setModel(modeloClientes);
}
    //Cargar la tabla de clientes que tenemos ya lista en modo semilla Y Tambien los que vayamos creando por el camino
    private void cargarTablaClientes() {
        if (modeloClientes == null) return;

        modeloClientes.setRowCount(0);

        if (ctx == null || ctx.clientes == null) return;

        for (proyectofinal.Cliente c : ctx.clientes) {
        modeloClientes.addRow(new Object[]{
                c.getId(),
                c.getNombre(),
                c.getTelefono(),
                c.getEmail()
        });
    }
}
    //Tabla para que una vez que agreguemos el formulario de borre
    private void limpiarFormulario() {
        idSeleccionado = null;
        txtNombreClientes.setText("");
        txtTelefonoClientes.setText("");
        txtEmailClientes.setText("");
        tblClientes.clearSelection();
        txtNombreClientes.requestFocus();
}
    //Aqui se utiliza clases anonimas
    private proyectofinal.Cliente buscarClientePorId(String id) {
        if (ctx == null || ctx.clientes == null) return null;
        for (proyectofinal.Cliente c : ctx.clientes) {
        if (c.getId().equals(id)) 
        return c;
    }
        return null;
}

    private void configurarEventos() {
        // Click en tabla -> cargar datos a formulario
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) return;
        int row = tblClientes.getSelectedRow();
        if (row < 0) return;

        String id = String.valueOf(tblClientes.getValueAt(row, 0));
        proyectofinal.Cliente c = buscarClientePorId(id);
        if (c == null) return;

        idSeleccionado = c.getId();
        txtNombreClientes.setText(c.getNombre());
        txtTelefonoClientes.setText(c.getTelefono());
        txtEmailClientes.setText(c.getEmail());
    });
}
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblTelefono = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtTelefonoClientes = new javax.swing.JTextField();
        txtNombreClientes = new javax.swing.JTextField();
        txtEmailClientes = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnNuevoClientes = new javax.swing.JButton();
        btnGuardarClientes = new javax.swing.JButton();
        btnActualizarClientes = new javax.swing.JButton();
        btnEliminarClientes = new javax.swing.JButton();
        btnLimpiarClientes = new javax.swing.JButton();
        btnMenuClientes = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Clientes");

        lblNombre.setText("Nombre");

        lblTelefono.setText("Telefono");

        lblEmail.setText("Email");

        txtNombreClientes.addActionListener(this::txtNombreClientesActionPerformed);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Telefono", "Email"
            }
        ));
        jScrollPane1.setViewportView(tblClientes);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNuevoClientes.setText("Nuevo");
        btnNuevoClientes.addActionListener(this::btnNuevoClientesActionPerformed);

        btnGuardarClientes.setText("Guardar");
        btnGuardarClientes.addActionListener(this::btnGuardarClientesActionPerformed);

        btnActualizarClientes.setText("Actualizar");
        btnActualizarClientes.addActionListener(this::btnActualizarClientesActionPerformed);

        btnEliminarClientes.setText("Eliminar");
        btnEliminarClientes.addActionListener(this::btnEliminarClientesActionPerformed);

        btnLimpiarClientes.setText("Limpiar");
        btnLimpiarClientes.addActionListener(this::btnLimpiarClientesActionPerformed);

        btnMenuClientes.setText("Menu");
        btnMenuClientes.addActionListener(this::btnMenuClientesActionPerformed);

        jLabel2.setText("Acciones:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevoClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnActualizarClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addComponent(btnEliminarClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLimpiarClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMenuClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnNuevoClientes))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGuardarClientes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizarClientes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarClientes))
                    .addComponent(btnMenuClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(btnLimpiarClientes))
        );

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/elevation 2.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTelefono)
                            .addComponent(lblEmail)
                            .addComponent(lblNombre))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNombreClientes)
                            .addComponent(txtTelefonoClientes)
                            .addComponent(txtEmailClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(329, 329, 329)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombre)
                            .addComponent(txtNombreClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTelefono)
                            .addComponent(txtTelefonoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmailClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEmail)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(56, 56, 56)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreClientesActionPerformed
        
    }//GEN-LAST:event_txtNombreClientesActionPerformed

    private void btnGuardarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClientesActionPerformed
        // TODO add your handling code here:
        if (ctx == null) {
        JOptionPane.showMessageDialog(this, "CTX no inicializado. Abra desde el menú principal.");
        return;
    }

    String nombre = txtNombreClientes.getText().trim();
    String tel = txtTelefonoClientes.getText().trim();
    String email = txtEmailClientes.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre no puede estar vacío.");
            return;
    }

        proyectofinal.Cliente c = new proyectofinal.Cliente(nombre, tel, email);
        ctx.clientes.add(c);

    cargarTablaClientes();
    limpiarFormulario();
    JOptionPane.showMessageDialog(this, "Cliente guardado."); //Una interfaz locasa de popup para confirmar los cambios :v
    }//GEN-LAST:event_btnGuardarClientesActionPerformed

    private void btnActualizarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarClientesActionPerformed
        // TODO add your handling code here:
        if (ctx == null) {
            JOptionPane.showMessageDialog(this, "CTX no inicializado. Abra desde el menú principal.");
            return;
    }

        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente en la tabla primero.");
            return;
    }

        proyectofinal.Cliente c = buscarClientePorId(idSeleccionado);
            if (c == null) {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            return;
    }

    String nombre = txtNombreClientes.getText().trim();
    String tel = txtTelefonoClientes.getText().trim();
    String email = txtEmailClientes.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre no puede estar vacío.");
            return;
    }

    c.setNombre(nombre);
    c.setTelefono(tel);
    c.setEmail(email);

    cargarTablaClientes();
    JOptionPane.showMessageDialog(this, "Cliente actualizado.");
    }//GEN-LAST:event_btnActualizarClientesActionPerformed

    private void btnEliminarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClientesActionPerformed
        // TODO add your handling code here:
        if (ctx == null) {
        JOptionPane.showMessageDialog(this, "CTX no inicializado. Abra desde el menú principal.");
        return;
    }

        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente en la tabla primero.");
            return;
    }

        int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar cliente seleccionado?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        ctx.clientes.removeIf(c -> c.getId().equals(idSeleccionado));

    cargarTablaClientes();
    limpiarFormulario();
    JOptionPane.showMessageDialog(this, "Cliente eliminado.");
    }//GEN-LAST:event_btnEliminarClientesActionPerformed

    private void btnNuevoClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoClientesActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoClientesActionPerformed

    private void btnLimpiarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarClientesActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnLimpiarClientesActionPerformed

    private void btnMenuClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuClientesActionPerformed
        // TODO add your handling code here:
        new VentanaPrincipal(ctx).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnMenuClientesActionPerformed

    
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
        java.awt.EventQueue.invokeLater(() -> new Clientes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarClientes;
    private javax.swing.JButton btnEliminarClientes;
    private javax.swing.JButton btnGuardarClientes;
    private javax.swing.JButton btnLimpiarClientes;
    private javax.swing.JButton btnMenuClientes;
    private javax.swing.JButton btnNuevoClientes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtEmailClientes;
    private javax.swing.JTextField txtNombreClientes;
    private javax.swing.JTextField txtTelefonoClientes;
    // End of variables declaration//GEN-END:variables
}
