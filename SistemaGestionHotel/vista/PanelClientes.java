package vista;

import controlador.ClienteController;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;  // IMPORTANTE para formato habitaciones
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;

public class PanelClientes extends JPanel {

    private JTextField txtCedula, txtNombre, txtTelefono, txtCorreo;
    private JList<Integer> listHabitaciones;
    private DefaultListModel<Integer> modeloHabitaciones;
    private JSpinner spnCantidad;
    private JSpinner spnFecha;
    private JSpinner spnDias;
    private JLabel lblTotal;
    private final JButton btnRegistrar;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private ClienteController controller;

    private final int PRECIO_HABITACION = 30;
    private Set<Integer> habitacionesReservadas = new HashSet<>();

    // Formateador para mostrar moneda en formato USD
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.US);

    public PanelClientes() {
        controller = new ClienteController();
        setLayout(new BorderLayout());

        // Crear formulario con GridBagLayout
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCedula = new JTextField(15);
        txtNombre = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtCorreo = new JTextField(15);

        modeloHabitaciones = new DefaultListModel<>();
        for (int i = 1; i <= 20; i++) {
            modeloHabitaciones.addElement(i);
        }

        listHabitaciones = new JList<>(modeloHabitaciones);
        listHabitaciones.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listHabitaciones.setVisibleRowCount(3);
        JScrollPane scrollHabitaciones = new JScrollPane(listHabitaciones);
        scrollHabitaciones.setPreferredSize(new Dimension(150, 60));

        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

        // --- NUEVOS CAMPOS ---

        spnFecha = new JSpinner(new SpinnerDateModel());
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        spnFecha.setValue(new Date()); // Valor inicial = hoy

        spnDias = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));

        lblTotal = new JLabel("Total: " + formatoMoneda.format(0));

        btnRegistrar = new JButton("Registrar Cliente");

        int y = 0;

        agregarFila(panelFormulario, gbc, y++, "Cédula:", txtCedula);
        agregarFila(panelFormulario, gbc, y++, "Nombre:", txtNombre);
        agregarFila(panelFormulario, gbc, y++, "Teléfono:", txtTelefono);
        agregarFila(panelFormulario, gbc, y++, "Correo:", txtCorreo);
        agregarFila(panelFormulario, gbc, y++, "Habitaciones:", scrollHabitaciones);
        agregarFila(panelFormulario, gbc, y++, "Cantidad:", spnCantidad);

        // Agregar filas nuevas para Fecha y Días
        agregarFila(panelFormulario, gbc, y++, "Fecha:", spnFecha);
        agregarFila(panelFormulario, gbc, y++, "Días:", spnDias);

        agregarFila(panelFormulario, gbc, y++, "Total:", lblTotal);

        // Botón en la última fila
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(btnRegistrar, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{
            "Cédula", "Nombre", "Teléfono", "Correo", "Habitaciones", "Cantidad", "Días", "Fecha", "Total"
        }, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(700, 200));
        add(scrollTabla, BorderLayout.CENTER);

        JButton guardarBtn = new JButton("Guardar");
        guardarBtn.addActionListener(e -> {
            try (PrintWriter writer = new PrintWriter(new FileWriter("reservas.txt"))) {
                // Escribir encabezados con tabulaciones
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    writer.print(tabla.getColumnName(j) + "\t");
                }
                writer.println();

                // Escribir datos
                for (int i = 0; i < tabla.getRowCount(); i++) {
                    for (int j = 0; j < tabla.getColumnCount(); j++) {
                        writer.print(tabla.getValueAt(i, j) + "\t");
                    }
                    writer.println();
                }
                JOptionPane.showMessageDialog(this, "Archivo guardado como reservas.txt");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar archivo.");
            }
        });
        JPanel panelGuardar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelGuardar.add(guardarBtn);
        add(panelGuardar, BorderLayout.SOUTH);

        // Cargar clientes existentes
        cargarClientes();

        spnCantidad.addChangeListener(e -> actualizarTotal());
        spnDias.addChangeListener(e -> actualizarTotal());

        btnRegistrar.addActionListener(__ -> {
            if (validarFormulario()) {
                int cantidad = (int) spnCantidad.getValue();
                int dias = (int) spnDias.getValue();
                Date fecha = (Date) spnFecha.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                List<Integer> habitacionesSeleccionadas = listHabitaciones.getSelectedValuesList();

                if (habitacionesSeleccionadas.size() != cantidad) {
                    JOptionPane.showMessageDialog(this,
                            "Debes seleccionar exactamente " + cantidad + " habitación(es).");
                    return;
                }

                for (Integer hab : habitacionesSeleccionadas) {
                    if (habitacionesReservadas.contains(hab)) {
                        JOptionPane.showMessageDialog(this,
                                "La habitación " + hab + " ya está reservada.");
                        return;
                    }
                }

                // VALIDACIÓN: fecha no puede ser anterior a hoy (sin hora)
                Date hoy = new Date();
                if (fecha.before(sinHora(hoy))) {
                    JOptionPane.showMessageDialog(this, "La fecha de reservación no puede ser anterior a hoy.");
                    return;
                }

                int total = cantidad * dias * PRECIO_HABITACION;

                controller.registrarCliente(
                        txtCedula.getText(),
                        txtNombre.getText(),
                        txtTelefono.getText(),
                        txtCorreo.getText()
                );

                // Formatear habitaciones sin corchetes
                String habitacionesStr = habitacionesSeleccionadas.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                modeloTabla.addRow(new Object[]{
                        txtCedula.getText(),
                        txtNombre.getText(),
                        txtTelefono.getText(),
                        txtCorreo.getText(),
                        habitacionesStr,
                        cantidad,
                        dias,
                        sdf.format(fecha),
                        formatoMoneda.format(total)
                });

                for (Integer hab : habitacionesSeleccionadas) {
                    habitacionesReservadas.add(hab);
                    modeloHabitaciones.removeElement(hab);
                }

                limpiarFormulario();
                actualizarTotal();

                JOptionPane.showMessageDialog(this, "Cliente registrado correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            }
        });
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int y, String etiqueta, Component campo) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(campo, gbc);
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<Cliente> lista = controller.listarClientes();
        for (Cliente c : lista) {
            modeloTabla.addRow(new Object[]{
                    c.getCedula(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getCorreo(),
                    "-", "-", "-", "-", "-" // placeholders para nuevas columnas
            });
        }
    }

    private boolean validarFormulario() {
        return !txtCedula.getText().isEmpty()
                && !txtNombre.getText().isEmpty()
                && !txtTelefono.getText().isEmpty()
                && !txtCorreo.getText().isEmpty();
    }

    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        spnCantidad.setValue(1);
        spnDias.setValue(1);
        spnFecha.setValue(new Date());
        listHabitaciones.clearSelection();
    }

    private void actualizarTotal() {
        int cantidad = (int) spnCantidad.getValue();
        int dias = (int) spnDias.getValue();
        int total = cantidad * dias * PRECIO_HABITACION;
        lblTotal.setText("Total: " + formatoMoneda.format(total));
    }

    // Método helper para limpiar la hora de una fecha y comparar solo fecha
    private Date sinHora(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    public void cargarReservaDesdeArchivo(String[] datos) {
    modeloTabla.addRow(datos);
}

    void recargarDesdeGestion(DefaultTableModel modelo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}



