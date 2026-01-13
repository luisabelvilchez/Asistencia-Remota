
package asistencia_remota;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {
    // datos staticos usados para toda la clase
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_deasistencias?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER ="root";
    private static final String PASS ="123456";
    
    public static Connection conectar(){
    Connection con=null;
    try{
    con =DriverManager.getConnection(URL,USER,PASS);
    }catch(SQLException e){
        System.out.println(" error al conectar"+e.getMessage());
    }
    return con;
    }
    
}
