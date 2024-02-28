import java.sql.Date;

public class Transaccion {
    private int idTransaccion;
    private int idUsuario;
    private String tipo;
    private int monto;
    private String cuentaDestino;
    private Date fecha;
    public Transaccion(int idTransaccion, int idUsuario, String tipo, int monto, String cuentaDestino, Date fecha) {
        this.idTransaccion = idTransaccion;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.monto = monto;
        this.cuentaDestino = cuentaDestino;
        this.fecha = fecha;
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }
}
