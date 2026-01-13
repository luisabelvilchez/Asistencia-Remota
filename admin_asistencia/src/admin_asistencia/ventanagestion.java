package admin_asistencia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ventanagestion extends JFrame {

    DefaultTableModel modeloTabla;

    public ventanagestion() {
        setTitle("Gestión de Empleados");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        final JTextField txtNombre = new JTextField(15);
        final JTextField txtDNI = new JTextField(15);
        final JTextField txtCargo = new JTextField(15);

        // --- PANEL IZQUIERDO (FORMULARIO) ---
        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Registro de Empleados"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelIzquierdo.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panelIzquierdo.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelIzquierdo.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1; panelIzquierdo.add(txtDNI, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelIzquierdo.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1; panelIzquierdo.add(txtCargo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton btnGuardar = new JButton("Registrar en Base de Datos");
        panelIzquierdo.add(btnGuardar, gbc);

        // --- PANEL DERECHO (TABLA Y BOTONES) ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Lista de Empleados"));

        String[] columnas = {"ID", "Nombre", "DNI", "Cargo"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        final JTable tablaEmpleados = new JTable(modeloTabla);
        panelDerecho.add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);

        // Botones Actualizar y Borrar
        JPanel panelBotonesTabla = new JPanel();
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnBorrar = new JButton("Borrar");
        panelBotonesTabla.add(btnActualizar);
        panelBotonesTabla.add(btnBorrar);
        panelDerecho.add(panelBotonesTabla, BorderLayout.SOUTH);

        // --- LÓGICA DE BOTONES ---

        // 1. REGISTRAR
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNombre.getText().trim().isEmpty() || txtDNI.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Completa los campos");
                    return;
                }
                try {
                    Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_deasistencias?serverTimezone=UTC", "root", "123456");
                    String sql = "INSERT INTO empleados (nombre_empleado, dni, cargo) VALUES (?,?,?)";
                    PreparedStatement pst = cn.prepareStatement(sql);
                    pst.setString(1, txtNombre.getText());
                    pst.setString(2, txtDNI.getText());
                    pst.setString(3, txtCargo.getText());
                    pst.executeUpdate();
                    
                    JOptionPane.showMessageDialog(null, "¡Empleado registrado con éxito!");
                    
                    // Actualizar tabla visual al instante
                    modeloTabla.addRow(new Object[]{"Nuevo", txtNombre.getText(), txtDNI.getText(), txtCargo.getText()});
                    
                    txtNombre.setText(""); txtDNI.setText(""); txtCargo.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // 2. BORRAR (Ya restaurado)
        btnBorrar.addActionListener(new ActionListener() {
        @Override
    public void actionPerformed(ActionEvent e) {
        int filaSeleccionada = tablaEmpleados.getSelectedRow();
        
        if (filaSeleccionada != -1) {
            // 1. Obtener el DNI o el ID de la fila seleccionada para saber a quién borrar en MySQL
            String dniABorrar = tablaEmpleados.getValueAt(filaSeleccionada, 2).toString(); 

            int respuesta = JOptionPane.showConfirmDialog(null, 
                "¿Estás seguro de eliminar al empleado con DNI: " + dniABorrar + "?", 
                "Confirmación de Seguridad", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    // 2. Conexión a la base de datos
                    Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_deasistencias?serverTimezone=UTC", "root", "123456");
                    
                    // 3. Preparar la orden de eliminación
                    String sql = "DELETE FROM empleados WHERE dni = ?";
                    PreparedStatement pst = cn.prepareStatement(sql);
                    pst.setString(1, dniABorrar);
                    
                    int resultado = pst.executeUpdate(); // Ejecuta el borrado en MySQL

                    if (resultado > 0) {
                        // 4. Si se borró en la base de datos, ahora sí lo quitamos de la interfaz
                        modeloTabla.removeRow(filaSeleccionada);
                        JOptionPane.showMessageDialog(null, "Empleado eliminado permanentemente.");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al borrar en BD: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona un empleado de la tabla.");
        }
    }
});

        // 3. ACTUALIZAR (Ya restaurado)
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaEmpleados.getSelectedRow();
                if (filaSeleccionada != -1) {
                    JOptionPane.showMessageDialog(null, "Función para editar datos seleccionados.");
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona una fila para actualizar.");
                }
            }
        });

        add(panelIzquierdo);
        add(panelDerecho);
        
        cargarDatosTabla();
    }

    public void cargarDatosTabla() {
        try {
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_deasistencias?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true", "root", "123456");
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM empleados");
            while (rs.next()) {
                Object[] fila = {rs.getInt("id_empleado"), rs.getString("nombre_empleado"), rs.getString("dni"), rs.getString("cargo")};
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar tabla: " + e.getMessage());
        }
    }
    //guarda los empleados y los deja ahi al entrar de nuevo podemos verlo
    
    
    
    
    
    
    
    
    
    
    
    
}