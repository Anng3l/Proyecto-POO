import com.mysql.cj.log.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Menu_Administrador extends JFrame {
    private JButton gestionarLibrosButton;
    private JButton gestionarUsuariosButton;
    private JButton historialDeDescargasButton;
    private JButton cerrarSesionButton;
    private JPanel Panel;

    public Menu_Administrador() {
        super("Menú");
        setContentPane(Panel);
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.setVisible(true);
            }
        });

        historialDeDescargasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Historial_Descargas historialDescargas = new Historial_Descargas();
                historialDescargas.setVisible(true);
            }
        });

        gestionarUsuariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Gestionar_Usuarios gestionarUsuarios = null;
                try {
                    gestionarUsuarios = new Gestionar_Usuarios();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                gestionarUsuarios.setVisible(true);
            }
        });

        gestionarLibrosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Gestionar_Libros gestionarLibros = null;
                try {
                    gestionarLibros = new Gestionar_Libros();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                gestionarLibros.setVisible(true);
            }
        });
    }
}
