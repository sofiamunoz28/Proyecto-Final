/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.sql.*;
import java.util.HashMap;

public class Cajero extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Cajero.class.getName());


    public Cajero() {
        initComponents();
        sesion.put("entrada", "");
         sesion.put("mensaje", "Seleccione una operación");
         mostrar();
    }
    
    HashMap<String, Object> sesion = new HashMap<>();
    
    public Connection conectar(){
       String usuario="sofia";
       String contrasena="#3Programacion3";
       String url="jdbc:mysql://72.167.84.254:3306/banco";
       Connection con=null;
       try{         
          con= DriverManager.getConnection(url,usuario,contrasena);
          txtPantalla.setText("Conexión correcta");
          return con;
       }catch (Exception e){
         txtPantalla.setText("Conexión incorrecta"); 
          return null;
       }
   }
    
    private void mostrar() {
    txtPantalla.setText(sesion.get("mensaje") + "\n\n> " + sesion.get("entrada"));
}
    
    // Convierte un número a formato con puntos de mil: 20000.0 -> $20.000
private String dinero(double valor) {
    return "$" + String.format(java.util.Locale.of("es", "CO"), "%,.0f", valor);
}
    
    
    private void escribir(String digito) {
            if (sesion.get("operacion") == null) {
        return;
    }
    String entrada = (String) sesion.get("entrada");
    if (entrada.length() < 12) {
        sesion.put("entrada", entrada + digito);
        mostrar();
    }
    }
    
    private void iniciar(String operacion, String mensaje) {
    liberarBloqueos();
    sesion.clear();
    sesion.put("operacion", operacion);
    sesion.put("paso", 1);
    sesion.put("entrada", "");
    sesion.put("mensaje", mensaje);
    mostrar();
     }
    
    private void terminar(String mensaje) {
    liberarBloqueos();
    sesion.remove("operacion");
    sesion.remove("paso");
    sesion.remove("cuenta");
    sesion.remove("destino");
    sesion.remove("cajero");
    sesion.put("entrada", "");
    sesion.put("mensaje", mensaje + "\n\nSeleccione una operación");
    }
    
    private boolean bloquear(Connection con, String tabla, Object id) throws SQLException {
    PreparedStatement ps = con.prepareStatement("UPDATE " + tabla + " SET estado = 1 WHERE id = ? AND estado = 0");
    ps.setObject(1, id);
    return ps.executeUpdate() == 1;
     }
    
    private void liberar(Connection con, String tabla, Object id) throws SQLException {
    PreparedStatement ps = con.prepareStatement("UPDATE " + tabla + " SET estado = 0 WHERE id = ?");
    ps.setObject(1, id);
    ps.executeUpdate();
     }
    
    private void liberarBloqueos() {
    try (Connection con = conectar()) {
        if (sesion.containsKey("lockCliente")) {
            liberar(con, "clientes", sesion.get("lockCliente"));
        }
        if (sesion.containsKey("lockDestino")) {
            liberar(con, "clientes", sesion.get("lockDestino"));
        }
        if (sesion.containsKey("lockCajero")) {
            liberar(con, "cajeros", sesion.get("lockCajero"));
        }
    } catch (SQLException e) {
        System.out.println("Error al liberar: " + e.getMessage());
    }
    sesion.remove("lockCliente");
    sesion.remove("lockDestino");
    sesion.remove("lockCajero");
    }
    
    private HashMap<String, Object> buscarCliente(Connection con, long ncuenta) throws SQLException {
    PreparedStatement ps = con.prepareStatement("SELECT * FROM clientes WHERE ncuenta = ?");
    ps.setLong(1, ncuenta);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
        return null;
    }
    HashMap<String, Object> c = new HashMap<>();
    c.put("id", rs.getInt("id"));
    c.put("nombre", rs.getString("nombre") + " " + rs.getString("apellido"));
    c.put("saldo", rs.getDouble("saldo"));
    return c;
    }
    
    private HashMap<String, Object> buscarCajero(Connection con, long id) throws SQLException {
    PreparedStatement ps = con.prepareStatement("SELECT * FROM cajeros WHERE id = ?");
    ps.setLong(1, id);
    ResultSet rs = ps.executeQuery();
    if (!rs.next()) {
        return null;
    }
    HashMap<String, Object> k = new HashMap<>();
    k.put("id", rs.getInt("id"));
    k.put("10000", rs.getInt("ndiez"));
    k.put("20000", rs.getInt("nveinte"));
    k.put("50000", rs.getInt("ncincuenta"));
    k.put("100000", rs.getInt("ncien"));
    return k;
    }
    
    private HashMap<String, Integer> calcularBilletes(long monto) {
    HashMap<String, Integer> b = new HashMap<>();
    b.put("100000", 0);
    b.put("50000", 0);
    b.put("20000", 0);
    b.put("10000", 0);
    if (monto == 50000) {                 // regla especial: 2 de 20.000 y 1 de 10.000
        b.put("20000", 2);
        b.put("10000", 1);
        return b;
    }
    long resto = monto;
    if (monto >= 100000) {                // regla especial: base de 50.000 + 2x20.000 + 10.000
        b.put("50000", 1);
        b.put("20000", 2);
        b.put("10000", 1);
        resto = monto - 100000;
    }
    // lo que falta se completa con la menor cantidad de billetes (de mayor a menor)
    b.put("100000", b.get("100000") + (int) (resto / 100000));
    resto = resto % 100000;
    b.put("50000", b.get("50000") + (int) (resto / 50000));
    resto = resto % 50000;
    b.put("20000", b.get("20000") + (int) (resto / 20000));
    resto = resto % 20000;
    b.put("10000", b.get("10000") + (int) (resto / 10000));
    return b;
   }
    
    private void aceptar() {
    String operacion = (String) sesion.get("operacion");
    String entrada = (String) sesion.get("entrada");
    if (operacion == null || entrada.isEmpty()) {
        return;
    }
    long numero = Long.parseLong(entrada);
    sesion.put("entrada", "");
    try {
        if (operacion.equals("saldo")) {
            pasoSaldo(numero);
        } else if (operacion.equals("consignar")) {
            pasoConsignar(numero);
        } else if (operacion.equals("retirar")) {
            pasoRetirar(numero);
        } else if (operacion.equals("transferir")) {
            pasoTransferir(numero);
        }
    } catch (Exception e) {
        terminar("Error: " + e.getMessage());
    }
    mostrar();
      }
    
    private void pasoSaldo(long numero) throws SQLException {
    try (Connection con = conectar()) {
        HashMap<String, Object> c = buscarCliente(con, numero);
        if (c == null) {
            terminar("Error: la cuenta no existe");
        } else {
            terminar("Cliente: " + c.get("nombre") + "\nSaldo: $" + c.get("saldo"));
        }
    }
    }
    
    private void pasoConsignar(long numero) throws SQLException {
    int paso = (int) sesion.get("paso");
    try (Connection con = conectar()) {
        if (paso == 1) {
            if (buscarCliente(con, numero) == null) {
                terminar("Error: el cliente no existe");
                return;
            }
            sesion.put("cuenta", numero);
            sesion.put("paso", 2);
            sesion.put("mensaje", "Ingrese el valor a consignar");
        } else {
            if (numero <= 0) {
                sesion.put("mensaje", "Error: el valor debe ser mayor a 0\nIngrese el valor a consignar");
                return;
            }
            HashMap<String, Object> c = buscarCliente(con, (long) sesion.get("cuenta"));
            if (!bloquear(con, "clientes", c.get("id"))) {
                terminar("Error: el cliente está ocupado");
                return;
            }
            sesion.put("lockCliente", c.get("id"));
            PreparedStatement ps = con.prepareStatement("UPDATE clientes SET saldo = saldo + ? WHERE id = ?");
            ps.setLong(1, numero);
            ps.setObject(2, c.get("id"));
            ps.executeUpdate();
            terminar("Consignación exitosa a " + c.get("nombre") + "\nValor: $" + numero);
        }
    }
    }
    
    private void pasoTransferir(long numero) throws SQLException {
    int paso = (int) sesion.get("paso");
    try (Connection con = conectar()) {
        if (paso == 1) {
            if (buscarCliente(con, numero) == null) {
                terminar("Error: la cuenta de origen no existe");
                return;
            }
            sesion.put("cuenta", numero);
            sesion.put("paso", 2);
            sesion.put("mensaje", "Ingrese la cuenta DESTINO");
        } else if (paso == 2) {
            if (numero == (long) sesion.get("cuenta")) {
                terminar("Error: no puede transferir a la misma cuenta");
                return;
            }
            if (buscarCliente(con, numero) == null) {
                terminar("Error: la cuenta destino no existe");
                return;
            }
            sesion.put("destino", numero);
            sesion.put("paso", 3);
            sesion.put("mensaje", "Ingrese el valor a transferir");
        } else {
            if (numero <= 0) {
                sesion.put("mensaje", "Error: el valor debe ser mayor a 0\nIngrese el valor a transferir");
                return;
            }
            HashMap<String, Object> origen = buscarCliente(con, (long) sesion.get("cuenta"));
            HashMap<String, Object> destino = buscarCliente(con, (long) sesion.get("destino"));
            if (!bloquear(con, "clientes", origen.get("id"))) {
                terminar("Error: la cuenta origen está ocupada");
                return;
            }
            sesion.put("lockCliente", origen.get("id"));
            if (!bloquear(con, "clientes", destino.get("id"))) {
                terminar("Error: la cuenta destino está ocupada");
                return;
            }
            sesion.put("lockDestino", destino.get("id"));
            if ((double) origen.get("saldo") < numero) {
                terminar("Error: saldo insuficiente");
                return;
            }
            PreparedStatement resta = con.prepareStatement("UPDATE clientes SET saldo = saldo - ? WHERE id = ?");
            resta.setLong(1, numero);
            resta.setObject(2, origen.get("id"));
            resta.executeUpdate();
            PreparedStatement suma = con.prepareStatement("UPDATE clientes SET saldo = saldo + ? WHERE id = ?");
            suma.setLong(1, numero);
            suma.setObject(2, destino.get("id"));
            suma.executeUpdate();
            terminar("Transferencia exitosa\nDe: " + origen.get("nombre") + "\nA: " + destino.get("nombre")
                    + "\nValor: $" + numero + "\nSu nuevo saldo: $" + ((double) origen.get("saldo") - numero));
        }
    }
    }
    
    private void pasoRetirar(long numero) throws SQLException {
    int paso = (int) sesion.get("paso");
    try (Connection con = conectar()) {
        if (paso == 1) {                                   // número de cuenta
            if (buscarCliente(con, numero) == null) {
                terminar("Error: la cuenta no existe");
                return;
            }
            sesion.put("cuenta", numero);
            sesion.put("paso", 2);
            sesion.put("mensaje", "Ingrese el número del cajero");
        } else if (paso == 2) {                            // número de cajero: aquí se bloquean ambos
            HashMap<String, Object> cliente = buscarCliente(con, (long) sesion.get("cuenta"));
            HashMap<String, Object> cajero = buscarCajero(con, numero);
            if (cajero == null) {
                terminar("Error: el cajero no existe");
                return;
            }
            if (!bloquear(con, "clientes", cliente.get("id"))) {
                terminar("Error: el cliente está ocupado");
                return;
            }
            sesion.put("lockCliente", cliente.get("id"));
            if (!bloquear(con, "cajeros", cajero.get("id"))) {
                terminar("Error: el cajero está ocupado");
                return;
            }
            sesion.put("lockCajero", cajero.get("id"));
            sesion.put("cajero", numero);
            sesion.put("paso", 3);
            sesion.put("mensaje", "Bienvenido(a) " + cliente.get("nombre")
                    + "\nIngrese el valor a retirar\n(múltiplos de 10.000, mínimo 10.000, máximo 1.000.000)");
        } else {                                           // monto
            retirarDinero(con, numero);
        }
    }
    }
    
    private void retirarDinero(Connection con, long monto) throws SQLException {
    if (monto % 10000 != 0 || monto < 10000 || monto > 1000000) {
        sesion.put("mensaje", "Error: el valor debe ser múltiplo de 10.000, mínimo 10.000 y máximo 1.000.000\nIngrese el valor de nuevo");
        return;
    }
    HashMap<String, Object> cliente = buscarCliente(con, (long) sesion.get("cuenta"));
    HashMap<String, Object> cajero = buscarCajero(con, (long) sesion.get("cajero"));

    long totalCajero = 10000L * (int) cajero.get("10000") + 20000L * (int) cajero.get("20000")
                     + 50000L * (int) cajero.get("50000") + 100000L * (int) cajero.get("100000");

    if ((double) cliente.get("saldo") < monto) {
        terminar("Error: saldo insuficiente en la cuenta");
        return;
    }
    if (totalCajero < monto) {
        terminar("Error: el cajero no tiene dinero suficiente");
        return;
    }
    HashMap<String, Integer> b = calcularBilletes(monto);
    if (b.get("10000") > (int) cajero.get("10000") || b.get("20000") > (int) cajero.get("20000")
            || b.get("50000") > (int) cajero.get("50000") || b.get("100000") > (int) cajero.get("100000")) {
        terminar("Error: el cajero no tiene los billetes necesarios");
        return;
    }

    PreparedStatement ps = con.prepareStatement(
        "UPDATE cajeros SET ndiez = ndiez - ?, nveinte = nveinte - ?, ncincuenta = ncincuenta - ?, ncien = ncien - ? WHERE id = ?");
    ps.setInt(1, b.get("10000"));
    ps.setInt(2, b.get("20000"));
    ps.setInt(3, b.get("50000"));
    ps.setInt(4, b.get("100000"));
    ps.setObject(5, cajero.get("id"));
    ps.executeUpdate();

    PreparedStatement ps2 = con.prepareStatement("UPDATE clientes SET saldo = saldo - ? WHERE id = ?");
    ps2.setLong(1, monto);
    ps2.setObject(2, cliente.get("id"));
    ps2.executeUpdate();

    terminar("Retiro satisfactorio\nBilletes entregados:\n"
        + "  $100.000 x " + b.get("100000") + "\n"
        + "  $50.000 x " + b.get("50000") + "\n"
        + "  $20.000 x " + b.get("20000") + "\n"
        + "  $10.000 x " + b.get("10000") + "\n"
        + "Saldo restante: $" + ((double) cliente.get("saldo") - monto));
}
    
    



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtPantalla = new javax.swing.JTextArea();
        btn1 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();
        btn5 = new javax.swing.JButton();
        btn6 = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        btn8 = new javax.swing.JButton();
        btn9 = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btn0 = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnSaldo = new javax.swing.JButton();
        btnConsignar = new javax.swing.JButton();
        btnRetirar = new javax.swing.JButton();
        btnTransferir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtPantalla.setEditable(false);
        txtPantalla.setColumns(20);
        txtPantalla.setRows(5);
        jScrollPane1.setViewportView(txtPantalla);

        btn1.setText("1");
        btn1.addActionListener(this::btn1ActionPerformed);

        btn2.setText("2");
        btn2.addActionListener(this::btn2ActionPerformed);

        btn3.setText("3");
        btn3.addActionListener(this::btn3ActionPerformed);

        btn4.setText("4");
        btn4.addActionListener(this::btn4ActionPerformed);

        btn5.setText("5");
        btn5.addActionListener(this::btn5ActionPerformed);

        btn6.setText("6");
        btn6.addActionListener(this::btn6ActionPerformed);

        btn7.setText("7");
        btn7.addActionListener(this::btn7ActionPerformed);

        btn8.setText("8");
        btn8.addActionListener(this::btn8ActionPerformed);

        btn9.setText("9");
        btn9.addActionListener(this::btn9ActionPerformed);

        btnBorrar.setText("Borrar");
        btnBorrar.addActionListener(this::btnBorrarActionPerformed);

        btn0.setText("0");
        btn0.addActionListener(this::btn0ActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(this::btnAceptarActionPerformed);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        btnSaldo.setText("Consultar Saldo");
        btnSaldo.addActionListener(this::btnSaldoActionPerformed);

        btnConsignar.setText("Consignar");
        btnConsignar.addActionListener(this::btnConsignarActionPerformed);

        btnRetirar.setText("Retirar");
        btnRetirar.addActionListener(this::btnRetirarActionPerformed);

        btnTransferir.setText("Transferir");
        btnTransferir.addActionListener(this::btnTransferirActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btn7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn0, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn8, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLimpiar))
                .addGap(88, 88, 88))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(btnSaldo)
                                .addGap(36, 36, 36)
                                .addComponent(btnConsignar))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnAceptar)
                                .addGap(33, 33, 33)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(btnRetirar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnTransferir))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(btnCancelar)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(34, 34, 34))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn1)
                    .addComponent(btn2)
                    .addComponent(btn3))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn4)
                    .addComponent(btn5)
                    .addComponent(btn6))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn7)
                    .addComponent(btn8)
                    .addComponent(btn9))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBorrar)
                    .addComponent(btn0)
                    .addComponent(btnLimpiar))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptar)
                    .addComponent(btnCancelar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaldo)
                    .addComponent(btnConsignar)
                    .addComponent(btnRetirar)
                    .addComponent(btnTransferir))
                .addGap(55, 55, 55))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn9ActionPerformed
        escribir("9");
    }//GEN-LAST:event_btn9ActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        escribir("1");
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        escribir("2");
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
      escribir("3");
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        escribir("4");
    }//GEN-LAST:event_btn4ActionPerformed

    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        escribir("5");
    }//GEN-LAST:event_btn5ActionPerformed

    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
       escribir("6");
    }//GEN-LAST:event_btn6ActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        escribir("7");
    }//GEN-LAST:event_btn7ActionPerformed

    private void btn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn8ActionPerformed
        escribir("8");
    }//GEN-LAST:event_btn8ActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
       String entrada = (String) sesion.get("entrada");
          if (!entrada.isEmpty()) {
    sesion.put("entrada", entrada.substring(0, entrada.length() - 1));
    mostrar();
          }
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
       sesion.put("entrada", "");
        mostrar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        aceptar();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
       terminar("Operación cancelada");
       mostrar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSaldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaldoActionPerformed
        iniciar("saldo", "Ingrese su número de cuenta");
    }//GEN-LAST:event_btnSaldoActionPerformed

    private void btnConsignarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsignarActionPerformed
       iniciar("consignar", "Ingrese el número de cuenta a consignar");
    }//GEN-LAST:event_btnConsignarActionPerformed

    private void btnRetirarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetirarActionPerformed
        iniciar("retirar", "Ingrese su número de cuenta");
    }//GEN-LAST:event_btnRetirarActionPerformed

    private void btnTransferirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferirActionPerformed
       iniciar("transferir", "Ingrese su número de cuenta (origen)");
    }//GEN-LAST:event_btnTransferirActionPerformed

    private void btn0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn0ActionPerformed
       escribir("0");
    }//GEN-LAST:event_btn0ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Cajero().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn0;
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btn8;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConsignar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRetirar;
    private javax.swing.JButton btnSaldo;
    private javax.swing.JButton btnTransferir;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtPantalla;
    // End of variables declaration//GEN-END:variables

   

   
}
