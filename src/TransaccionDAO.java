import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDAO {
    private Connection connection;

    public TransaccionDAO(Connection connection) {
        this.connection = connection;
    }

    public void realizarTransaccion(int idUsuario, String tipo, int monto) throws SQLException {
        String query = "INSERT INTO transacciones (id_usuario, tipo_transaccion, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);
            statement.setString(2, tipo);
            statement.setInt(3, monto);
            statement.executeUpdate();
        }
    }

    public List<Transaccion> obtenerTransaccionesUsuario(int idUsuario) throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String query = "SELECT * FROM transacciones WHERE id_usuario = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transacciones.add(new Transaccion(
                            resultSet.getInt("id_transaccion"),
                            resultSet.getInt("id_usuario"),
                            resultSet.getString("tipo_transaccion"),
                            resultSet.getInt("cantidad"),
                            resultSet.getString("cuenta_destino"),
                            resultSet.getDate("fecha_hora")
                    ));
                }
            }
        }
        return transacciones;
    }
}
