package asistencia_remota;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AsistenciaApp extends JFrame {

    private JTextField txtNombre, txtDni;
    private JComboBox<String> comboTipo;
    private JButton btnRegistrar;

    public AsistenciaApp() {
        // 1. Configuración básica
        setTitle("Registro de Asistencia - Empleado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 40, 40, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- DISEÑO DEL FORMULARIO ---
        JLabel lblTitulo = new JLabel("REGISTRO DE ASISTENCIA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 70));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Nombre del Empleado:"), gbc);
        txtNombre = new JTextField(30);
        gbc.gridx = 1;
        add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("DNI del Empleado:"), gbc);
        txtDni = new JTextField(20);
        gbc.gridx = 1;
        add(txtDni, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Tipo de Registro:"), gbc);
        comboTipo = new JComboBox<>(new String[]{"ENTRADA", "SALIDA"});
        gbc.gridx = 1;
        add(comboTipo, gbc);

        btnRegistrar = new JButton("MARCAR ASISTENCIA");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(new Color(52, 152, 219));
        btnRegistrar.setForeground(Color.WHITE);
        
        // --- AQUÍ ESTÁ LA LÓGICA DE VALIDACIÓN CORREGIDA ---
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarYEnviarAsistencia();
            }
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnRegistrar, gbc);
    }

private void validarYEnviarAsistencia() {
    String nombreInput = txtNombre.getText().trim();
    String dniInput = txtDni.getText().trim();

    if (nombreInput.isEmpty() || dniInput.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos");
        return;
    }

try {
        String urlRailway = "jdbc:mysql://hopper.proxy.rlwy.net:50468/railway";
        Connection cn = DriverManager.getConnection(urlRailway, "root", "TBuPxHmGYWbvFFwuSRXkWuxpYRMyWDaW");

        // 1. Verificar existencia del empleado
        String sqlExiste = "SELECT * FROM empleados WHERE nombre_empleado = ? AND dni = ?";
        PreparedStatement pstE = cn.prepareStatement(sqlExiste);
        pstE.setString(1, nombreInput);
        pstE.setString(2, dniInput);
        ResultSet rsE = pstE.executeQuery();

        if (rsE.next()) {
            // 2. Consultar el historial de HOY (usando 'fecha_hora')
            String sqlCheck = "SELECT tipo FROM registros WHERE dni = ? AND DATE(fecha_hora) = CURRENT_DATE ORDER BY id DESC LIMIT 1";
            PreparedStatement pstC = cn.prepareStatement(sqlCheck);
            pstC.setString(1, dniInput);
            ResultSet rsC = pstC.executeQuery();

            String tipoFinal = "";
            boolean puedeRegistrar = true;

            if (rsC.next()) {
                // CASO: YA EXISTE AL MENOS UN REGISTRO HOY
                String ultimoTipo = rsC.getString("tipo");

                // Si lo último que hizo fue entrar, ahora le toca SALIR
                if (ultimoTipo.equalsIgnoreCase("ENTRADA") || ultimoTipo.equalsIgnoreCase("TARDE")) {
                    tipoFinal = "SALIDA";
                } // NUEVO: Si lo último fue SALIDA, permitimos que vuelva a marcar ENTRADA
                else if (ultimoTipo.equalsIgnoreCase("SALIDA")) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "Ya registró una salida hoy. ¿Desea registrar una NUEVA ENTRADA?",
                            "Re-ingreso", JOptionPane.YES_NO_OPTION);

                    if (respuesta == JOptionPane.YES_OPTION) {
                        java.time.LocalTime ahora = java.time.LocalTime.now();
                        java.time.LocalTime limite = java.time.LocalTime.of(8, 15);
                        tipoFinal = ahora.isAfter(limite) ? "TARDE" : "ENTRADA";
                    } else {
                        puedeRegistrar = false;
                    }
                }
            }

            // 3. Ejecutar el registro solo si pasó los filtros
            if (puedeRegistrar) {
                procesarEnvioAPI(nombreInput, dniInput, tipoFinal);
                txtNombre.setText("");
                txtDni.setText("");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Datos incorrectos o empleado no registrado.");
        }
        cn.close();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error de sistema: " + ex.getMessage());
    }
}

    private void procesarEnvioAPI(String nombre, String dni, String tipo) {
        // Tu lógica original de ngrok
        String url = "https://asistencia-remota-e63u.onrender.com/api/asistencia";
        String json = String.format("{\"empleado\":\"%s\", \"dni\":\"%s\", \"tipo\":\"%s\"}", nombre, dni, tipo);

        try {
// Usamos la misma conexión que ya sabemos que funciona
        String urlRailway = "jdbc:mysql://hopper.proxy.rlwy.net:50468/railway";
        Connection cn = DriverManager.getConnection(urlRailway, "root", "TBuPxHmGYWbvFFwuSRXkWuxpYRMyWDaW");

        // Insertamos directamente en la tabla de registros que creamos en Workbench
        String sql = "INSERT INTO registros (empleado, dni, tipo) VALUES (?, ?, ?)";
        PreparedStatement pst = cn.prepareStatement(sql);
        pst.setString(1, nombre);
        pst.setString(2, dni);
        pst.setString(3, tipo);

        int resultado = pst.executeUpdate();

        if (resultado > 0) {
            JOptionPane.showMessageDialog(this, "¡Asistencia registrada directamente en la Base de Datos!");
        } else {
            JOptionPane.showMessageDialog(this, "Error al insertar en la tabla registros.");
        }
        
        cn.close();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        System.out.println("Error BD: " + ex.getMessage());
    }
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        EventQueue.invokeLater(() -> {
            new AsistenciaApp().setVisible(true);
        });
    }
}