package configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class SymConfigLoader {
    private final Logger logger = LoggerFactory.getLogger(SymConfigLoader.class);

    public SymConfig loadFromFile(String path){
        ObjectMapper mapper = new ObjectMapper();
        SymConfig config = null;
        try {
           config = mapper.readValue(new File(path), SymConfig.class);
            return config;
        } catch (IOException e) {
            logger.error("Error reading json config file", e);
        }
        return config;
    }
}
