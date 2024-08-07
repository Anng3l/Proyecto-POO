import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Crear_Usuario extends JFrame {
    private JTextField nombreTextField;
    private JButton crearUsuarioButton;
    private JButton cancelarButton;
    private JTextField apellidoTextField;
    private JTextField ciTextField;
    private JComboBox generoComboBox;
    private JComboBox tipoComboBox;
    private JPanel Panel;
    private JTextField passwordTextField;

    public Crear_Usuario() {
        super("Crear usuario");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);

        crearUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = nombreTextField.getText();
                String apellido = apellidoTextField.getText();
                String ci = ciTextField.getText();

                String genero = (String) generoComboBox.getSelectedItem();
                assert genero != null;
                genero = genero.toLowerCase().trim();

                String tipo = (String) tipoComboBox.getSelectedItem();
                assert tipo != null;
                tipo = tipo.toLowerCase().trim();

                String password = passwordTextField.getText();
                try {
                    CrearUsuarios(nombre, apellido, ci, genero, tipo, password);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    public void CrearUsuarios(String nombre, String apellido, String ci, String genero, String tipo, String password) throws SQLException
    {
        Connection connection = Conexion();

        String sql = "INSERT INTO usuarios (nombre_usuario, apellido_usuario, ci_usuario, genero_usuario, tipo_usuario, password_usuario) VALUES ((?), (?), (?), (?), (?), (?));";

        PreparedStatement prst = connection.prepareStatement(sql);

        prst.setString(1, nombre);
        prst.setString(2, apellido);
        prst.setString(3, ci);
        prst.setString(4, genero);
        prst.setString(5, tipo);
        prst.setString(6, password);

        int efecto = prst.executeUpdate();

        if (efecto > 0)
        {
            JOptionPane.showMessageDialog(null, "Usuario creado exitosamente");
        }

        prst.close();
        connection.close();
    }


    public Connection Conexion() throws SQLException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/proyecto";
        String user = "root";
        String password = "vamossobreruedasdefuegoAa@_";

        return DriverManager.getConnection(url, user, password);
    }
}
