import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/cajeroautomatico"+"root"+"";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cajeroautomatico","root","mati123456789");
            System.out.println("Conexión exitosa a la base de datos");
            return connection;
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
