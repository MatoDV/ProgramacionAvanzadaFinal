import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CajeroAutomatico implements ServiciosCajero{
    private static Connection connection;
    private static UsuarioDAO usuarioDAO;
    private static TransaccionDAO transaccionDAO;
    private static Scanner scanner = new Scanner(System.in);
    public Usuario usuario;


    public static void main(String[] args) {
        try {
            connection = ConexionBD.getConnection();
            usuarioDAO = new UsuarioDAO(connection);
            transaccionDAO = new TransaccionDAO(connection);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese su número de cuenta:");
            String numeroCuenta = scanner.nextLine();
            System.out.println("Ingrese su PIN:");
            String pin = scanner.nextLine();

            CajeroAutomatico cajeroAutomatico = new CajeroAutomatico();
            cajeroAutomatico.iniciarSesion(numeroCuenta, pin); // Llamada al método iniciarSesion() con los argumentos adecuados
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


    @Override
    public void iniciarSesion(String numeroCuenta, String pin) {
        try {
            usuario = usuarioDAO.iniciarSesion(numeroCuenta, pin);
            if (usuario != null) {
                System.out.println("Inicio de sesión exitoso");
                mostrarMenu();
            } else {
                System.out.println("Credenciales incorrectas. Intente nuevamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }
    }


        private void mostrarMenu() {
            boolean salir = false;
            while (!salir) {
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
                int cantidad = 0;
                String cuentaDestino = "";
                String nuevoPIN = "";
                switch (opcion) {
                    case 1:
                        consultarSaldo();
                        break;
                    case 2:
                        retirar(cantidad);
                        break;
                    case 3:
                        depositar(cantidad);
                        break;
                    case 4:
                        transferir(cuentaDestino, cantidad);
                        break;
                    case 5:
                        cambiarPIN(nuevoPIN);
                        break;
                    case 6:
                        obtenerHistorialOperaciones();
                        break;
                    case 7:
                        cerrarSesion();
                        salir = true; // Salir del bucle y cerrar sesión
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            }
        }

    @Override
    public void consultarSaldo() {
        try {
            int saldo = usuarioDAO.consultarSaldo(usuario.getNumeroCuenta());
            System.out.println("Saldo actual: $" + saldo);
        } catch (SQLException e) {
            System.out.println("Error al consultar saldo: " + e.getMessage());
        }
    }

    @Override
    public void retirar(int cantidad) {
        System.out.println("Ingrese la cantidad a retirar: ");
        cantidad = Integer.parseInt(scanner.nextLine());

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

    @Override
    public void depositar(int cantidad) {
        System.out.println("Ingrese la cantidad a depositar: ");
        cantidad = Integer.parseInt(scanner.nextLine());

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

    @Override
    public void transferir(String cuentaDestino, int cantidad) {
        System.out.println("Ingrese el número de cuenta de destino: ");
        cuentaDestino = scanner.nextLine();
        System.out.println("Ingrese la cantidad a transferir: ");
        cantidad = Integer.parseInt(scanner.nextLine());

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

    @Override
    public void cambiarPIN(String nuevoPIN) {
        System.out.println("Ingrese el nuevo PIN: ");
        nuevoPIN = scanner.nextLine();

        try {
            usuarioDAO.cambiarPIN(usuario.getNumeroCuenta(), nuevoPIN);
            System.out.println("PIN cambiado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al cambiar el PIN: " + e.getMessage());
        }
    }

    public void obtenerHistorialOperaciones() {
        try {
            List<Transaccion> transacciones = transaccionDAO.obtenerTransaccionesUsuario(usuario.getIdUsuario());
            System.out.println("Historial de Transacciones:");
            for (Transaccion transaccion : transacciones) {
                System.out.println("ID de Cuenta: " + transaccion.getIdTransaccion());
                System.out.println("Tipo: " + transaccion.getTipo());
                System.out.println("Monto: " + transaccion.getMonto());
                System.out.println("Fecha: " + transaccion.getFecha());
                System.out.println("--------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el historial de transacciones: " + e.getMessage());
        }
    }

    @Override
    public void cerrarSesion() {
        usuario = null;
    }
}

