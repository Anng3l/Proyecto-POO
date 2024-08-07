import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Eliminar_Libro extends JFrame {
    private JTextField idTextField;
    private JButton eliminarLibroButton;
    private JButton volverButton;
    private JPanel Panel;

    public Eliminar_Libro() {
        super("Eliminar Libro");
        setContentPane(Panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(200, 200);

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
