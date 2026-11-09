/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.sql.*;
import java.util.HashMap;

public class Administracion extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Administracion.class.getName());

    public Administracion() {
        initComponents();
    }
    
           public Connection conectar(){
       String usuario="sofia";
       String contrasena="#3Programacion3";
       String url="jdbc:mysql://72.167.84.254:3306/banco";
       Connection con=null;
       try{         
          con= DriverManager.getConnection(url,usuario,contrasena);
          txtSalida.setText("Conexión correcta");
          return con;
       }catch (Exception e){
         txtSalida.setText("Conexión incorrecta"); 
          return null;
       }
   }
           private boolean bloquear(Connection con, String tabla, int id) throws SQLException {
                 PreparedStatement ps = con.prepareStatement("UPDATE " + tabla + " SET estado = 1 WHERE id = ? AND estado = 0");
                  ps.setInt(1, id);
                  return ps.executeUpdate() == 1;
           }
           
           private void liberar(Connection con, String tabla, int id) throws SQLException {
                   PreparedStatement ps = con.prepareStatement("UPDATE " + tabla + " SET estado = 0 WHERE id = ?");
                   ps.setInt(1, id);
                   ps.executeUpdate();
           }
           
           private HashMap<String, Object> leerCliente() {
               HashMap<String, Object> c = new HashMap<>();
               c.put("nombre", txtNombre.getText());
               c.put("apellido", txtApellido.getText());
               c.put("telefono", txtTelefono.getText());
               c.put("ciudad", txtCiudad.getText());
               c.put("ncuenta", Long.parseLong(txtCuenta.getText()));
               c.put("saldo", Double.parseDouble(txtSaldo.getText()));
               return c;
           }
           private void mostrarClientes(PreparedStatement ps) throws SQLException {
               ResultSet rs = ps.executeQuery();
               String texto = "";
               while (rs.next()) {
                    texto += "ID: " + rs.getInt("id") + " | " + rs.getString("nombre") + " " + rs.getString("apellido")
                       + " | Tel: " + rs.getString("telefono") + " | Ciudad: " + rs.getString("ciudad")
                       + " | Cuenta: " + rs.getInt("ncuenta") + " | Saldo: " + rs.getDouble("saldo")
                       + " | Estado: " + rs.getInt("estado") + "\n";
                }
                 if (texto.isEmpty()) {
                     texto = "No se encontraron clientes";
                    }
                 txtSalida.setText(texto);
           }
           
           private void cambiarSaldo(boolean retiro) {
               try (Connection con = conectar()) {
               int id = Integer.parseInt(txtId.getText());
               double monto = Double.parseDouble(txtMonto.getText());
                if (monto <= 0) {
                    txtSalida.setText("El monto debe ser mayor a 0");
                      return;
                }
                if (!bloquear(con, "clientes", id)) {
                    txtSalida.setText("El cliente no existe o está ocupado");
                    return;
                }
                try {
            int filas;
            if (retiro) {
                PreparedStatement ps = con.prepareStatement("UPDATE clientes SET saldo = saldo - ? WHERE id = ? AND saldo >= ?");
                ps.setDouble(1, monto);
                ps.setInt(2, id);
                ps.setDouble(3, monto);
                filas = ps.executeUpdate();
            } else {
                PreparedStatement ps = con.prepareStatement("UPDATE clientes SET saldo = saldo + ? WHERE id = ?");
                ps.setDouble(1, monto);
                ps.setInt(2, id);
                filas = ps.executeUpdate();
                }
            txtSalida.setText(filas == 1 ? "Operación exitosa" : "Saldo insuficiente");
             } finally {
            liberar(con, "clientes", id);
                      }
            } catch (Exception e) {
                   txtSalida.setText("Error: " + e.getMessage());
                 }
            }
           private void modificarCajero(String sql, boolean validar) {
                   try (Connection con = conectar()) {
        int id = Integer.parseInt(txtCajId.getText());
        int diez = Integer.parseInt(txtDiez.getText());
        int veinte = Integer.parseInt(txtVeinte.getText());
        int cincuenta = Integer.parseInt(txtCincuenta.getText());
        int cien = Integer.parseInt(txtCien.getText());
        if (!bloquear(con, "cajeros", id)) {
            txtSalida.setText("El cajero no existe o está ocupado");
            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, diez);
            ps.setInt(2, veinte);
            ps.setInt(3, cincuenta);
            ps.setInt(4, cien);
            ps.setInt(5, id);
            if (validar) {
                ps.setInt(6, diez);
                ps.setInt(7, veinte);
                ps.setInt(8, cincuenta);
                ps.setInt(9, cien);
            }
            int filas = ps.executeUpdate();
            txtSalida.setText(filas == 1 ? "Operación exitosa" : "El cajero no tiene suficientes billetes");
        } finally {
            liberar(con, "cajeros", id);
        }
    } catch (Exception e) {
        txtSalida.setText("Error: " + e.getMessage());
    }
           }

          
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtCiudad = new javax.swing.JTextField();
        txtCuenta = new javax.swing.JTextField();
        txtSaldo = new javax.swing.JTextField();
        txtMonto = new javax.swing.JTextField();
        btnListar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscarNombre = new javax.swing.JButton();
        btnBuscarId = new javax.swing.JButton();
        btnCosignar = new javax.swing.JButton();
        btnRetirar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtCajId = new javax.swing.JTextField();
        txtDiez = new javax.swing.JTextField();
        txtVeinte = new javax.swing.JTextField();
        txtCincuenta = new javax.swing.JTextField();
        txtCien = new javax.swing.JTextField();
        btnListarCajeros = new javax.swing.JButton();
        btnAgregarCajero = new javax.swing.JButton();
        btnEditarCajero = new javax.swing.JButton();
        btnEliminarCajero = new javax.swing.JButton();
        btnAgregarBilletes = new javax.swing.JButton();
        btnQuitarBilletes = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        btnResumen = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSalida = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("CLIENTES ");

        jLabel2.setText("Id:");

        jLabel3.setText("Nombre:");

        jLabel4.setText("Apellido:");

        jLabel5.setText("Telefono:");

        jLabel6.setText("Ciudad:");

        jLabel7.setText("Cuenta:");

        jLabel8.setText("Saldo:");

        jLabel9.setText("Monto:");

        txtId.addActionListener(this::txtIdActionPerformed);

        btnListar.setText("Listar");

        btnAgregar.setText("Agregar");

        btnEditar.setText("Editar");

        btnEliminar.setText("Eliminar");

        btnBuscarNombre.setText("Buscar Nombre");

        btnBuscarId.setText("Buscar Id");

        btnCosignar.setText("Consignar");

        btnRetirar.setText("Retirar");

        jLabel10.setText("CAJEROS:");

        jLabel11.setText("Id Cajero:");

        jLabel12.setText("$ 10.000 :");

        jLabel13.setText("$ 20.000 :");

        jLabel14.setText("$  50.000 :");

        jLabel15.setText("$ 100.000 :");

        btnListarCajeros.setText("Listar");

        btnAgregarCajero.setText("Agregar");

        btnEditarCajero.setText("Editar");

        btnEliminarCajero.setText("Eliminar");

        btnAgregarBilletes.setText("Agregar Billetes ");

        btnQuitarBilletes.setText("Quitar Billetes");

        jLabel16.setText("RESUMEN:");

        btnResumen.setText("Resumen");
        btnResumen.addActionListener(this::btnResumenActionPerformed);

        txtSalida.setEditable(false);
        txtSalida.setColumns(20);
        txtSalida.setRows(5);
        jScrollPane2.setViewportView(txtSalida);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBuscarNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBuscarId))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCosignar)
                        .addGap(18, 18, 18)
                        .addComponent(btnRetirar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6)))
                                        .addGap(24, 24, 24)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(163, 163, 163))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(txtCuenta)
                                                    .addComponent(txtCiudad, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtApellido, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtId))
                                                .addGap(113, 113, 113))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnAgregar)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addGap(16, 16, 16)
                                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(txtMonto, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                                                    .addComponent(txtSaldo)))
                                            .addComponent(btnEliminar))
                                        .addGap(113, 113, 113))))
                            .addComponent(btnListar)
                            .addComponent(btnEditar))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnQuitarBilletes, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(btnAgregarBilletes, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(jLabel10))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnResumen)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEditarCajero)
                                .addGap(43, 43, 43)
                                .addComponent(btnEliminarCajero))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnListarCajeros)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                    .addComponent(btnAgregarCajero))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel11)
                                        .addComponent(jLabel13)
                                        .addComponent(jLabel14)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtCajId)
                                        .addComponent(txtDiez)
                                        .addComponent(txtVeinte)
                                        .addComponent(txtCincuenta)
                                        .addComponent(txtCien, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel10))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(243, 243, 243)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel8))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnListar)
                                    .addComponent(btnAgregar))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(26, 26, 26)
                                        .addComponent(btnEditar))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(btnEliminar)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnBuscarNombre)
                                    .addComponent(btnBuscarId)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnListarCajeros)
                                    .addComponent(btnAgregarCajero))
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEditarCajero)
                                    .addComponent(btnEliminarCajero))
                                .addGap(19, 19, 19)
                                .addComponent(btnAgregarBilletes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnQuitarBilletes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addComponent(jLabel16)
                                .addGap(15, 15, 15)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCosignar)
                                .addComponent(btnRetirar))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(btnResumen))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(txtCajId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(txtDiez, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(txtVeinte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(txtCincuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(txtCien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void btnResumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResumenActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Administracion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAgregarBilletes;
    private javax.swing.JButton btnAgregarCajero;
    private javax.swing.JButton btnBuscarId;
    private javax.swing.JButton btnBuscarNombre;
    private javax.swing.JButton btnCosignar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEditarCajero;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarCajero;
    private javax.swing.JButton btnListar;
    private javax.swing.JButton btnListarCajeros;
    private javax.swing.JButton btnQuitarBilletes;
    private javax.swing.JButton btnResumen;
    private javax.swing.JButton btnRetirar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtCajId;
    private javax.swing.JTextField txtCien;
    private javax.swing.JTextField txtCincuenta;
    private javax.swing.JTextField txtCiudad;
    private javax.swing.JTextField txtCuenta;
    private javax.swing.JTextField txtDiez;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtSaldo;
    private javax.swing.JTextArea txtSalida;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtVeinte;
    // End of variables declaration//GEN-END:variables
}
