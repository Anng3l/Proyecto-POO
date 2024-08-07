import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Gestionar_Libros extends JFrame {
    private JTextField autorTextField;
    private JTextField tituloTextField;
    private JTextField idTextField;
    private JTable librosTable;
    private JButton buscarButton;
    private JButton ingresarLibroButton;
    private JButton eliminarLibroButton;
    private JButton modificarLibroButton;
    private JButton volverButton;
    private JButton descargarButton;
    private JPanel Panel;
    private JComboBox<String> ordenComboBox;

    public Gestionar_Libros() throws SQLException {
        super("Gestión de Libros");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);

        // Inicializar el JComboBox con las opciones de ordenamiento
        ordenComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Mayor a menor (id)",
                "Menor a mayor (id)",
                "Alfabeticamente (titulo)"
        }));

        CreatePanelOptions("", "", "", "");

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText();
                String titulo = tituloTextField.getText();
                String autor = autorTextField.getText();
                String organize = "";

                String x = (String) ordenComboBox.getSelectedItem();
                if (x.equals("Mayor a menor (id)")) {
                    organize = "id_libro DESC";
                } else if (x.equals("Menor a mayor (id)")) {
                    organize = "id_libro ASC";
                } else {
                    organize = "titulo_libro ASC";
                }

                try {
                    CreatePanelOptions(id, titulo, autor, organize);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        modificarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Modifica_Libro modificaLibro = new Modifica_Libro();
                modificaLibro.setVisible(true);
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

        eliminarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Eliminar_Libro eliminarLibro = new Eliminar_Libro();
                eliminarLibro.setVisible(true);
            }
        });

        ingresarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Crear_Libro crearLibro = new Crear_Libro();
                crearLibro.setVisible(true);
            }
        });

        descargarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    downloadFile();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Gestionar_Libros.this, "Error al descargar el archivo.");
                    ex.printStackTrace();
                }
            }
        });
    }

    private void CreatePanelOptions(String id, String titulo, String autor, String orden) throws SQLException {
        Connection connection = Conexion();

        String sql = "SELECT libros.id_libro, libros.titulo_libro, libros.nombre_autor_libro, libros.apellido_autor_libro, libros.genero_libro, libros.descripcion_libro, libros.anio_publicacion, " +
                "libros.historial_creacion_libro, libros.historial_edicion_libro, libros.extension_archivo, " +
                "GROUP_CONCAT(CONCAT(libros.nombre_autor_libro, ' ', libros.apellido_autor_libro)) AS autores FROM libros " +
                "WHERE 1=1";

        if (!id.isEmpty()) {
            sql += " AND libros.id_libro = ?";
        }
        if (!titulo.isEmpty()) {
            sql += " AND libros.titulo_libro LIKE ?";
        }
        if (!autor.isEmpty()) {
            sql += " AND (libros.nombre_autor_libro LIKE ? OR libros.apellido_autor_libro LIKE ?)";
        }
        sql += " GROUP BY libros.id_libro";
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
                new String[]{"ID", "Titulo", "Autores", "Género", "Descripcion", "Año", "Fecha de Creacion", "Actualización", "Archivo", "Extensión"}, 0);

        while (rs.next()) {
            int id = rs.getInt("id_libro");
            String titulo = rs.getString("titulo_libro");
            String autores = rs.getString("autores");
            String genero = rs.getString("genero_libro");
            String descripcion = rs.getString("descripcion_libro");
            String anio = rs.getString("anio_publicacion");
            String creacion = rs.getString("historial_creacion_libro");
            String edicion = rs.getString("historial_edicion_libro");
            String extension = rs.getString("extension_archivo"); // La extensión del archivo

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
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo.");
            e.printStackTrace();
        } finally {
            rs.close();
            prst.close();
            connection.close();
        }
    }

    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/proyectofinal";
        String user = "root";
        String password = "vamossobreruedasdefuegoAa@_";

        return DriverManager.getConnection(url, user, password);
    }
}










/*
public class Gestionar_Libros extends JFrame {
    private JTextField autorTextField;
    private JTextField tituloTextField;
    private JTextField idTextField;
    private JTable librosTable;
    private JButton buscarButton;
    private JButton ingresarLibroButton;
    private JButton eliminarLibroButton;
    private JButton modificarLibroButton;
    private JButton volverButton;
    private JPanel Panel;
    private JComboBox ordenComboBox;

    public Gestionar_Libros() throws SQLException {
        super("Gestión de Libros");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);

        // Inicializar el JComboBox con las opciones de ordenamiento

        CreatePanelOptions("", "", "", "");

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText();
                String titulo = tituloTextField.getText();
                String autor = autorTextField.getText();
                String organize = "";

                String x = (String) ordenComboBox.getSelectedItem();
                if (x.equals("Mayor a menor (id)")) {
                    organize = "id_libro DESC";
                } else if (x.equals("Menor a mayor (id)")) {
                    organize = "id_libro ASC";
                } else {
                    organize = "titulo_libro ASC";
                }

                try {
                    CreatePanelOptions(id, titulo, autor, organize);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        modificarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Modifica_Libro modificaLibro = new Modifica_Libro();
                modificaLibro.setVisible(true);
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

        eliminarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Eliminar_Libro eliminarLibro = new Eliminar_Libro();
                eliminarLibro.setVisible(true);
            }
        });

        ingresarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Crear_Libro crearLibro = new Crear_Libro();
                crearLibro.setVisible(true);
            }
        });
    }

    private void CreatePanelOptions(String id, String titulo, String autor, String orden) throws SQLException {
        Connection connection = Conexion();

        String sql = "SELECT libros.id_libro, libros.titulo_libro, libros.nombre_autor_libro, libros.apellido_autor_libro, libros.genero_libro, libros.descripcion_libro, libros.anio_publicacion, " +
                "libros.historial_creacion_libro, libros.historial_edicion_libro, " +
                "GROUP_CONCAT(CONCAT(libros.nombre_autor_libro, ' ', libros.apellido_autor_libro)) AS autores FROM libros " +
                "WHERE 1=1";

        if (!id.isEmpty()) {
            sql += " AND libros.id_libro = ?";
        }
        if (!titulo.isEmpty()) {
            sql += " AND libros.titulo_libro LIKE ?";
        }
        if (!autor.isEmpty()) {
            sql += " AND (libros.nombre_autor_libro LIKE ? OR libros.apellido_autor_libro LIKE ?)";
        }
        sql += " GROUP BY libros.id_libro";
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
                new String[]{"ID", "Titulo", "Autores", "Género", "Descripcion", "Año", "Fecha de Creacion", "Actualización"}, 0);

        while (rs.next()) {
            int id = rs.getInt("id_libro");
            String titulo = rs.getString("titulo_libro");
            String autores = rs.getString("autores");
            String genero = rs.getString("genero_libro");
            String descripcion = rs.getString("descripcion_libro");
            String anio = rs.getString("anio_publicacion");
            String creacion = rs.getString("historial_creacion_libro");
            String edicion = rs.getString("historial_edicion_libro");

            defaultTableModel.addRow(new Object[]{id, titulo, autores, genero, descripcion, anio, creacion, edicion});
        }

        librosTable.setModel(defaultTableModel);

        rs.close();
        prst.close();
        connection.close();
    }

    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/proyectofinal";
        String user = "root";
        String password = "vamossobreruedasdefuegoAa@_";

        return DriverManager.getConnection(url, user, password);
    }
}

*/
