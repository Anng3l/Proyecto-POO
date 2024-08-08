import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * La clase Crear_Usuario proporciona una interfaz gráfica para crear y almacenar información sobre nuevos usuarios.
 */
public class Crear_Usuario extends JFrame {
    private JTextField nombreTextField;
    private JButton crearUsuarioButton;
    private JButton cancelarButton;
    private JTextField apellidoTextField;
    private JTextField ciTextField;
    private JComboBox<String> generoComboBox;
    private JComboBox<String> tipoComboBox;
    private JPanel Panel;
    private JTextField passwordTextField;

    /**
     * Constructor de la clase Crear_Usuario.
     * Inicializa los componentes de la interfaz gráfica y establece los manejadores de eventos.
     */
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

    /**
     * Crea un nuevo usuario en la base de datos con la información proporcionada.
     *
     * @param nombre    El nombre del usuario.
     * @param apellido  El apellido del usuario.
     * @param ci        El CI del usuario.
     * @param genero    El género del usuario.
     * @param tipo      El tipo de usuario.
     * @param password  La contraseña del usuario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public void CrearUsuarios(String nombre, String apellido, String ci, String genero, String tipo, String password) throws SQLException {
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

        if (efecto > 0) {
            JOptionPane.showMessageDialog(null, "Usuario creado exitosamente");
        }

        prst.close();
        connection.close();
    }

    /**
     * Establece una conexión con la base de datos.
     *
     * @return La conexión establecida.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
