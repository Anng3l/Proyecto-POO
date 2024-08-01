import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Modifica_Usuario extends JFrame {
    private JTextField idTextField;
    private JButton actualizarInformacionButton;
    private JButton cancelarButton;
    private JTextField nombreTextField;
    private JTextField apellidoTextField;
    private JTextField ciTextField;
    private JComboBox generoComboBox;
    private JComboBox tipoComboBox;
    private JPanel Panel;

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

    public void ActualizarInformacionUsuarios(String id, String nombre, String apellido, String ci, String genero, String tipo) throws SQLException
    {
        Connection connection = Conexion();

        String sqlDos = "SELECT nombre_usuario, apellido_usuario, ci_usuario, genero_usuario, tipo_usuario FROM usuarios WHERE id_usuario = (?);";

        String[] valoresOriginales = new String[5];
        PreparedStatement x = connection.prepareStatement(sqlDos);
        x.setString(1, id);
        ResultSet y = x.executeQuery();

        if (y.next())
        {
            valoresOriginales[0] = y.getString("nombre_usuario");
            valoresOriginales[1] = y.getString("apellido_usuario");
            valoresOriginales[2] = y.getString("ci_usuario");
            valoresOriginales[3] = y.getString("genero_usuario");
            valoresOriginales[4] = y.getString("tipo_usuario");
        }

        x.close();
        y.close();

        String sql = "UPDATE usuarios SET nombre_usuario = (?), apellido_usuario = (?), ci_usuario = (?), genero_usuario = (?), tipo_usuario = (?) WHERE id_usuario = (?);";

        PreparedStatement prst = connection.prepareStatement(sql);

        prst.setString(1, nombre = (nombre.isBlank()) ? valoresOriginales[0] : nombre);
        prst.setString(2, apellido = (apellido.isBlank()) ? valoresOriginales[1] : apellido);
        prst.setString(3, ci = (ci.isBlank()) ? valoresOriginales[2] : ci);
        prst.setString(4, genero = (genero.isBlank()) ? valoresOriginales[3] : genero);
        prst.setString(5, tipo = (tipo.isBlank()) ? valoresOriginales[4] : tipo);
        prst.setString(6, id);

        int efecto = prst.executeUpdate();

        if (efecto > 0)
        {
            JOptionPane.showMessageDialog(null, "Datos actualizados exitosamente");
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
