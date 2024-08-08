import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * La clase Login proporciona una interfaz gráfica para la autenticación de usuarios.
 * Permite a los usuarios iniciar sesión en el sistema, salir de la aplicación o registrarse.
 */
public class Login extends JFrame {
    private JComboBox<String> modoComboBox;
    private JTextField cedulaTextField;
    private JPasswordField passwordField;
    private JButton iniciarSesionButton;
    private JButton salirButton;
    private JPanel Panel;

    /**
     * Constructor de la clase Login.
     * Inicializa los componentes de la interfaz gráfica y establece los manejadores de eventos.
     */
    public Login() {
        super("Login");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 300);

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ci = cedulaTextField.getText();
                char[] pass = passwordField.getPassword();
                String password = new String(pass);
                String modo = (String) modoComboBox.getSelectedItem();
                modo = modo.toLowerCase().trim();

                Boolean x;
                try {
                    x = login(ci, password, modo);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if (x && modo.equals("administrador")) {
                    dispose();
                    JOptionPane.showMessageDialog(null, "Credenciales correctas.");
                    int userId = 0;
                    try {
                        userId = getUserIdByCi(ci);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    SessionManager.setCurrentUserId(userId);
                    Menu_Administrador menuAdministrador = new Menu_Administrador();
                    menuAdministrador.setVisible(true);
                } else if (x && modo.equals("usuario")) {
                    dispose();
                    JOptionPane.showMessageDialog(null, "Credenciales correctas.");
                    int userId = 0;
                    try {
                        userId = getUserIdByCi(ci);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    SessionManager.setCurrentUserId(userId);
                    Menu_Usuario menuUsuario = new Menu_Usuario();
                    menuUsuario.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
                }
            }
        });

        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Verifica las credenciales del usuario en la base de datos.
     *
     * @param cedula La cédula del usuario.
     * @param password La contraseña del usuario.
     * @param modo El modo de usuario (administrador o usuario).
     * @return true si las credenciales son correctas, false en caso contrario.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     */
    public boolean login(String cedula, String password, String modo) throws SQLException {
        boolean resultado;
        Connection connection = conexion();
        String sql = "SELECT ci_usuario, password_usuario, tipo_usuario FROM usuarios WHERE ci_usuario = (?) AND tipo_usuario = (?);";

        PreparedStatement prst = connection.prepareStatement(sql);
        prst.setString(1, cedula);
        prst.setString(2, modo);

        ResultSet rs = prst.executeQuery();

        String ci = "";
        String pass = "";
        String mode = "";

        if (rs.next()) {
            ci = rs.getString("ci_usuario");
            pass = rs.getString("password_usuario");
            mode = rs.getString("tipo_usuario");
        }

        if (ci.equals(cedula) && pass.equals(password) && mode.equals(modo)) {
            resultado = true;
        } else {
            resultado = false;
        }

        rs.close();
        prst.close();
        connection.close();

        return resultado;
    }

    /**
     * Obtiene el ID de usuario basado en la cédula.
     *
     * @param ci La cédula del usuario.
     * @return El ID del usuario.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     */
    public int getUserIdByCi(String ci) throws SQLException {
        int userId = -1;
        Connection connection = conexion();
        String sql = "SELECT id_usuario FROM usuarios WHERE ci_usuario = ?";
        PreparedStatement prst = connection.prepareStatement(sql);
        prst.setString(1, ci);

        ResultSet rs = prst.executeQuery();
        if (rs.next()) {
            userId = rs.getInt("id_usuario");
        }

        rs.close();
        prst.close();
        connection.close();

        return userId;
    }

    /**
     * Establece una conexión con la base de datos.
     *
     * @return La conexión con la base de datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
