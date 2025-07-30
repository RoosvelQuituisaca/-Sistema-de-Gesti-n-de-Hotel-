package vista;

import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Login - Hotel");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        JTextField usuario = new JTextField();
        JPasswordField clave = new JPasswordField();
        JButton ingresar = new JButton("Ingresar");

        add(new JLabel("Usuario:"));
        add(usuario);
        add(new JLabel("Contraseña:"));
        add(clave);
        add(new JLabel(""));
        add(ingresar);

        ingresar.addActionListener(__ -> {
            if (usuario.getText().equals("hotel") && new String(clave.getPassword()).equals("123456")) {
                dispose();
                new MenuPrincipalFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            }
        });
    }
}
