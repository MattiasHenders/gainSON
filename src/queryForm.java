import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class queryForm {
    private JTextField nameText;
    private JTextField descText;
    private JRadioButton boolAtGymRadioButton;
    private JRadioButton boolAtHomeRadioButton;
    private JRadioButton boolOutsideRadioButton;
    private JRadioButton boolRepsSetsRadioButton;
    private JRadioButton boolBodyWeightRadioButton;
    private JRadioButton boolWeightsRadioButton;
    private JRadioButton boolTimerRadioButton;
    private JRadioButton boolStopwatchRadioButton;
    private JRadioButton boolDistanceRadioButton;
    private JCheckBox tricepsCheckBox;
    private JCheckBox pectoralsCheckBox;
    private JCheckBox deltoidsCheckBox;
    private JCheckBox quadsCheckBox;
    private JCheckBox hamstringsCheckBox;
    private JCheckBox latsCheckBox;
    private JCheckBox trapsCheckBox;
    private JCheckBox bicepsCheckBox;
    private JTextField youtubeText;
    private JButton ADDTOTABLEButton;
    private JTextField difficultyText;
    private JTextField idText;
    private JPanel root;


    public queryForm() {
        ADDTOTABLEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sql =
                        "INSERT INTO Tracking VALUES(" + idText.getText() + ",\n" +
                                "/*boolRepsSets*/\n" +
                                (boolRepsSetsRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolBodyWeight*/\n" +
                                (boolBodyWeightRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolWeights*/\n" +
                                (boolWeightsRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolTimer*/\n" +
                                (boolTimerRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolStopwatch*/\n" +
                                (boolStopwatchRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolDistance*/\n" +
                                (boolDistanceRadioButton.isSelected() ? "1" : "0") +");\n" +
                                "\n" +
                                "insert into Locations values(" + idText.getText() + ", \n" +
                                "/*boolAtGym*/\n" +
                                (boolAtGymRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolAtHome*/\n" +
                                (boolAtHomeRadioButton.isSelected() ? "1" : "0") +",\n" +
                                "/*boolOutside*/\n" +
                                (boolOutsideRadioButton.isSelected() ? "1" : "0") +"\n" +
                                ");\n" +
                                "\n" +
                                "insert into MuscleGroups values(" + idText.getText() + ", \n" +
                                "/*triceps*/\n" +
                                (tricepsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*pectorals*/\n" +
                                (pectoralsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*deltoids*/\n" +
                                (deltoidsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*quads*/\n" +
                                (quadsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*hamstrings*/\n" +
                                (hamstringsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*lats*/\n" +
                                (latsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*traps*/\n" +
                                (trapsCheckBox.isSelected() ? "1" : "0") +",\n" +
                                "/*biceps*/\n" +
                                (bicepsCheckBox.isSelected() ? "1" : "0") +"\n" +
                                ");\n" +
                                "\n" +
                                "insert into Media values(" + idText.getText() + ",\n" +
                                "/*Youtube*/\n" +
                                "'" + youtubeText.getText() + "'\n" +
                                ");\n" +
                                "\n" +
                                "insert into SpanishData values(" + idText.getText() + ",\n" +
                                "/*spanish name*/\n" +
                                "'Spanish " + nameText.getText().toLowerCase() + "',\n" +
                                "/*spanish Description*/\n" +
                                "'Text for spanish " + nameText.getText().toLowerCase() + " here',\n" +
                                "/*language name*/\n" +
                                "'Spanish'\n" +
                                ");\n" +
                                "\n" +
                                "insert into FrenchData values(" + idText.getText() + ",\n" +
                                "/*French name*/\n" +
                                "'French " + nameText.getText().toLowerCase() + "',\n" +
                                "/*French Description*/\n" +
                                "'Text for french " + nameText.getText().toLowerCase() + " here',\n" +
                                "/*language name*/\n" +
                                "'French'\n" +
                                ");\n" +
                                "\n" +
                                "insert into Exercises values(" + idText.getText() + ",\n" +
                                "/*Name*/\n" +
                                "'" + nameText.getText() + "',\n" +
                                "/*Description*/\n" +
                                "'" + descText.getText() + "',\n" +
                                "/*Difficulty*/\n" +
                                difficultyText.getText() + ");\n" +
                                "\n";

                try {
                    SQLDatabase.connectAndStatementSQL(sql);
                    System.out.println("Success! Added " + nameText.getText());
                } catch (Exception exception) {
                    System.out.println(exception);
                }
            }
        });
    }

    /**
     * Displays the form
     * @param args
     */
    public static void main(String[] args) {
        JFrame root = new JFrame("gainSON Exercise Maker");
        root.setContentPane(new queryForm().root);
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root.pack();
        root.setVisible(true);
    }

}
