import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CajeroAutomatico {
    private static Connection connection;
    private static UsuarioDAO usuarioDAO;
    private static TransaccionDAO transaccionDAO;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            connection = ConexionBD.getConnection();
            usuarioDAO = new UsuarioDAO(connection);
            transaccionDAO = new TransaccionDAO(connection);

            iniciarSesion();
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    private static void iniciarSesion() {
        System.out.println("Ingrese su número de cuenta:");
        String numeroCuenta = scanner.nextLine();

        System.out.println("Ingrese su PIN:");
        String pin = scanner.nextLine();

        try {
            Usuario usuario = usuarioDAO.iniciarSesion(numeroCuenta, pin);
            if (usuario != null) {
                System.out.println("Inicio de sesión exitoso");
                mostrarMenu(usuario);
            } else {
                System.out.println("Credenciales incorrectas. Intente nuevamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }
    }

    private static void mostrarMenu(Usuario usuario) {
        System.out.println("\nMenú:");
        System.out.println("1. Consultar Saldo");
        System.out.println("2. Realizar Retiro");
        System.out.println("3. Realizar Depósito");
        System.out.println("4. Realizar Transferencia");
        System.out.println("5. Cambiar PIN");
        System.out.println("6. Ver Historial de Transacciones");
        System.out.println("7. Cerrar Sesión");
        System.out.println("Seleccione una opción:");

        int opcion = Integer.parseInt(scanner.nextLine());

        switch (opcion) {
            case 1:
                consultarSaldo(usuario);
                break;
            case 2:
                realizarRetiro(usuario);
                break;
            case 3:
                realizarDeposito(usuario);
                break;
            case 4:
                realizarTransferencia(usuario);
                break;
            case 5:
                cambiarPIN(usuario);
                break;
            case 6:
                verHistorialTransacciones(usuario);
                break;
            case 7:
                System.out.println("Sesión cerrada correctamente");
                break;
            default:
                System.out.println("Opción no válida");
        }
    }

    private static void consultarSaldo(Usuario usuario) {
        try {
            int saldo = usuarioDAO.consultarSaldo(usuario.getNumeroCuenta());
            System.out.println("Saldo actual: $" + saldo);
        } catch (SQLException e) {
            System.out.println("Error al consultar saldo: " + e.getMessage());
        }
    }

    private static void realizarRetiro(Usuario usuario) {
        System.out.println("Ingrese la cantidad a retirar:");
        int cantidad = Integer.parseInt(scanner.nextLine());

        try {
            int saldoActual = usuarioDAO.consultarSaldo(usuario.getNumeroCuenta());
            if (cantidad > saldoActual) {
                System.out.println("Saldo insuficiente");
            } else {
                int nuevoSaldo = saldoActual - cantidad;
                usuarioDAO.actualizarSaldo(usuario.getNumeroCuenta(), nuevoSaldo);
                transaccionDAO.realizarTransaccion(usuario.getIdUsuario(), "Retiro", cantidad);
                System.out.println("Retiro realizado correctamente");
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar el retiro: " + e.getMessage());
        }
    }

    private static void realizarDeposito(Usuario usuario) {
        System.out.println("Ingrese la cantidad a depositar:");
        int cantidad = Integer.parseInt(scanner.nextLine());

        try {
            int saldoActual = usuarioDAO.consultarSaldo(usuario.getNumeroCuenta());
            int nuevoSaldo = saldoActual + cantidad;
            usuarioDAO.actualizarSaldo(usuario.getNumeroCuenta(), nuevoSaldo);
            transaccionDAO.realizarTransaccion(usuario.getIdUsuario(), "Depósito", cantidad);
            System.out.println("Depósito realizado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al realizar el depósito: " + e.getMessage());
        }
    }

    private static void realizarTransferencia(Usuario usuario) {
        System.out.println("Ingrese el número de cuenta de destino:");
        String cuentaDestino = scanner.nextLine();

        System.out.println("Ingrese la cantidad a transferir:");
        int cantidad = Integer.parseInt(scanner.nextLine());

        try {
            int saldoActual = usuarioDAO.consultarSaldo(usuario.getNumeroCuenta());
            if (cantidad > saldoActual) {
                System.out.println("Saldo insuficiente");
            } else {
                int nuevoSaldoOrigen = saldoActual - cantidad;
                usuarioDAO.actualizarSaldo(usuario.getNumeroCuenta(), nuevoSaldoOrigen);
                usuarioDAO.actualizarSaldo(cuentaDestino, cantidad);
                transaccionDAO.realizarTransaccion(usuario.getIdUsuario(), "Transferencia Saliente", cantidad);
                transaccionDAO.realizarTransaccion(usuarioDAO.obtenerIdUsuario(cuentaDestino), "Transferencia Entrante", cantidad);
                System.out.println("Transferencia realizada correctamente");
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar la transferencia: " + e.getMessage());
        }
    }

    private static void cambiarPIN(Usuario usuario) {
        System.out.println("Ingrese el nuevo PIN:");
        String nuevoPIN = scanner.nextLine();

        try {
            usuarioDAO.cambiarPIN(usuario.getNumeroCuenta(), nuevoPIN);
            System.out.println("PIN cambiado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al cambiar el PIN: " + e.getMessage());
        }
    }

    private static void verHistorialTransacciones(Usuario usuario) {
        try {
            List<Transaccion> transacciones = transaccionDAO.obtenerTransaccionesUsuario(usuario.getIdUsuario());
            System.out.println("Historial de Transacciones:");
            for (Transaccion transaccion : transacciones) {
                System.out.println(transaccion);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el historial de transacciones: " + e.getMessage());
        }
    }
}
