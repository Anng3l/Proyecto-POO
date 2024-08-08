import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

/**
 * La clase Modifica_Libro proporciona una interfaz gráfica para modificar la información de un libro en la base de datos.
 * Permite al usuario actualizar el título, autor, descripción y fecha de publicación del libro.
 */
public class Modifica_Libro extends JFrame {
    private JTextField idTextField;
    private JTextField tituloTextField;
    private JTextField nombreTextField;
    private JTextField apellidoTextField;
    private JTextArea descripcionTextArea;
    private JTextField anioTextField;
    private JTextField mesTextField;
    private JTextField diaTextField;
    private JButton actualizarLibroButton;
    private JButton volverButton;
    private JPanel Panel;

    /**
     * Constructor de la clase Modifica_Libro.
     * Inicializa los componentes de la interfaz gráfica y establece los manejadores de eventos.
     */
    public Modifica_Libro() {
        super("Modificar Libro");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);

        actualizarLibroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText();
                String titulo = tituloTextField.getText();
                String nombreAutor = nombreTextField.getText();
                String apellidoAutor = apellidoTextField.getText();
                String descripcion = descripcionTextArea.getText();

                // Construir la fecha desde los campos anioTextField, mesTextField y diaTextField
                String anioString = anioTextField.getText();
                String mesString = mesTextField.getText();
                String diaString = diaTextField.getText();

                java.sql.Date fechaPublicacion = null;
                try {
                    if (!anioString.isBlank() && !mesString.isBlank() && !diaString.isBlank()) {
                        int anio = Integer.parseInt(anioString);
                        int mes = Integer.parseInt(mesString);
                        int dia = Integer.parseInt(diaString);
                        fechaPublicacion = java.sql.Date.valueOf(String.format("%d-%02d-%02d", anio, mes, dia));
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, "Formato de fecha inválido. Use YYYY-MM-DD.");
                    return;
                }

                try {
                    actualizarInformacionLibros(id, titulo, nombreAutor, apellidoAutor, descripcion, fechaPublicacion);
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
     * Actualiza la información de un libro en la base de datos.
     *
     * @param id              El ID del libro a actualizar.
     * @param titulo          El nuevo título del libro.
     * @param nombreAutor     El nuevo nombre del autor del libro.
     * @param apellidoAutor   El nuevo apellido del autor del libro.
     * @param descripcion     La nueva descripción del libro.
     * @param fechaPublicacion La nueva fecha de publicación del libro.
     * @throws SQLException Si ocurre un error al actualizar la información en la base de datos.
     */
    public void actualizarInformacionLibros(String id, String titulo, String nombreAutor, String apellidoAutor, String descripcion, java.sql.Date fechaPublicacion) throws SQLException {
        Connection connection = conexion();

        // Consulta para obtener los valores originales del libro
        String sqlDos = "SELECT titulo_libro, nombre_autor_libro, apellido_autor_libro, descripcion_libro, anio_publicacion FROM libros WHERE id_libro = (?);";
        String[] valoresOriginales = new String[5];
        PreparedStatement x = connection.prepareStatement(sqlDos);
        x.setString(1, id);
        ResultSet y = x.executeQuery();

        if (y.next()) {
            valoresOriginales[0] = y.getString("titulo_libro");
            valoresOriginales[1] = y.getString("nombre_autor_libro");
            valoresOriginales[2] = y.getString("apellido_autor_libro");
            valoresOriginales[3] = y.getString("descripcion_libro");
            valoresOriginales[4] = y.getString("anio_publicacion");
        }

        x.close();
        y.close();

        // Actualización de los datos del libro
        String sql = "UPDATE libros SET titulo_libro = (?), nombre_autor_libro = (?), apellido_autor_libro = (?), descripcion_libro = (?), anio_publicacion = (?) WHERE id_libro = (?);";
        PreparedStatement prst = connection.prepareStatement(sql);

        prst.setString(1, titulo.isBlank() ? valoresOriginales[0] : titulo);
        prst.setString(2, nombreAutor.isBlank() ? valoresOriginales[1] : nombreAutor);
        prst.setString(3, apellidoAutor.isBlank() ? valoresOriginales[2] : apellidoAutor);
        prst.setString(4, descripcion.isBlank() ? valoresOriginales[3] : descripcion);
        prst.setDate(5, fechaPublicacion != null ? fechaPublicacion : java.sql.Date.valueOf(valoresOriginales[4]));
        prst.setString(6, id);

        int efecto = prst.executeUpdate();

        if (efecto > 0) {
            JOptionPane.showMessageDialog(null, "Datos del libro actualizados exitosamente");
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
    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";

        return DriverManager.getConnection(url, user, password);
    }
}
