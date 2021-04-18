import org.json.JSONException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class SaveJSONGUI {
    private JButton selectFilepathButton;
    private JTextField fileNameTextField;
    private JButton GENERATEJSONButton;
    private JPanel root;

    public static String filePath;
    public static String fileName;

    public SaveJSONGUI() {

        //Set the filepath away from the default C: drive
        selectFilepathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final JFileChooser jd = new JFileChooser();
                jd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jd.setDialogTitle("Choose File Path");

                int returnVal= jd.showOpenDialog(root.getComponent(0));
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    filePath = "../";
                } else {
                    filePath = jd.getSelectedFile().toString();
                }
            }
        });

        //Trigger generation of the JSON
        GENERATEJSONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Get filepath and filename
                String inputFilePath = fileNameTextField.getText();

                //Check file name is not null/empty
                if (inputFilePath != null && !inputFilePath.equals("")) {
                    fileName = inputFilePath;
                } else {
                    fileName = "gainSON";
                }

                try {
                    //If valid then generate JSON
                    JSONGenerator.generateJSON(filePath, fileName);

                    //If no error display success message
                    JOptionPane.showMessageDialog(null, "Generated JSON!");

                } catch (Exception exception) {

                    //If any errors display them
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null, "ERROR generating JSON: " + exception.getLocalizedMessage());

                }

            }
        });

    }

    /**
     * Displays the form
     * @param args
     */
    public static void main(String[] args) {
        JFrame root = new JFrame("gainSON Generator");
        root.setContentPane(new SaveJSONGUI().root);
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root.pack();
        root.setVisible(true);
    }
}
