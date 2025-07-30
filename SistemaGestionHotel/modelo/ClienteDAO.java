package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ClienteDAO {

    public void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (" +
                "cedula TEXT PRIMARY KEY," +
                "nombre TEXT NOT NULL," +
                "telefono TEXT," +
                "correo TEXT)";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
        }
    }

    public void insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes (cedula, nombre, telefono, correo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getCorreo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("correo")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
        }
        return lista;
    }
}
