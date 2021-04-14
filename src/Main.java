import org.json.JSONException;
import org.json.JSONObject;
import java.sql.ResultSet;
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
    final static String VERSION_CODE  = "0.0.1";

    /**
     * Drives program.
     * @param args unused.
     * @throws JSONException
     */
    public static void main(String[] args) throws JSONException, SQLException {

        String getFullTable =
                "select * from Exercises as e \n" +
                "join MuscleGroups as mg on e.exerciseID = mg.muscleGroupsID\n" +
                "join Locations as l on e.exerciseID = l.locationsID\n" +
                "join Tracking as t on e.exerciseID = t.trackingID\n" +
                "join ForeignData as fd on fd.foreignDataID = e.exerciseID\n" +
                "join SpanishData as sd on fd.foreignDataID = sd.spanishID\n" +
                "join FrenchData as frd on fd.foreignDataID = frd.frenchID\n" +
                "join Media as md on md.mediaID = e.exerciseID\n" +
                "order by e.exerciseID asc";

        ResultSet result = SQLDatabase.connectAndExecuteSQL(getFullTable);

        //Create the full JSON Object to be returned
        JSONObject fullJSON = new JSONObject();

        //Add meta data to the top
        fullJSON.put("meta", getMetaJSON());

        //Add full exerciseData below meta data
        fullJSON.put("exerciseData", getDataJSON(result));

        System.out.println(fullJSON.toString(3));

        //Write the JSON to memory
        //saveFullJSONToFile(fullJSON);

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

        //Create exercise JSON to hold each exercise
        JSONObject exerciseJSON;

        //Loop through results
        while (dbResult.next()) {

            exerciseJSON = new JSONObject();

            //Create the label
            String name = dbResult.getString("name");

            //Add the immediate variables
            exerciseJSON.put("exerciseID", dbResult.getInt("exerciseID"));
            exerciseJSON.put("name", name);
            exerciseJSON.put("description", dbResult.getString("description"));
            exerciseJSON.put("difficulty", dbResult.getFloat("difficulty"));

            //Create locations object
            JSONObject locationsObject = new JSONObject();
            locationsObject.put("boolAtGym", dbResult.getBoolean("boolAtGym"));
            locationsObject.put("boolAtHome", dbResult.getBoolean("boolAtHome"));
            locationsObject.put("boolOutside", dbResult.getBoolean("boolOutside"));
            exerciseJSON.put("locations", locationsObject);

            //Create tracking object
            JSONObject trackingObject = new JSONObject();
            trackingObject.put("boolRepsSets", dbResult.getBoolean("boolRepsSets"));
            trackingObject.put("boolBodyWeight", dbResult.getBoolean("boolBodyWeight"));
            trackingObject.put("boolWeights", dbResult.getBoolean("boolWeights"));
            trackingObject.put("boolTimer", dbResult.getBoolean("boolTimer"));
            trackingObject.put("boolStopwatch", dbResult.getBoolean("boolStopwatch"));
            trackingObject.put("boolDistance", dbResult.getBoolean("boolDistance"));
            exerciseJSON.put("tracking", trackingObject);

            //Create muscle groups array
            ArrayList<String> muscleGroupsArray = new ArrayList<>();

            if (dbResult.getBoolean("pectorals"))
                muscleGroupsArray.add("pectorals");
            if (dbResult.getBoolean("triceps"))
                muscleGroupsArray.add("triceps");
            if (dbResult.getBoolean("deltoids"))
                muscleGroupsArray.add("deltoids");
            if (dbResult.getBoolean("quadriceps"))
                muscleGroupsArray.add("quadriceps");
            if (dbResult.getBoolean("hamstrings"))
                muscleGroupsArray.add("hamstrings");
            if (dbResult.getBoolean("lats"))
                muscleGroupsArray.add("lats");
            if (dbResult.getBoolean("traps"))
                muscleGroupsArray.add("traps");

            exerciseJSON.put("muscleGroups", muscleGroupsArray.toArray());

            //Create media object
            JSONObject mediaObject = new JSONObject();
            mediaObject.put("youtube", dbResult.getString("youtube"));
            exerciseJSON.put("media", mediaObject);

            //Create foreign data array
            //TODO: Parse data for foreign data

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

}
