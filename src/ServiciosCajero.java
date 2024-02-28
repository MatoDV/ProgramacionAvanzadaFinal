public interface ServiciosCajero {
    void iniciarSesion(String numeroCuenta, String pin);
    void consultarSaldo();
    void retirar(int cantidad);
    void depositar(int cantidad);
    void transferir(String cuentaDestino, int cantidad);
    void cambiarPIN(String nuevoPIN);
    void cerrarSesion();
    void obtenerHistorialOperaciones();
}
