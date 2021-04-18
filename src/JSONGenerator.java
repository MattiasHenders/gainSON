import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Drives the application.
 */
public class JSONGenerator {

    //==========================================================================
    //  CHANGE LOG
    //  1) Increment VERSION CODE
    //  2) Add date and brief note on addition to the version
    //==========================================================================
    //
    //final static String VERSION_CODE  = "0.0.1"; 20210413 First log, connects to database and gets a query. start of JSON building
    //final static String VERSION_CODE  = "0.0.2"; 20210415 Starting dynamic grabbing of column data, need to separate into 3 functions
    //final static String VERSION_CODE  = "0.0.3"; 20210416 Dynamic loading works, saves file to path. Need to work on foreign data next
    //final static String VERSION_CODE  = "0.0.4"; 20210417 Foreign data is added correctly. Full JSON is complete
    //final static String VERSION_CODE  = "0.0.5"; 20210417 Added methods to allow the user to filter out certain exercises
    //final static String VERSION_CODE  = "0.0.6"; 20210417 Moved methods around and created a basic GUI for saving the JSON
    final static String VERSION_CODE  = "0.0.7";

    public static final String getFullTable =
            "select * from Exercises as e \n" +
                    "join MuscleGroups as mg on e.exerciseID = mg.muscleGroupsID\n" +
                    "join Locations as l on e.exerciseID = l.locationsID\n" +
                    "join Tracking as t on e.exerciseID = t.trackingID\n" +
                    "join Media as md on md.mediaID = e.exerciseID\n" +
                    "order by e.exerciseID asc";

    public static JSONObject exerciseJSON;

    public static int currentExerciseID = -1;

    public static boolean skipExercise = false;

    /** SQL Data Types */
    public static final int BOOL = 0;
    public static final int INT = 1;
    public static final int FLOAT = 2;
    public static final int STRING = 3;
    public static final int ARRAY = 4;

    /**
     * Generate the JSON and drives the program
     * @param filePath path to save JSON to
     * @param fileName name to call the file
     * @throws JSONException
     * @throws SQLException
     * @throws IOException
     */
    public static void generateJSON(String filePath, String fileName) throws JSONException, SQLException, IOException {

        //Get the exercise table to count how many exercises we have
        String exerciseSQL = "select * from exercises";
        ResultSet result = SQLDatabase.connectAndExecuteSQL(exerciseSQL);

        //Create the full JSON Object to be returned
        JSONObject fullJSON = new JSONObject();

        //Add meta data to the top
        fullJSON.put("meta", getMetaJSON());

        //Add full exerciseData below meta data
        fullJSON.put("exerciseData", getDataJSON(result));

        //System.out.println(fullJSON.toString(3));

        //Write the JSON to memory
        saveFullJSONToFile(fullJSON, filePath, fileName);

    }

    /**
     * Saves the JSON to a filepath
     * @param jsonRaw the raw JSON to be saved
     * @param filePath path to store file
     * @param fileName name of the file to save
     * @throws IOException
     * @throws JSONException
     */
    public static void saveFullJSONToFile(JSONObject jsonRaw, String filePath, String fileName) throws IOException, JSONException {

        //Save the JSON with 2 space spacing.
        String toWrite = jsonRaw.toString(2);

        //Saves to location
        File textFile = new File(filePath, fileName + ".json");
        BufferedWriter out = new BufferedWriter(new FileWriter(textFile));
        try {
            out.write(toWrite);
        } finally {
            out.close();
        }


    }

    /**
     * Gets the JSON Object full of meta data for debugging and versioning.
     * @return JSON object to go under meta
     * @throws JSONException
     */
    public static JSONObject getMetaJSON() throws JSONException {

        //Create JSON object to hold meta data
        JSONObject returnJSON = new JSONObject();

        //Get data from system
        Date now = new Date();

        //Put data into the JSON
        returnJSON.put("date", now.toString());
        returnJSON.put("version", VERSION_CODE);

        return returnJSON;
    }

    /**
     * Gets the JSON Object full of exercise data for the full JSON.
     * @return JSON object to go under exerciseData
     * @throws JSONException
     */
    public static JSONObject getDataJSON(ResultSet dbResult) throws JSONException, SQLException {

        //Create JSON object to hold exercise data
        JSONObject returnJSON = new JSONObject();

        //Loop through results
        while (dbResult.next()) {

            //Reset the skip flag
            skipExercise = false;

            //Reset the exercise object
            exerciseJSON = new JSONObject();

            //Get the currentExerciseID, used later
            currentExerciseID = dbResult.getInt("exerciseID");

            //Add the immediate variables from the exercise table
            String exerciseSQL = "select * from Exercises where exerciseID = " + currentExerciseID;
            addFullTableValuesToJSON(exerciseSQL, "");

            //Create locations object
            String locationsSQL = "select * from Locations where locationsID = " + currentExerciseID;
            exerciseJSON.put("locations", addTableObjectToJSON(locationsSQL, "locationsID"));

            //Create tracking object
            String trackingSQL = "select * from tracking where trackingID = " + currentExerciseID;
            exerciseJSON.put("tracking", addTableObjectToJSON(trackingSQL, "trackingID"));

            //Create muscle groups array
            String musclegroupsSQL = "select * from musclegroups where musclegroupsID = " + currentExerciseID;
            addTableStringArrayToJSONBasedOnBool(musclegroupsSQL, "muscleGroups", "muscleGroupsID");

            //Create media object
            String mediaSQL = "select * from media where mediaID = " + currentExerciseID;
            exerciseJSON.put("media", addTableObjectToJSON(mediaSQL, "mediaID"));

            //Create foreign data array,
            getForeignDataObjectArray("foreignDataID");

            //Create the label
            String name = dbResult.getString("name");

            //Skip the current exercise if flag was tripped somewhere
            if (skipExercise) {
                continue;
            }

            //Add the exercise to the data JSON
            returnJSON.put(createJSONLabel(name), exerciseJSON);
        }

        return returnJSON;
    }

    /**
     * Creates a label in camelcase for the JSON
     * @param rawName raw exercise name
     * @return name in camelCase
     */
    public static String createJSONLabel(String rawName) {

        //If the string is empty or null
        if (rawName == null || rawName.length() == 0) {
            return "null";
        }

        //Create an array of words
        String[] words = rawName.split(" ");

        //Create first word
        String label = words[0].toLowerCase();

        //Add on following words
        for (int i = 1; i < words.length; i++) {
            String nextWord = words[i].substring(0, 1).toUpperCase()
                    + words[i].substring(1).toLowerCase();
            label += nextWord;
        }

        return label;
    }

    /**
     * Adds DIRECT values to the JSON, not for objects
     * @param sql The sql string to be executed
     * @param columnToSkip skips the specific column
     * @throws SQLException
     * @throws JSONException
     */
    public static void addFullTableValuesToJSON(String sql, String columnToSkip) throws SQLException, JSONException {

        //Get the tables data
        ResultSet table = SQLDatabase.connectAndExecuteSQL(sql);
        ResultSetMetaData tableMeta = table.getMetaData();

        //Move to the row
        table.next();

        //Add each columns data to the table
        for (int i = 1; i <= tableMeta.getColumnCount(); i++) {

            //Check if the current column is one we skip
            if (tableMeta.getColumnLabel(i).equals(columnToSkip)) {
                continue;
            }

            //Based on the type of data, add it to the JSON
            int type = getDataType(tableMeta.getColumnTypeName(i));

            //Add based on the correct type
            switch (type) {
                case BOOL -> exerciseJSON.put(tableMeta.getColumnLabel(i), table.getBoolean(i));
                case INT -> exerciseJSON.put(tableMeta.getColumnLabel(i), table.getInt(i));
                case FLOAT -> exerciseJSON.put(tableMeta.getColumnLabel(i), table.getFloat(i));
                case STRING -> exerciseJSON.put(tableMeta.getColumnLabel(i), table.getString(i));
                case ARRAY -> exerciseJSON.put(tableMeta.getColumnLabel(i), table.getArray(i));
            }
        }
    }

    /**
     *
     * @param sql The sql string to be executed
     * @param tableLabel The label for the array in the JSON
     * @param columnToSkip skips the specific column
     * @throws SQLException
     * @throws JSONException
     */
    public static void addTableStringArrayToJSONBasedOnBool(String sql, String tableLabel, String columnToSkip) throws SQLException, JSONException {

        //Get the tables data
        ResultSet table = SQLDatabase.connectAndExecuteSQL(sql);
        ResultSetMetaData tableMeta = table.getMetaData();

        //Create a blank object to be added
        JSONObject temp = new JSONObject();

        //Move to the row
        table.next();

        ArrayList<String> tempArray = new ArrayList();

        //Add each columns data to the table
        for (int i = 1; i <= tableMeta.getColumnCount(); i++) {

            //Check if the current column is one we skip
            if (tableMeta.getColumnLabel(i).equals(columnToSkip)) {
                continue;
            }

            //Check that we are working with a bool
            if (getDataType(tableMeta.getColumnTypeName(i)) != BOOL) {
                continue;
            }

            //If the column is true then add it
            if (table.getBoolean(i)) {
                tempArray.add(tableMeta.getColumnLabel(i));
            }
        }

        //Add temp JSON to exercise JSON
        exerciseJSON.put(tableLabel, tempArray.toArray());
    }

    /**
     * Returns that table as a JSON
     * @param sql The sql string to be executed
     * @param columnToSkip skips the specific column
     * @return a JSON object of a given table
     * @throws SQLException
     * @throws JSONException
     */
    public static JSONObject addTableObjectToJSON(String sql, String columnToSkip) throws SQLException, JSONException {

        //Get the tables row data
        ResultSet row = SQLDatabase.connectAndExecuteSQL(sql);
        ResultSetMetaData rowMeta = row.getMetaData();

        //Create a blank object to be added
        JSONObject temp = new JSONObject();
        
        //Move to the row
        row.next();

        //Add each columns data to the row
        for (int i = 1; i <= rowMeta.getColumnCount(); i++) {

            //Check if the current column is one we skip
            if (rowMeta.getColumnLabel(i).equals(columnToSkip)) {
                continue;
            }

            //Based on the type of data, add it to the JSON
            int type = getDataType(rowMeta.getColumnTypeName(i));

            //Add based on the correct type
            switch (type) {
                case BOOL -> temp.put(rowMeta.getColumnLabel(i), row.getBoolean(i));
                case INT -> temp.put(rowMeta.getColumnLabel(i), row.getInt(i));
                case FLOAT -> temp.put(rowMeta.getColumnLabel(i), row.getFloat(i));
                case STRING -> temp.put(rowMeta.getColumnLabel(i), row.getString(i));
                case ARRAY -> temp.put(rowMeta.getColumnLabel(i), row.getArray(i));
            }
        }

        //Add temp JSON to exercise JSON
        return temp;
    }

    /**
     * Specific to the foreign data entered to each exercise.
     * @param columnToSkip skips the specific column
     * @throws SQLException
     * @throws JSONException
     */
    public static void getForeignDataObjectArray(String columnToSkip) throws SQLException, JSONException {

        //Create an array to store Language Data JSONObjects
        ArrayList<JSONObject> languageObjectArray = new ArrayList<>();

        //Find amount of language tables
        String foreignDataSQL = "select * from ForeignData";
        ResultSet table = SQLDatabase.connectAndExecuteSQL(foreignDataSQL);
        ResultSetMetaData tableMeta = table.getMetaData();

        //Move to the row
        table.next();

        //Create an ArrayList to store the table names for sql
        ArrayList<String> languageTableNamesArray = new ArrayList<>();

        //Add the names to the ArrayList
        for (int i = 1; i <= tableMeta.getColumnCount(); i++) {

            //Check if the current column is one we skip
            if (tableMeta.getColumnLabel(i).equals(columnToSkip)) {
                continue;
            }

            //Loop through each language table name and add it to the ArrayList
            languageTableNamesArray.add(table.getString(i));
        }

        //Get data from each found language table
        for (int j = 0; j < languageTableNamesArray.size(); j++) {

            //Grab each table as an object
            String languageDataSQL = "select * from " + languageTableNamesArray.get(j) + " where exerciseID = " + currentExerciseID;
            languageObjectArray.add(addTableObjectToJSON(languageDataSQL, "exerciseID"));
        }

        //Put the whole array in the exercise JSON
        exerciseJSON.put("foreignData", languageObjectArray.toArray());
    }

    /**
     * Gets an SQL datatype and converts into an int
     * @param type the SQL datatype
     * @return the int that represents the datatype
     */
    public static int getDataType(String type) {
        if (type.equals("bit")) {
            return BOOL;
        } else if (type.equals("int")) {
            return INT;
        } else if (type.equals("float")) {
            return FLOAT;
        } else if (type.equals("varchar")) {
            return STRING;
        } else if (type.equals("array")) {
            return ARRAY;
        } else {
            return -1;
        }
    }

    /**
     * If the desired value is found skip the exercise
     * @param row the row to be searched
     * @param columnName the column name to check
     * @param boolToSkip the value to check in the column
     * @throws SQLException
     */
    public static void checkIfExerciseIsValid(ResultSet row, String columnName, boolean boolToSkip) throws SQLException {

        //Column number
        int selectedColumnInt = -1;

        //Check if the row given contains the column
        for (int i = 1; i <= row.getMetaData().getColumnCount(); i++) {

            //If the row is there, check that value later
            if (row.getMetaData().getColumnName(i).equals(columnName)) {
                selectedColumnInt = i;
                break;
            }

            //If the value is not found, exit out, no need to continue just assume the exercise is ok
            if (i == row.getMetaData().getColumnCount()) {
                return;
            }
        }

        if (selectedColumnInt != -1) {

            //Based on the type check the value
            ResultSetMetaData meta = row.getMetaData();
            int type = getDataType(meta.getColumnTypeName(selectedColumnInt));

            //Check that it is a boolean
            if (type == BOOL) {
                if (row.getBoolean(selectedColumnInt) == boolToSkip) {
                    skipExercise = true;
                }
            }
        }
    }

    /**
     * If the desired value is found skip the exercise
     * @param row the row to be searched
     * @param columnName the column name to check
     * @param intToSkip the value to check in the column
     * @throws SQLException
     */
    public static void checkIfExerciseIsValid(ResultSet row, String columnName, int intToSkip) throws SQLException {

        //Column number
        int selectedColumnInt = -1;

        //Check if the row given contains the column
        for (int i = 1; i <= row.getMetaData().getColumnCount(); i++) {

            //If the row is there, check that value later
            if (row.getMetaData().getColumnName(i).equals(columnName)) {
                selectedColumnInt = i;
                break;
            }

            //If the value is not found, exit out, no need to continue just assume the exercise is ok
            if (i == row.getMetaData().getColumnCount()) {
                return;
            }
        }

        if (selectedColumnInt != -1) {

            //Based on the type check the value
            ResultSetMetaData meta = row.getMetaData();
            int type = getDataType(meta.getColumnTypeName(selectedColumnInt));

            //Check that it is an int
            if (type == INT) {
                if (row.getInt(selectedColumnInt) == intToSkip) {
                    skipExercise = true;
                }
            }
        }
    }

    /**
     * If the desired value is found skip the exercise
     * @param row the row to be searched
     * @param columnName the column name to check
     * @param floatToSkip the value to check in the column
     * @throws SQLException
     */
    public static void checkIfExerciseIsValid(ResultSet row, String columnName, float floatToSkip) throws SQLException {

        //Column number
        int selectedColumnInt = -1;

        //Check if the row given contains the column
        for (int i = 1; i <= row.getMetaData().getColumnCount(); i++) {

            //If the row is there, check that value later
            if (row.getMetaData().getColumnName(i).equals(columnName)) {
                selectedColumnInt = i;
                break;
            }

            //If the value is not found, exit out, no need to continue just assume the exercise is ok
            if (i == row.getMetaData().getColumnCount()) {
                return;
            }
        }

        if (selectedColumnInt != -1) {

            //Based on the type check the value
            ResultSetMetaData meta = row.getMetaData();
            int type = getDataType(meta.getColumnTypeName(selectedColumnInt));

            //Check that it is a float
            if (type == FLOAT) {
                if (thresholdBasedFloatsComparison(row.getFloat(selectedColumnInt), floatToSkip)) {
                    skipExercise = true;
                }
            }
        }
    }

    /**
     * If the desired value is found skip the exercise
     * @param row the row to be searched
     * @param columnName the column name to check
     * @param stringToSkip the value to check in the column
     * @throws SQLException
     */
    public static void checkIfExerciseIsValid(ResultSet row, String columnName, String stringToSkip) throws SQLException {

        //Column number
        int selectedColumnInt = -1;

        //Check if the row given contains the column
        for (int i = 1; i <= row.getMetaData().getColumnCount(); i++) {

            //If the row is there, check that value later
            if (row.getMetaData().getColumnName(i).equals(columnName)) {
                selectedColumnInt = i;
                break;
            }

            //If the value is not found, exit out, no need to continue just assume the exercise is ok
            if (i == row.getMetaData().getColumnCount()) {
                return;
            }
        }

        if (selectedColumnInt != -1) {

            //Based on the type check the value
            ResultSetMetaData meta = row.getMetaData();
            int type = getDataType(meta.getColumnTypeName(selectedColumnInt));

            //Check that it is a string
            if (type == STRING) {
                if (row.getString(selectedColumnInt).equals(stringToSkip)) {
                    skipExercise = true;
                }
            }
        }
    }

    /**
     * If the desired value is found skip the exercise
     * @param row the row to be searched
     * @param columnName the column name to check
     * @param arrayToSkip the value to check in the column
     * @throws SQLException
     */
    public static void checkIfExerciseIsValid(ResultSet row, String columnName, Array arrayToSkip) throws SQLException {

        //Column number
        int selectedColumnInt = -1;

        //Check if the row given contains the column
        for (int i = 1; i <= row.getMetaData().getColumnCount(); i++) {

            //If the row is there, check that value later
            if (row.getMetaData().getColumnName(i).equals(columnName)) {
                selectedColumnInt = i;
                break;
            }

            //If the value is not found, exit out, no need to continue just assume the exercise is ok
            if (i == row.getMetaData().getColumnCount()) {
                return;
            }
        }

        if (selectedColumnInt != -1) {

            //Based on the type check the value
            ResultSetMetaData meta = row.getMetaData();
            int type = getDataType(meta.getColumnTypeName(selectedColumnInt));

            //Check that it is an array
            if (type == ARRAY) {
                if (row.getArray(selectedColumnInt).equals(arrayToSkip)) {
                    skipExercise = true;
                }
            }
        }
    }

    /**
     * Compares two floats
     * @author https://howtodoinjava.com/java-examples/correctly-compare-float-double/
     */
    private static boolean thresholdBasedFloatsComparison(float f1, float f2)
    {
        final double THRESHOLD = .0001;

        //Checks the float based on abs math
        if (Math.abs(f1 - f2) < THRESHOLD)
            return true;
        else
            return false;
    }
}
