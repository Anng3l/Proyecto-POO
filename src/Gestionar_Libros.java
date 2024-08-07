import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

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
        super("Gestión de usuarios");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);

        CreatePanelOptions("", "");

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = tituloTextField.getText();
                String organize = "";

                String x = (String) ordenComboBox.getSelectedItem();
                if (x.equals("Mayor a menor (id)"))
                {
                    organize = "id_libro DESC";
                }
                else if (x.equals("Menor a mayor (id)"))
                {
                    organize = "id_libro ASC";
                }
                else
                {
                    organize = "titulo_libro ASC";
                }


                try {
                    CreatePanelOptions(titulo, organize);
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


    private void CreatePanelOptions(String titulo, String orden) throws SQLException {
        Connection connection = Conexion();

        String sql = "SELECT * FROM libros " +
                     "JOIN libros_autores ON libros.id_libro = libros_autores.fk_id_libro " +
                     "JOIN autores ON autores.id_autor = libros_autores.fk_id_autor;";


        if (titulo.isEmpty() && orden.isEmpty())
        {
            PreparedStatement prst = connection.prepareStatement(sql);

            ExecuteSQL(prst, connection);
        }
        else if (!titulo.isBlank() && orden.isBlank())
        {
            sql = "SELECT * FROM libros JOIN libros_autores ON libros.id_libro = libros_autores.fk_id_libro" +
                    "JOIN autores ON autores.id_autor = libros_autores.fk_id_autor" +
                    " WHERE titulo_libro = (?);";
            PreparedStatement prst = connection.prepareStatement(sql);
            prst.setString(1, titulo);

            ExecuteSQL(prst, connection);
        }
        else if (titulo.isBlank() && !orden.isBlank()) {
            sql = "SELECT * FROM libros JOIN libros_autores ON libros.id_libro = libros_autores.fk_id_libro " +
                    "JOIN autores ON autores.id_autor = libros_autores.fk_id_autor " +
                    "ORDER BY " + orden + ";";
            PreparedStatement prst = connection.prepareStatement(sql);

            ExecuteSQL(prst, connection);
        }
        /*
        else if (!nombre.isBlank() && !orden.isBlank()) {
            sql = "SELECT * FROM libros WHERE nombre_usuario = (?) ORDER BY " + orden + ";";
            PreparedStatement prst = connection.prepareStatement(sql);
            prst.setString(1, nombre);

            ExecuteSQL(prst, connection);
        }*/
    }



    public void ExecuteSQL(PreparedStatement prst, Connection connection) throws SQLException {
        ResultSet rs = prst.executeQuery();

        DefaultTableModel defaultTableModel = new DefaultTableModel(
                new String[] {"ID", "Titulo", "Autor", "Descripcion", "Año", "Fecha de Creacion", "Actualización"}, 0);

        while (rs.next())
        {
            int id = rs.getInt("id_libro");
            String titulo = rs.getString("titulo_libro");
            String nickname = rs.getString("nickname_autor");
            String descripcion = rs.getString("descripcion_libro");
            String anio = rs.getString("anio_publicacion");
            String creacion = rs.getString("historial_creacion_libro");
            String edicion = rs.getString("historial_edicion_libro");
            //String archivo = rs.getString("archivo");

            defaultTableModel.addRow(new Object[]{id, titulo, nickname, descripcion, anio, creacion, edicion});
        }

        librosTable.setModel(defaultTableModel);

        rs.close();
        prst.close();
        connection.close();
    }

    public Connection Conexion() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/proyecto";
        String user = "root";
        String password = "vamossobreruedasdefuegoAa@_";

        return DriverManager.getConnection(url, user, password);
    }
}
