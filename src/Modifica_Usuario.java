import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * La clase Modifica_Usuario proporciona una interfaz gráfica para modificar la información de un usuario en la base de datos.
 * Permite al usuario actualizar el nombre, apellido, cédula, género y tipo de usuario.
 */
public class Modifica_Usuario extends JFrame {
    private JTextField idTextField;
    private JButton actualizarInformacionButton;
    private JButton cancelarButton;
    private JTextField nombreTextField;
    private JTextField apellidoTextField;
    private JTextField ciTextField;
    private JComboBox<String> generoComboBox;
    private JComboBox<String> tipoComboBox;
    private JPanel Panel;

    /**
     * Constructor de la clase Modifica_Usuario.
     * Inicializa los componentes de la interfaz gráfica y establece los manejadores de eventos.
     */
    public Modifica_Usuario() {
        super("Actualización de datos");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);

        actualizarInformacionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText();
                String nombre = nombreTextField.getText();
                String apellido = apellidoTextField.getText();
                String ci = ciTextField.getText();

                String genero = (String) generoComboBox.getSelectedItem();
                assert genero != null;
                genero = genero.toLowerCase().trim();

                String tipo = (String) tipoComboBox.getSelectedItem();
                assert tipo != null;
                tipo = tipo.toLowerCase().trim();

                try {
                    ActualizarInformacionUsuarios(id, nombre, apellido, ci, genero, tipo);
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
     * Actualiza la información de un usuario en la base de datos.
     *
     * @param id      El ID del usuario a actualizar.
     * @param nombre  El nuevo nombre del usuario.
     * @param apellido El nuevo apellido del usuario.
     * @param ci      La nueva cédula del usuario.
     * @param genero  El nuevo género del usuario.
     * @param tipo    El nuevo tipo de usuario.
     * @throws SQLException Si ocurre un error al actualizar la información en la base de datos.
     */
    public void ActualizarInformacionUsuarios(String id, String nombre, String apellido, String ci, String genero, String tipo) throws SQLException {
        Connection connection = Conexion();

        // Consulta para obtener los valores originales del usuario
        String sqlDos = "SELECT nombre_usuario, apellido_usuario, ci_usuario, genero_usuario, tipo_usuario FROM usuarios WHERE id_usuario = (?);";
        String[] valoresOriginales = new String[5];
        PreparedStatement x = connection.prepareStatement(sqlDos);
        x.setString(1, id);
        ResultSet y = x.executeQuery();

        if (y.next()) {
            valoresOriginales[0] = y.getString("nombre_usuario");
            valoresOriginales[1] = y.getString("apellido_usuario");
            valoresOriginales[2] = y.getString("ci_usuario");
            valoresOriginales[3] = y.getString("genero_usuario");
            valoresOriginales[4] = y.getString("tipo_usuario");
        }

        x.close();
        y.close();

        // Actualización de los datos del usuario
        String sql = "UPDATE usuarios SET nombre_usuario = (?), apellido_usuario = (?), ci_usuario = (?), genero_usuario = (?), tipo_usuario = (?) WHERE id_usuario = (?);";
        PreparedStatement prst = connection.prepareStatement(sql);

        prst.setString(1, nombre.isBlank() ? valoresOriginales[0] : nombre);
        prst.setString(2, apellido.isBlank() ? valoresOriginales[1] : apellido);
        prst.setString(3, ci.isBlank() ? valoresOriginales[2] : ci);
        prst.setString(4, genero.isBlank() ? valoresOriginales[3] : genero);
        prst.setString(5, tipo.isBlank() ? valoresOriginales[4] : tipo);
        prst.setString(6, id);

        int efecto = prst.executeUpdate();

        if (efecto > 0) {
            JOptionPane.showMessageDialog(null, "Datos actualizados exitosamente");
        }

        prst.close();
        connection.close();
    }

    /**
     * Establece la conexión con la base de datos.
     *
     * @return La conexión a la base de datos.
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
