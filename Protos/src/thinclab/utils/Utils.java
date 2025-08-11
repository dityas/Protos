
package thinclab.utils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.Global;
import thinclab.models.datastructures.PolicyGraph;

public class Utils {

    private static final Logger LOGGER = LogManager.getFormatterLogger(Utils.class);

    // Zip two lists
    public static List<Tuple<?, ?>> zip(List<?> a, List<?> b) {
        return null;
    }

    // Write JSON object to file
    public static void writeJsonToFile(JsonElement json, String fileName) {

        try {
            var writer = new FileWriter(new File(fileName));

            var gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(json));
            writer.flush();

            writer.close();

            LOGGER.info("Wrote JSON to file %s", fileName);
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

    // Write policy graph to file
    public static void serializePolicyGraph(PolicyGraph G, String modelName) {
        if (Global.RESULTS_DIR != null) {
            try {

                var fileName =
                    Paths.get(
                            String.format("%s/%s_%s_pol_graph.json",
                                Global.RESULTS_DIR
                                .toAbsolutePath().toString(),
                                G.hashCode(), modelName)); 
                Files.writeString(fileName, G.toString());

                LOGGER.info("Wrote policy graph to %s", fileName);
            }

            catch (Exception e) {
                LOGGER.error("Got error while writing policy graph to file: %s", e);
            }
        }

        else
            LOGGER.error("Global.RESULTS_DIR not defined! Graph will not be stored");
    }
}
