import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Eliminar_Usuario extends JFrame {
    private JTextField idTextField;
    private JButton eliminarButton;
    private JPanel Panel;
    private JButton volverButton;

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


    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
