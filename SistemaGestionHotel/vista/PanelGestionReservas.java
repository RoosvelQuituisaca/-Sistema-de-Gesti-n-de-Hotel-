package vista;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Reserva; // Importa la clase Reserva
import java.text.SimpleDateFormat;

public class PanelGestionReservas extends JPanel {
    private static final String[] ENCABEZADOS = {
        "Cédula", "Nombre", "Teléfono", "Correo",
        "Habitaciones", "Cantidad", "Días", "Fecha", "Total"
    };
    private static final String COLUMNA_CEDULA = ENCABEZADOS[0];
    private static final String COLUMNA_NOMBRE = ENCABEZADOS[1];

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField txtBuscarApellido;
    private final JTextField txtBuscarFecha;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public PanelGestionReservas(PanelClientes panelClientes, PanelPrincipal panelPrincipal) {
        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(ENCABEZADOS, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JPanel panelBuscar = new JPanel(new GridLayout(2, 2, 10, 10));
        txtBuscarApellido = new JTextField();
        txtBuscarFecha = new JTextField();
        panelBuscar.setBorder(BorderFactory.createTitledBorder("Buscar Reservas"));
        panelBuscar.add(new JLabel("Apellido:"));
        panelBuscar.add(txtBuscarApellido);
        panelBuscar.add(new JLabel("Fecha (dd/MM/yyyy):"));
        panelBuscar.add(txtBuscarFecha);

        JPanel panelBotones = new JPanel();
        JButton btnBuscar = new JButton("Buscar");
        JButton btnCancelar = new JButton("Cancelar Reserva");
        JButton btnRecargar = new JButton("Mostrar Todo");
        JButton btnCrear = new JButton("Crear");
        JButton btnGuardarCambios = new JButton("Guardar Cambios");

        btnGuardarCambios.addActionListener(e -> guardarCambios());

        panelBotones.add(btnBuscar);
        panelBotones.add(btnRecargar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCrear);
        panelBotones.add(btnGuardarCambios);

        add(panelBuscar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarDesdeArchivo();

        btnBuscar.addActionListener(e -> buscarReservas());
        btnRecargar.addActionListener(e -> cargarDesdeArchivo());

        btnCancelar.addActionListener((ActionEvent e) -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(PanelGestionReservas.this, "Selecciona una reserva para cancelar.");
                return;
            }
            modelo.removeRow(fila);
            guardarCambios();
            panelClientes.recargarDesdeGestion(modelo);
            JOptionPane.showMessageDialog(PanelGestionReservas.this, "Reserva cancelada.");
        });

        btnCrear.addActionListener(e -> panelPrincipal.irAPestanaClientes());
    }

    private void buscarReservas() {
        String apellido = txtBuscarApellido.getText().trim().toLowerCase();
        String fechaTexto = txtBuscarFecha.getText().trim();

        if (apellido.isEmpty() && fechaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un apellido o una fecha para buscar.");
            return;
        }

        cargarDesdeArchivo();

        for (int i = modelo.getRowCount() - 1; i >= 0; i--) {
            String nombre = modelo.getValueAt(i, 1).toString().toLowerCase();
            String fecha = modelo.getValueAt(i, 7).toString();

            boolean coincideApellido = apellido.isEmpty() || nombre.contains(apellido);
            boolean coincideFecha = fechaTexto.isEmpty() || fecha.equals(fechaTexto);

            if (!(coincideApellido && coincideFecha)) {
                modelo.removeRow(i);
            }
        }
    }

    private void cargarDesdeArchivo() {
        modelo.setRowCount(0);
        File archivo = new File("reservas.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                String[] datos = linea.split("\t");
                if (datos.length == ENCABEZADOS.length && datos[0].equalsIgnoreCase(COLUMNA_CEDULA)) continue;

                if (datos.length == ENCABEZADOS.length) {
                    agregarReserva(datos);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer reservas.txt");
        }
    }

    private void guardarCambios() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("reservas.txt"))) {
            writer.println(String.join("\t", ENCABEZADOS));
            for (int i = 0; i < modelo.getRowCount(); i++) {
                String cedula = modelo.getValueAt(i, 0).toString().trim();
                if (cedula.equalsIgnoreCase(COLUMNA_CEDULA)) continue;

                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    writer.print(modelo.getValueAt(i, j).toString() + "\t");
                }
                writer.println();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar archivo.");
        }
    }

    private boolean existeReserva(String cedula, String fecha) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            String c = modelo.getValueAt(i, 0).toString().trim();
            String f = modelo.getValueAt(i, 7).toString().trim();
            if (c.equalsIgnoreCase(cedula) && f.equalsIgnoreCase(fecha)) {
                return true;
            }
        }
        return false;
    }

    public void agregarReserva(String[] datos) {
        if (datos.length != ENCABEZADOS.length) return;
        String cedula = datos[0].trim();
        String fecha = datos[7].trim();

        if (cedula.equalsIgnoreCase(COLUMNA_CEDULA) && datos[1].equalsIgnoreCase(COLUMNA_NOMBRE)) {
            return; // No agregar la fila encabezado
        }

        if (existeReserva(cedula, fecha)) {
            System.out.println("Reserva ya existe para cédula: " + cedula + ", fecha: " + fecha);
            return;
        }

        modelo.addRow(datos);
    }

    // Nuevo método para agregar directamente un objeto Reserva
    public void agregarReserva(Reserva reserva) {
        String[] datos = reservaToArray(reserva);
        agregarReserva(datos);
        guardarCambios();
    }

    // Método para convertir Reserva a arreglo String[]
    private String[] reservaToArray(Reserva r) {
        return new String[] {
            r.getCedula(),
            r.getNombre(),
            r.getTelefono(),
            r.getCorreo(),
            r.getHabitaciones(),
            String.valueOf(r.getCantidad()),
            String.valueOf(r.getDias()),
            sdf.format(r.getFecha()),
            String.valueOf(r.getTotal())
        };
    }
}




