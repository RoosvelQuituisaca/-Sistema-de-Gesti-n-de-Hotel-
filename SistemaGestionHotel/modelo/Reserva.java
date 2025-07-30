package modelo;

import java.util.Date;

public class Reserva {
    private String cedula;
    private String nombre;
    private String telefono;
    private String correo;
    private String habitaciones; // Ej: "1, 3, 5"
    private int cantidad;
    private int dias;
    private Date fecha;
    private int total;

    public Reserva(String cedula, String nombre, String telefono, String correo,
                   String habitaciones, int cantidad, int dias, Date fecha, int total) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.habitaciones = habitaciones;
        this.cantidad = cantidad;
        this.dias = dias;
        this.fecha = fecha;
        this.total = total;
    }

    // Getters y setters (omito por brevedad)

    // Método para formato texto para guardar
    public String toArchivoString() {
        return cedula + "\t" + nombre + "\t" + telefono + "\t" + correo + "\t" +
               habitaciones + "\t" + cantidad + "\t" + dias + "\t" +
               new java.text.SimpleDateFormat("dd/MM/yyyy").format(fecha) + "\t" + total;
    }

    public static Reserva fromArchivoString(String linea) throws Exception {
        String[] partes = linea.split("\t");
        if (partes.length != 9) throw new Exception("Formato incorrecto reserva");
        String cedula = partes[0];
        String nombre = partes[1];
        String telefono = partes[2];
        String correo = partes[3];
        String habitaciones = partes[4];
        int cantidad = Integer.parseInt(partes[5]);
        int dias = Integer.parseInt(partes[6]);
        Date fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(partes[7]);
        int total = Integer.parseInt(partes[8]);
        return new Reserva(cedula, nombre, telefono, correo, habitaciones, cantidad, dias, fecha, total);
    }

    public String getCedula() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(String habitaciones) {
        this.habitaciones = habitaciones;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
