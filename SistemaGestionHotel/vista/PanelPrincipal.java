package vista;

import java.io.*;
import javax.swing.*;

public class PanelPrincipal extends JFrame {

    private final JTabbedPane pestañas;
    private final PanelClientes panelClientes;
    private final PanelGestionReservas panelGestion;

    public PanelPrincipal() {
        setTitle("Sistema de Gestión de Hotel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        pestañas = new JTabbedPane();

        panelClientes = new PanelClientes();
        panelGestion = new PanelGestionReservas(panelClientes, this);

        pestañas.addTab("Clientes", panelClientes);
        pestañas.addTab("Gestión de Reservas", panelGestion);

        add(pestañas);
        setVisible(true);

        cargarReservasIniciales();
    }

    public void irAPestanaClientes() {
        pestañas.setSelectedComponent(panelClientes);
    }

    private void cargarReservasIniciales() {
        File archivo = new File("reservas.txt");
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) { // Ignorar encabezado
            // Ignorar encabezado
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\t");
                if (datos.length >= 9) {
                    // Agregar a ambas pestañas
                    panelClientes.cargarReservaDesdeArchivo(datos);
                    panelGestion.agregarReserva(datos);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar reservas.");
        }
    }
}
