import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * La clase Gestionar_Usuarios proporciona una interfaz gráfica para gestionar usuarios en una base de datos.
 */
public class Gestionar_Usuarios extends JFrame {
    private JTable usuariosTable;
    private JButton crearUsuarioButton;
    private JButton eliminarUsuarioButton;
    private JButton modificarUsuarioButton;
    private JComboBox<String> ordenComboBox;
    private JTextField nombreTextField;
    private JPanel Panel;
    private JButton buscarButton;
    private JButton volverButton;

    /**
     * Constructor de la clase Gestionar_Usuarios.
     * Inicializa los componentes de la interfaz gráfica y establece los manejadores de eventos.
     *
     * @throws SQLException Si ocurre un error al establecer la conexión con la base de datos.
     */
    public Gestionar_Usuarios() throws SQLException {
        super("Gestión de usuarios");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);

        CreatePanelOptions("", "");

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom = nombreTextField.getText();
                String organize = "";

                String x = (String) ordenComboBox.getSelectedItem();
                if (x.equals("Mayor a menor (id)")) {
                    organize = "id_usuario DESC";
                } else if (x.equals("Menor a mayor (id)")) {
                    organize = "id_usuario ASC";
                } else {
                    organize = "nombre_usuario ASC";
                }

                try {
                    CreatePanelOptions(nom, organize);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        modificarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Modifica_Usuario modificaUsuario = new Modifica_Usuario();
                modificaUsuario.setVisible(true);
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Menu_Administrador menuAdministrador = new Menu_Administrador();
                menuAdministrador.setVisible(true);
            }
        });

        eliminarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Eliminar_Usuario eliminarUsuario = new Eliminar_Usuario();
                eliminarUsuario.setVisible(true);
            }
        });

        crearUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Crear_Usuario crearUsuario = new Crear_Usuario();
                crearUsuario.setVisible(true);
            }
        });
    }

    /**
     * Crea y actualiza el panel de opciones con los resultados de búsqueda.
     *
     * @param nombre El nombre del usuario.
     * @param orden  El criterio de ordenamiento.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private void CreatePanelOptions(String nombre, String orden) throws SQLException {
        Connection connection = Conexion();

        String sql = "SELECT * FROM usuarios;";

        if (nombre.isEmpty() && orden.isEmpty()) {
            PreparedStatement prst = connection.prepareStatement(sql);
            ExecuteSQL(prst, connection);
        } else if (!nombre.isBlank() && orden.isBlank()) {
            sql = "SELECT * FROM usuarios WHERE nombre_usuario = (?);";
            PreparedStatement prst = connection.prepareStatement(sql);
            prst.setString(1, nombre);
            ExecuteSQL(prst, connection);
        } else if (nombre.isBlank() && !orden.isBlank()) {
            sql = "SELECT * FROM usuarios ORDER BY " + orden + ";";
            PreparedStatement prst = connection.prepareStatement(sql);
            ExecuteSQL(prst, connection);
        } else if (!nombre.isBlank() && !orden.isBlank()) {
            sql = "SELECT * FROM usuarios WHERE nombre_usuario = (?) ORDER BY " + orden + ";";
            PreparedStatement prst = connection.prepareStatement(sql);
            prst.setString(1, nombre);
            ExecuteSQL(prst, connection);
        }
    }

    /**
     * Ejecuta la consulta SQL y actualiza la tabla de usuarios con los resultados.
     *
     * @param prst       La sentencia SQL preparada.
     * @param connection La conexión a la base de datos.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public void ExecuteSQL(PreparedStatement prst, Connection connection) throws SQLException {
        ResultSet rs = prst.executeQuery();

        DefaultTableModel defaultTableModel = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "CI", "Género", "Tipo"}, 0);

        while (rs.next()) {
            int id = rs.getInt("id_usuario");
            String name = rs.getString("nombre_usuario");
            String lastname = rs.getString("apellido_usuario");
            String ci = rs.getString("ci_usuario");
            String genero = rs.getString("genero_usuario");
            String tipo = rs.getString("tipo_usuario");

            defaultTableModel.addRow(new Object[]{id, name, lastname, ci, genero, tipo});
        }

        usuariosTable.setModel(defaultTableModel);

        rs.close();
        prst.close();
        connection.close();
    }

    /**
     * Establece una conexión con la base de datos.
     *
     * @return La conexión con la base de datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
