import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class Menu_Usuario extends JFrame {
    private JTextField autorTextField;
    private JTextField tituloTextField;
    private JTextField idTextField;
    private JComboBox<String> ordenComboBox;
    private JTable librosTable;
    private JButton buscarButton;
    private JButton descargarButton;
    private JButton volverButton;
    private JPanel Panel;

    public Menu_Usuario() {
        super("Menú de Usuario");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText();
                String titulo = tituloTextField.getText();
                String autor = autorTextField.getText();
                String orden = "";

                String selectedOrder = (String) ordenComboBox.getSelectedItem();
                if (selectedOrder.equals("Mayor a menor (id)")) {
                    orden = "id_libro DESC";
                } else if (selectedOrder.equals("Menor a mayor (id)")) {
                    orden = "id_libro ASC";
                } else {
                    orden = "titulo_libro ASC";
                }

                try {
                    CreatePanelOptions(id, titulo, autor, orden);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Menu_Usuario.this, "Error al realizar la búsqueda.");
                    ex.printStackTrace();
                }
            }
        });

        descargarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    downloadFile();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Menu_Usuario.this, "Error al descargar el archivo.");
                    ex.printStackTrace();
                }
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.setVisible(true);
            }
        });
    }

    private void CreatePanelOptions(String id, String titulo, String autor, String orden) throws SQLException {
        Connection connection = Conexion();

        String sql = "SELECT id_libro, titulo_libro, nombre_autor_libro, apellido_autor_libro, genero_libro, descripcion_libro, anio_publicacion, " +
                "historial_creacion_libro, historial_edicion_libro, extension_archivo " +
                "FROM libros WHERE 1=1";

        if (!id.isEmpty()) {
            sql += " AND id_libro = ?";
        }
        if (!titulo.isEmpty()) {
            sql += " AND titulo_libro LIKE ?";
        }
        if (!autor.isEmpty()) {
            sql += " AND (nombre_autor_libro LIKE ? OR apellido_autor_libro LIKE ?)";
        }
        if (!orden.isEmpty()) {
            sql += " ORDER BY " + orden;
        }

        PreparedStatement prst = connection.prepareStatement(sql);

        int paramIndex = 1;
        if (!id.isEmpty()) {
            prst.setInt(paramIndex++, Integer.parseInt(id));
        }
        if (!titulo.isEmpty()) {
            prst.setString(paramIndex++, "%" + titulo + "%");
        }
        if (!autor.isEmpty()) {
            prst.setString(paramIndex++, "%" + autor + "%");
            prst.setString(paramIndex++, "%" + autor + "%");
        }

        ExecuteSQL(prst, connection);
    }

    public void ExecuteSQL(PreparedStatement prst, Connection connection) throws SQLException {
        ResultSet rs = prst.executeQuery();

        DefaultTableModel defaultTableModel = new DefaultTableModel(
                new String[]{"ID", "Título", "Autores", "Género", "Descripción", "Año", "Fecha de Creación", "Actualización", "Archivo", "Extensión"}, 0);

        while (rs.next()) {
            int id = rs.getInt("id_libro");
            String titulo = rs.getString("titulo_libro");
            String autores = rs.getString("nombre_autor_libro") + " " + rs.getString("apellido_autor_libro");
            String genero = rs.getString("genero_libro");
            String descripcion = rs.getString("descripcion_libro");
            String anio = rs.getString("anio_publicacion");
            String creacion = rs.getString("historial_creacion_libro");
            String edicion = rs.getString("historial_edicion_libro");
            String extension = rs.getString("extension_archivo");

            defaultTableModel.addRow(new Object[]{id, titulo, autores, genero, descripcion, anio, creacion, edicion, "Descargar", extension});
        }

        librosTable.setModel(defaultTableModel);

        rs.close();
        prst.close();
        connection.close();
    }

    private void downloadFile() throws SQLException {
        int selectedRow = librosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila.");
            return;
        }

        Connection connection = Conexion();
        int id = (int) librosTable.getValueAt(selectedRow, 0); // ID del libro
        String extension = (String) librosTable.getValueAt(selectedRow, 9); // Extensión del archivo

        String sql = "SELECT archivo FROM libros WHERE id_libro = ?";
        PreparedStatement prst = connection.prepareStatement(sql);
        prst.setInt(1, id);
        ResultSet rs = prst.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "No hay archivo para descargar.");
            return;
        }

        Blob archivoBlob = rs.getBlob("archivo");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Archivo");
        fileChooser.setSelectedFile(new File("archivo." + extension)); // Usa la extensión almacenada
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        try (FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
            InputStream inputStream = archivoBlob.getBinaryStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            JOptionPane.showMessageDialog(this, "Archivo descargado exitosamente.");
            registerDownload(id);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo.");
            e.printStackTrace();
        } finally {
            rs.close();
            prst.close();
            connection.close();
        }
    }

    private void registerDownload(int bookId) throws SQLException {
        Integer userId = getCurrentUserId();
        System.out.println("ID de usuario actual: " + userId);

        Connection connection = Conexion();
        try {
            String checkUserSql = "SELECT COUNT(*) FROM usuarios WHERE id_usuario = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSql);
            checkUserStmt.setInt(1, userId);
            ResultSet rsUser = checkUserStmt.executeQuery();
            if (rsUser.next() && rsUser.getInt(1) == 0) {
                throw new SQLException("El usuario con ID " + userId + " no existe.");
            }
            rsUser.close();
            checkUserStmt.close();

            String checkBookSql = "SELECT COUNT(*) FROM libros WHERE id_libro = ?";
            PreparedStatement checkBookStmt = connection.prepareStatement(checkBookSql);
            checkBookStmt.setInt(1, bookId);
            ResultSet rsBook = checkBookStmt.executeQuery();
            if (rsBook.next() && rsBook.getInt(1) == 0) {
                throw new SQLException("El libro con ID " + bookId + " no existe.");
            }
            rsBook.close();
            checkBookStmt.close();

            String sql = "INSERT INTO descargas (fk_id_usuario, fk_id_libro, fecha_descarga) VALUES (?, ?, ?)";
            PreparedStatement prst = connection.prepareStatement(sql);
            prst.setInt(1, userId);
            prst.setInt(2, bookId);
            prst.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Usa Timestamp para la fecha actual

            prst.executeUpdate();
            prst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar la descarga: " + e.getMessage());
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private Integer getCurrentUserId() {

        return Integer.valueOf(SessionManager.getCurrentUserId());
    }

    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
