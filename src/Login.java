import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JFrame {
    private JComboBox modoComboBox;
    private JTextField cedulaTextField;
    private JPasswordField passwordField;
    private JButton iniciarSesionButton;
    private JButton salirButton;
    private JButton registrarseButton;
    private JPanel Panel;

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

                if (x == true && modo.equals("administrador")) {
                    dispose();
                    JOptionPane.showMessageDialog(null, "Credenciales correctas.");
                    // Establecer el ID del usuario actual en SessionManager
                    int userId = 0;
                    try {
                        userId = getUserIdByCi(ci);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    SessionManager.setCurrentUserId(userId);
                    Menu_Administrador menuAdministrador = new Menu_Administrador();
                    menuAdministrador.setVisible(true);
                } else if (x == true && modo.equals("usuario")) {
                    dispose();
                    JOptionPane.showMessageDialog(null, "Credenciales correctas.");
                    // Establecer el ID del usuario actual en SessionManager
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

        registrarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para registrarse
            }
        });
    }

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

    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
