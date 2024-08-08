import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * La clase Historial_Descargas proporciona una interfaz gráfica para visualizar el historial de descargas de libros.
 */
public class Historial_Descargas extends JFrame {
    private JTable visualizarTable;
    private JButton volverButton;
    private JPanel Panel;

    /**
     * Constructor de la clase Historial_Descargas.
     * Inicializa los componentes de la interfaz gráfica y establece el manejador de eventos para el botón de volver.
     */
    public Historial_Descargas() {
        super("Historial de Descargas");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        
        visualizarTable.setModel(new DefaultTableModel(
                new String[]{"ID Usuario", "Nombre Usuario", "Apellido Usuario", "ID Libro", "Título Libro", "Fecha Descarga"}, 0));

        cargarDatos();

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Menu_Administrador menuAdministrador = new Menu_Administrador();
                menuAdministrador.setVisible(true);
            }
        });
    }

    /**
     * Carga los datos de las descargas en la tabla desde la base de datos.
     */
    private void cargarDatos() {
        try {
            Connection connection = Conexion();
            String sql = "SELECT d.fk_id_usuario, u.nombre_usuario, u.apellido_usuario, d.fk_id_libro, l.titulo_libro, d.fecha_descarga " +
                    "FROM descargas d " +
                    "JOIN usuarios u ON d.fk_id_usuario = u.id_usuario " +
                    "JOIN libros l ON d.fk_id_libro = l.id_libro";

            PreparedStatement prst = connection.prepareStatement(sql);
            ResultSet rs = prst.executeQuery();

            DefaultTableModel tableModel = (DefaultTableModel) visualizarTable.getModel();

            while (rs.next()) {
                int idUsuario = rs.getInt("fk_id_usuario");
                String nombreUsuario = rs.getString("nombre_usuario");
                String apellidoUsuario = rs.getString("apellido_usuario");
                int idLibro = rs.getInt("fk_id_libro");
                String tituloLibro = rs.getString("titulo_libro");
                Timestamp fechaDescarga = rs.getTimestamp("fecha_descarga");

                tableModel.addRow(new Object[]{idUsuario, nombreUsuario, apellidoUsuario, idLibro, tituloLibro, fechaDescarga});
            }

            rs.close();
            prst.close();
            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Establece una conexión con la base de datos.
     *
     * @return La conexión con la base de datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    private Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
