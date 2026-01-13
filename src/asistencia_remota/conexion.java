
package asistencia_remota;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class conexion {
    // datos staticos usados para toda la clase
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_deasistencias";
    private static final String USER ="root";
    private static final String PASS ="1234";
    
    public static Connection conectar(){
    Connection con=null;
    try{
    con =DriverManager.getConnection(URL,USER,PASS);
    }catch(SQLException e){
        System.out.println(" error al conectar"+e.getMessage());
    }
    return con;
    }
    
    //verificar estado de hoy 
    public String verificarMarcadoHoy(String dni) {
    String resultado = "PENDIENTE"; // Por defecto no tiene nada
    // Consulta para obtener el último marcado del día actual
    String sql = "SELECT tipo_marcado FROM asistencias WHERE dni = ? AND fecha = CURRENT_DATE ORDER BY id DESC LIMIT 1";
    
    try (Connection con = this.conectar(); // Usa tu método conectar() existente
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, dni);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            String ultimoTipo = rs.getString("tipo_marcado");
            if (ultimoTipo.equalsIgnoreCase("ENTRADA") || ultimoTipo.equalsIgnoreCase("TARDE")) {
                resultado = "SOLO_ENTRADA"; // Ya entró, le toca salir
            } else if (ultimoTipo.equalsIgnoreCase("SALIDA")) {
                resultado = "FINALIZADO"; // Ya hizo las dos cosas
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al verificar estado: " + e.getMessage());
    }
    return resultado;
}
    
  
    
}
