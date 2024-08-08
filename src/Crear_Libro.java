import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class Crear_Libro extends JFrame {
    private JTextField tituloTextField;
    private JTextField anioTextField;
    private JTextArea descripcionTextArea;
    private JTextField generoTextField;
    private JTextField nombreAutorTextField;
    private JTextField apellidoAutorTextField;
    private JButton ingresarLibroButton;
    private JButton volverButton;
    private JPanel Panel;
    private JButton seleccionarArchivoButton;
    private JLabel archivoSeleccionadoLabel;

    private File archivoSeleccionado;
    private String extensionArchivo;

    public Crear_Libro() {
        super("Crear Libro");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);

        seleccionarArchivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Documentos", "pdf", "txt", "doc", "docx", "epub"));
                int result = fileChooser.showOpenDialog(Crear_Libro.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    archivoSeleccionado = fileChooser.getSelectedFile();
                    archivoSeleccionadoLabel.setText(archivoSeleccionado.getName());
                    // Obtener la extensión del archivo
                    String archivoNombre = archivoSeleccionado.getName();
                    int puntoIndex = archivoNombre.lastIndexOf('.');
                    if (puntoIndex > 0 && puntoIndex < archivoNombre.length() - 1) {
                        extensionArchivo = archivoNombre.substring(puntoIndex + 1).toLowerCase();
                    } else {
                        extensionArchivo = "";
                    }
                }
            }
        });

        ingresarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = tituloTextField.getText();
                String anio = anioTextField.getText();
                String descripcion = descripcionTextArea.getText();
                String genero = generoTextField.getText();
                String nombreAutor = nombreAutorTextField.getText();
                String apellidoAutor = apellidoAutorTextField.getText();

                if (titulo.isEmpty() || anio.isEmpty() || descripcion.isEmpty() || genero.isEmpty() || nombreAutor.isEmpty() || apellidoAutor.isEmpty() || archivoSeleccionado == null) {
                    JOptionPane.showMessageDialog(Crear_Libro.this, "Por favor, complete todos los campos y seleccione un archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection connection = Conexion()) {
                    String sql = "INSERT INTO libros (titulo_libro, nombre_autor_libro, apellido_autor_libro, genero_libro, descripcion_libro, anio_publicacion, archivo, extension_archivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement prst = connection.prepareStatement(sql);

                    prst.setString(1, titulo);
                    prst.setString(2, nombreAutor);
                    prst.setString(3, apellidoAutor);
                    prst.setString(4, genero);
                    prst.setString(5, descripcion);
                    prst.setDate(6, Date.valueOf(anio + "-01-01")); // Convertir año a fecha

                    // Leer el archivo y establecerlo en el PreparedStatement
                    FileInputStream fis = new FileInputStream(archivoSeleccionado);
                    prst.setBinaryStream(7, fis, (int) archivoSeleccionado.length());
                    prst.setString(8, extensionArchivo);

                    int filasAfectadas = prst.executeUpdate();
                    if (filasAfectadas > 0) {
                        JOptionPane.showMessageDialog(Crear_Libro.this, "Libro ingresado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(Crear_Libro.this, "Error al ingresar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    prst.close();
                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Crear_Libro.this, "Error al ingresar el libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
