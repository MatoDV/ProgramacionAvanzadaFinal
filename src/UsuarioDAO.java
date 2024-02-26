import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }
    public int obtenerIdUsuario(String numeroCuenta) throws SQLException {
        String query = "SELECT id_usuario FROM usuarios WHERE numero_cuenta = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, numeroCuenta);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_usuario");
                } else {
                    throw new SQLException("No se encontró la cuenta");
                }
            }
        }
    }

    public Usuario iniciarSesion(String numeroCuenta, String pin) throws SQLException {
        String query = "SELECT * FROM usuarios WHERE numero_cuenta = ? AND pin = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, numeroCuenta);
            statement.setString(2, pin);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Usuario(
                            resultSet.getInt("id_usuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("numero_cuenta"),
                            resultSet.getString("pin"),
                            resultSet.getInt("saldo")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public int consultarSaldo(String numeroCuenta) throws SQLException {
        String query = "SELECT saldo FROM usuarios WHERE numero_cuenta = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, numeroCuenta);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("saldo");
                } else {
                    throw new SQLException("No se encontró la cuenta");
                }
            }
        }
    }

    public void actualizarSaldo(String numeroCuenta, int nuevoSaldo) throws SQLException {
        String query = "UPDATE usuarios SET saldo = ? WHERE numero_cuenta = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nuevoSaldo);
            statement.setString(2, numeroCuenta);
            statement.executeUpdate();
        }
    }

    public void cambiarPIN(String numeroCuenta, String nuevoPIN) throws SQLException {
        String query = "UPDATE usuarios SET pin = ? WHERE numero_cuenta = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nuevoPIN);
            statement.setString(2, numeroCuenta);
            statement.executeUpdate();
        }
    }
}
