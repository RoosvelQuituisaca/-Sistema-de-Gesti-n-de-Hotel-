package main;

import javax.swing.SwingUtilities;
import vista.PanelPrincipal;

public class Main {
    public static void main(String[] args) {
        // Ejecutar en hilo de interfaz gráfica para evitar problemas
        SwingUtilities.invokeLater(() -> new PanelPrincipal());
    }
}


