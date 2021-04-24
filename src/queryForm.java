import org.json.JSONException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class queryForm {

    public JTextField nameText;
    public JTextField descText;
    public JRadioButton boolAtGymRadioButton;
    public JRadioButton boolAtHomeRadioButton;
    public JRadioButton boolOutsideRadioButton;
    public JRadioButton boolRepsSetsRadioButton;
    public JRadioButton boolBodyWeightRadioButton;
    public JRadioButton boolWeightsRadioButton;
    public JRadioButton boolTimerRadioButton;
    public JRadioButton boolStopwatchRadioButton;
    public JRadioButton boolDistanceRadioButton;
    public JCheckBox tricepsCheckBox;
    public JCheckBox pectoralsCheckBox;
    public JCheckBox deltoidsCheckBox;
    public JCheckBox quadsCheckBox;
    public JCheckBox hamstringsCheckBox;
    public JCheckBox latsCheckBox;
    public JCheckBox trapsCheckBox;
    public JCheckBox bicepsCheckBox;
    public JTextField youtubeText;
    public JButton ADDTOTABLEButton;
    public JTextField difficultyText;
    public JTextField idText;
    public JPanel root;
    public JButton saveTXT;


    public queryForm() {
        ADDTOTABLEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    SQLDatabase.connectAndStatementSQL(getAddExerciseString());
                    System.out.println("Success! Added " + nameText.getText());
                } catch (Exception exception) {
                    System.out.println(exception);
                }
            }
        });
        saveTXT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    saveFullAddExerciseToFile(getAddExerciseString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
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

    /**
     * Saves the txt to local filepath
     * @throws IOException
     * @throws JSONException
     */
    public void saveFullAddExerciseToFile(String toWrite) throws IOException {

        //Saves to location
        File textFile = new File("./", nameText.getText().trim().toLowerCase() + "ExerciseAdd.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(textFile));
        try {
            out.write(toWrite);
            System.out.println("Successfully wrote to file!");
        } finally {
            out.close();
        }
    }

    /**
     * Generates the output string for INSERT SQL
     * @return the sql String object
     */
    public String getAddExerciseString() {

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
                        difficultyText.getText() + ");";
        
        return sql;
    }

}
