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

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class AdminPanel extends JFrame {

    private JTable tablaAsistencia;
    private JTextField txtFechaFiltro; // Nuevo campo de texto nativo
    private JButton  btnHoy;
    private DefaultTableModel modelo;

    public AdminPanel() {
        // 1. Configuración de la ventana
        setTitle("Panel de Administración - Control de Asistencia");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Espaciado entre componentes
        
        //buscador 
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(new Color(44, 62, 80)); // Manteniendo tu estilo azul oscuro
        
        JLabel lblFiltro = new JLabel("Buscar por Fecha: ");
        lblFiltro.setForeground(Color.WHITE);
        
       
       
        
        // Configuramos un campo de texto con la fecha de hoy
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtFechaFiltro = new JTextField(sdf.format(new Date()));
        txtFechaFiltro.setPreferredSize(new Dimension(120, 30));
        
        // 2. Configuramos el botón (una sola vez)
        btnHoy = new JButton("Buscar");
        btnHoy.addActionListener(e -> cargarDatos());
        
        
        
        
        // Lo añadimos al panel
        
        panelSuperior.add(lblFiltro);  
        panelSuperior.add(txtFechaFiltro);
        panelSuperior.add(btnHoy);                          
        add(panelSuperior,BorderLayout.NORTH);
        
        


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
// 1. Obtenemos la fecha escrita en el cuadrito
        String fechaSeleccionada = txtFechaFiltro.getText().trim();
        
        // 2. Agregamos la fecha a la URL como un parámetro (?fecha=)
        String url = "https://prolixly-sprightlier-candi.ngrok-free.dev/api/asistencia?fecha=" + fechaSeleccionada;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                modelo.setRowCount(0); 
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
            JOptionPane.showMessageDialog(this, "Error al filtrar datos: " + e.getMessage());
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