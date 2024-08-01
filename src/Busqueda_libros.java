import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;

public class Busqueda_libros extends JFrame {
    private JTable librosTable;
    private JComboBox<String> generoComboBox;
    private JComboBox<Date> yearComboBox;
    private JComboBox<String> autorComboBox;
    private JTextField tituloTextField;
    private JButton volverButton;
    private JPanel Panel;
    private JButton buscarButton;
    private JComboBox comboBox1;

    public Busqueda_libros() {
        super("Biblioteca");
        setContentPane(Panel);
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        CreatePanelOptions();
    }

    private void CreatePanelOptions(){
        librosTable.setModel(new DefaultTableModel(
                null,
                new String[] {"Título", "Autor", "Descripcion", "Año de publicación", "Archivo", "Creación", "Última edición"}
        ));
    }


}
