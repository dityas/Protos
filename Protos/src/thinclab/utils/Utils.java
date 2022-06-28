
package thinclab.utils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {

    private static final Logger LOGGER = LogManager.getFormatterLogger(Utils.class);

    // Write JSON object to file
    public static void writeJsonToFile(JsonElement json, String fileName) {

        try {
            var writer = new FileWriter(new File(fileName));

            var gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(json));
            writer.flush();

            writer.close();

            LOGGER.info("Wrote %s to file %s", json, fileName);
        }

        catch (Exception e) {

            LOGGER.error("Error while opening file %s", fileName);
            System.exit(-1);
        }
    }

    // Read JSON object from file
    public static JsonElement readJsonFromFile(String fileName) {

        try {
            var path = Paths.get(fileName); 
            var data = Files.readString(path);

            return JsonParser.parseString(data);
        }

        catch (Exception e) {

            LOGGER.error("While reading JSON from %s, %s", fileName, e);
            System.exit(-1);
            return null;
        }
    }
}
