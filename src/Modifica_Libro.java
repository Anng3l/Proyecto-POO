import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Modifica_Libro extends JFrame {
    private JTextField idTextField;
    private JTextField tituloTextField;
    private JComboBox autorComboBox;
    private JTextArea descripcionTextArea;
    private JTextField anioTextField;
    private JButton actualizarLibroButton;
    private JButton volverButton;
    private JPanel Panel;

    public Modifica_Libro() {
        super("Modificar Libro");
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
