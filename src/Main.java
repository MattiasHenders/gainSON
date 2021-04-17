import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Drives the application.
 */
public class Main {

    //==========================================================================
    //  CHANGE LOG
    //  1) Increment VERSION CODE
    //  2) Add date and brief note on addition to the version
    //==========================================================================
    //
    //final static String VERSION_CODE  = "0.0.1"; 20210413 First log, connects to database and gets a query. start of JSON building
    //final static String VERSION_CODE  = "0.0.2"; 20210415 Starting dynamic grabbing of column data, need to separate into 3 functions
    //final static String VERSION_CODE  = "0.0.3"; 20210416 Dynamic loading works, saves file to path. Need to work on foreign data next
    final static String VERSION_CODE  = "0.0.3";

    public static final String getFullTable =
            "select * from Exercises as e \n" +
                    "join MuscleGroups as mg on e.exerciseID = mg.muscleGroupsID\n" +
                    "join Locations as l on e.exerciseID = l.locationsID\n" +
                    "join Tracking as t on e.exerciseID = t.trackingID\n" +
                    "join ForeignData as fd on fd.foreignDataID = e.exerciseID\n" +
                    "join SpanishData as sd on fd.foreignDataID = sd.spanishID\n" +
                    "join FrenchData as frd on fd.foreignDataID = frd.frenchID\n" +
                    "join Media as md on md.mediaID = e.exerciseID\n" +
                    "order by e.exerciseID asc";

    public static JSONObject exerciseJSON;

    public static int currentExerciseID = -1;

    /** SQL Data Types */
    public static final int BOOL = 0;
    public static final int INT = 1;
    public static final int FLOAT = 2;
    public static final int STRING = 3;
    public static final int ARRAY = 4;

    /**
     * Drives program.
     * @param args unused.
     * @throws JSONException
     */
    public static void main(String[] args) throws JSONException, SQLException, IOException {

        //Get the FULL table
        ResultSet result = SQLDatabase.connectAndExecuteSQL(getFullTable);

        //Create the full JSON Object to be returned
        JSONObject fullJSON = new JSONObject();

        //Add meta data to the top
        fullJSON.put("meta", getMetaJSON());

        //Add full exerciseData below meta data
        fullJSON.put("exerciseData", getDataJSON(result));

        System.out.println(fullJSON.toString(3));

        //Write the JSON to memory
        String filePath = "C:\\Users\\hende\\Desktop";
        String fileName = "testJSON";
        saveFullJSONToFile(fullJSON, filePath, fileName);

    }

    public static void saveFullJSONToFile(JSONObject jsonRaw, String filePath, String fileName) throws IOException, JSONException {

        String toWrite = jsonRaw.toString(2);

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

            //Reset the exercise object
            exerciseJSON = new JSONObject();

            //Get the currentExerciseID, used later
            currentExerciseID = dbResult.getInt("exerciseID");

            //Add the immediate variables from the exercise table
            String exerciseSQL = "select * from Exercises where exerciseID = " + currentExerciseID;
            addFullTableValuesToJSON(exerciseSQL);

            //Create locations object
            String locationsSQL = "select * from Locations where locationsID = " + currentExerciseID;
            addTableObjectToJSON(locationsSQL, "locations", "locationsID");

            //Create tracking object
            String trackingSQL = "select * from tracking where trackingID = " + currentExerciseID;
            addTableObjectToJSON(trackingSQL, "tracking", "trackingID");

            //Create muscle groups array
            String musclegroupsSQL = "select * from musclegroups where musclegroupsID = " + currentExerciseID;
            addTableStringArrayToJSONBasedOnBool(musclegroupsSQL, "muscleGroups", "muscleGroupsID");

            //Create media object
            String mediaSQL = "select * from media where mediaID = " + currentExerciseID;
            addTableObjectToJSON(mediaSQL, "media", "mediaID");

            //Create foreign data array
            //TODO: Parse data for foreign data

            //Create the label
            String name = dbResult.getString("name");

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
     * @throws SQLException
     * @throws JSONException
     */
    public static void addFullTableValuesToJSON(String sql) throws SQLException, JSONException {

        //Get the tables data
        ResultSet table = SQLDatabase.connectAndExecuteSQL(sql);
        ResultSetMetaData tableMeta = table.getMetaData();

        //Move to the row
        table.next();

        //Add each columns data to the table
        for (int i = 1; i <= tableMeta.getColumnCount(); i++) {

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
     * Adds DIRECT values to the JSON, not for objects
     * @param sql The sql string to be executed
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
     * Adds DIRECT values to the JSON, not for objects
     * @param sql The sql string to be executed
     * @throws SQLException
     * @throws JSONException
     */
    public static void addTableObjectToJSON(String sql, String tableLabel, String columnToSkip) throws SQLException, JSONException {

        //Get the tables data
        ResultSet table = SQLDatabase.connectAndExecuteSQL(sql);
        ResultSetMetaData tableMeta = table.getMetaData();

        //Create a blank object to be added
        JSONObject temp = new JSONObject();
        
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
                case BOOL -> temp.put(tableMeta.getColumnLabel(i), table.getBoolean(i));
                case INT -> temp.put(tableMeta.getColumnLabel(i), table.getInt(i));
                case FLOAT -> temp.put(tableMeta.getColumnLabel(i), table.getFloat(i));
                case STRING -> temp.put(tableMeta.getColumnLabel(i), table.getString(i));
                case ARRAY -> temp.put(tableMeta.getColumnLabel(i), table.getArray(i));
            }
        }

        //Add temp JSON to exercise JSON
        exerciseJSON.put(tableLabel, temp);
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
}
