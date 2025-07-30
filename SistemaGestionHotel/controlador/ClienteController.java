package controlador;

import java.util.List;
import modelo.Cliente;
import modelo.ClienteDAO;

public class ClienteController {
    private final ClienteDAO dao;

    public ClienteController() {
        dao = new ClienteDAO();
        dao.crearTabla();
    }

    public void registrarCliente(String cedula, String nombre, String telefono, String correo) {
        Cliente c = new Cliente(cedula, nombre, telefono, correo);
        dao.insertar(c);
    }

    public List<Cliente> listarClientes() {
        return dao.listar();
    }
}
