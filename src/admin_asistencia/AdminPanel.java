package admin_asistencia;

import com.formdev.flatlaf.FlatDarkLaf; // Importar tema moderno
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminPanel extends JFrame {

    private JTable tablaAsistencia;
    private DefaultTableModel modelo;

    public AdminPanel() {
        // 1. Configuración de la ventana
        setTitle("Panel de Administración - Control de Asistencia");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Espaciado entre componentes

        // 2. CENTRO: La Tabla
        String[] columnas = {"ID", "Empleado","DNI", "Fecha y Hora", "Tipo"}; //modificado
        modelo = new DefaultTableModel(columnas, 0);
        tablaAsistencia = new JTable(modelo);
        tablaAsistencia.setRowHeight(30); // Filas más altas para mejor lectura
        JScrollPane scrollPane = new JScrollPane(tablaAsistencia);
        add(scrollPane, BorderLayout.CENTER);

        // 3. DERECHA: Panel de Filtros (Azul)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(new Color(44, 62, 80)); // Color azul oscuro
        panelDerecho.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20)); // 3 botones apilados
        panelDerecho.setPreferredSize(new Dimension(250, 60));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton btnTodos = crearBotonMenu("Todos los empleados");
        JButton btnFalto = crearBotonMenu("Falto");
        JButton btnTardanzas = crearBotonMenu("Tardanzas");
        JButton btngestionar =crearBotonMenu("Gestionar Empleados");

        panelDerecho.add(btnTodos);
        panelDerecho.add(btnFalto);
        panelDerecho.add(btnTardanzas);
        panelDerecho.add(btngestionar);
        add(panelDerecho, BorderLayout.EAST);
        
        
        //creacion de la interfaz del boton 
        
        btngestionar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) { // La P debe ser mayúscula
        // Asegúrate que 'ventanagestion' esté escrito igual que el nombre de tu pestaña arriba
        ventanagestion ventana = new ventanagestion(); 
        ventana.setVisible(true);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        });

        // 4. ABAJO: Botón Refrescar
        JButton btnRefrescar = new JButton("REFRESCAR LISTA DE EMPLEADOS");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRefrescar.addActionListener(e -> cargarDatos());
        add(btnRefrescar, BorderLayout.SOUTH);

        cargarDatos(); // Cargar datos al abrir
    }

    // Método para dar estilo a los botones del dibujo
    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 50));
        return btn;
    }

    private void cargarDatos() {
        // URL Corregida (Sin flechas ni espacios extra)
        String url = "https://redder-kadence-tranquilly.ngrok-free.dev/api/asistencia";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                modelo.setRowCount(0); // Limpiar tabla
                JSONArray jsonArray = new JSONArray(response.body());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    modelo.addRow(new Object[]{
                        obj.optInt("id"),
                        obj.optString("empleado"),
                        obj.optString("dni"),
                        obj.optString("fechaHora"),
                        obj.optString("tipo")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Aplicar tema FlatLaf antes de iniciar
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(() -> {
            new AdminPanel().setVisible(true);
        });
    }
}