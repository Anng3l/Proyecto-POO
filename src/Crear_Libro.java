import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Crear_Libro extends JFrame {
    private JTextField tituloTextField;
    private JTextField anioTextField;
    private JTextArea descripcionTextArea;
    private JTextField generoTextField;
    private JButton ingresarLibroButton;
    private JComboBox autorComboBox;
    private JButton volverButton;
    private JPanel Panel;

    public Crear_Libro() {
        super("Crear Libro");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


    }
}
