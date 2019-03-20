package configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SymConfigLoader {
    private final static Logger logger = LoggerFactory.getLogger(SymConfigLoader.class);

    /**
     * This is actually an utility class which is not required to be initialized
     * @param path Path of the config file
     * @return Symphony Configuration
     */
    public static SymConfig loadFromFile(String path){
        ObjectMapper mapper = new ObjectMapper();
        SymConfig config = null;
        try {
            config = mapper.readValue(new File(path), SymConfig.class);
        } catch (IOException e) {
            logger.error("Unable to load config file: " + path, e);
        }
        return config;
    }

    /**
     * This is actually an utility class which is not required to be initialized
     * @param inputStream Input stream
     * @return Symphony Configuration
     */
    public static SymConfig load(InputStream inputStream) {
        ObjectMapper mapper = new ObjectMapper();
        SymConfig config = null;
        try {
            config = mapper.readValue(inputStream, SymConfig.class);
        } catch (IOException e) {
            logger.error("Unable to load config", e);
        }
        return config;
    }
}
