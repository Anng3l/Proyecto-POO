import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Eliminar_Libro extends JFrame {
    private JTextField idTextField;
    private JButton eliminarLibroButton;
    private JButton volverButton;
    private JPanel Panel;

    public Eliminar_Libro() {
        super("Eliminar Libro");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        eliminarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer idLibro = Integer.valueOf(idTextField.getText());
                    EliminarLibro(idLibro);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Eliminar_Libro.this, "ID de libro inválido. Debe ser un número entero.");
                }
            }
        });
    }

    public void EliminarLibro(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Establecer la conexión a la base de datos
            conn = conexion();

            // Preparar la sentencia SQL para eliminar el libro
            String sql = "DELETE FROM libros WHERE id_libro = ?";
            stmt = conn.prepareStatement(sql);

            // Establecer el parámetro de la sentencia SQL
            stmt.setInt(1, id);

            // Ejecutar la sentencia SQL
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Libro eliminado con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el libro con el ID proporcionado.");
            }
        } catch (SQLException e) {
            // Manejo de errores
            JOptionPane.showMessageDialog(this, "Error al eliminar el libro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
