import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * La clase Eliminar_Usuario proporciona una interfaz gráfica para eliminar usuarios de una base de datos.
 */
public class Eliminar_Usuario extends JFrame {
    private JTextField idTextField;
    private JButton eliminarButton;
    private JPanel Panel;
    private JButton volverButton;

    /**
     * Constructor de la clase Eliminar_Usuario.
     * Inicializa los componentes de la interfaz gráfica y establece los manejadores de eventos.
     */
    public Eliminar_Usuario() {
        super("Eliminar usuario");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 300);

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText();

                try {
                    EliminarUsuario(id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Elimina un usuario de la base de datos dado su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @throws SQLException Si ocurre un error al eliminar el usuario.
     */
    public void EliminarUsuario(String id) throws SQLException {
        Connection connection = Conexion();

        String sql = "DELETE FROM usuarios WHERE id_usuario = (?)";

        PreparedStatement prst = connection.prepareStatement(sql);

        prst.setString(1, id);

        int effect = prst.executeUpdate();

        if (effect > 0) {
            JOptionPane.showMessageDialog(null, "Usuario eliminado exitosamente");
        }
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
