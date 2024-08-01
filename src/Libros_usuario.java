import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Libros_usuario extends JFrame {
    private JTextField titleField;
    private JComboBox<String> authorComboBox;
    private JComboBox<String> genreComboBox;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public Libros_usuario() {
        setTitle("Library Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Author:"));
        authorComboBox = new JComboBox<>();
        inputPanel.add(authorComboBox);

        inputPanel.add(new JLabel("Genre:"));
        genreComboBox = new JComboBox<>();
        inputPanel.add(genreComboBox);

        add(inputPanel, BorderLayout.NORTH);

        // Botón de búsqueda
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchButtonListener());
        add(searchButton, BorderLayout.CENTER);

        // Tabla de libros
        booksTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"Title", "Author", "Genre"}, 0);
        booksTable.setModel(tableModel);
        add(new JScrollPane(booksTable), BorderLayout.SOUTH);

        loadAuthors();
        loadGenres();
    }

    private void loadAuthors() {
        try (Connection conn = conexion()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nickname_autor FROM autores");
            while (rs.next()) {
                authorComboBox.addItem(rs.getString("nickname_autor"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGenres() {
        try (Connection conn = conexion()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nombre_genero FROM generos");
            while (rs.next()) {
                genreComboBox.addItem(rs.getString("nombre_genero"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchBooks();
        }
    }

    private void searchBooks() {
        String title = titleField.getText();
        String author = (String) authorComboBox.getSelectedItem();
        String genre = (String) genreComboBox.getSelectedItem();

        tableModel.setRowCount(0);

        String query = "SELECT l.titulo_libro, a.nickname_autor, g.nombre_genero " +
                "FROM libros l " +
                "JOIN libros_autores la ON l.id_libro = la.fk_id_libro " +
                "JOIN autores a ON la.fk_id_autor = a.id_autor " +
                "JOIN libros_generos lg ON l.id_libro = lg.fk_id_libro " +
                "JOIN generos g ON lg.fk_id_genero = g.id_genero " +
                "WHERE l.titulo_libro LIKE ? AND a.nickname_autor = ? AND g.nombre_genero = ?";

        try (Connection conn = conexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + title + "%");
            stmt.setString(2, author);
            stmt.setString(3, genre);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("titulo_libro"),
                        rs.getString("nickname_autor"),
                        rs.getString("nombre_genero")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/proyecto";
        String user = "root";
        String password = "vamossobreruedasdefuegoAa@_";
        /*
        String url = "jdbc:mysql://u4zbafnoplzh3tko:DVSH9VULhHuUDlV4G322@" +
                "bf6cezx2kmkamarpt4ii-mysql.services.clever-cloud.com:3306/bf6cezx2kmkamarpt4ii";
        String user = "u4zbafnoplzh3tko";
        String password = "DVSH9VULhHuUDlV4G322";
         */
        return DriverManager.getConnection(url, user, password);
    }
}