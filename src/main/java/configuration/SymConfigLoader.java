package configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.NoConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SymConfigLoader {
    private final static Logger logger = LoggerFactory.getLogger(SymConfigLoader.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Load configuration from a file path
     *
     * @param path Path of the config file
     * @return Symphony Configuration
     */
    public static SymConfig loadFromFile(String path) {
        return loadFromFile(path, SymConfig.class);
    }

    /**
     * Load configuration from an input stream
     *
     * @param inputStream Input stream
     * @return Symphony Configuration
     */
    public static SymConfig load(InputStream inputStream) {
        return load(inputStream, SymConfig.class);
    }

    /**
     * Load load balancer configuration from a file path
     *
     * @param path Path of the config file
     * @return Load Balancer Configuration
     */
    public static SymLoadBalancedConfig loadLoadBalancerFromFile(String path) {
        return loadFromFile(path, SymLoadBalancedConfig.class);
    }

    /**
     * Load load balancer configuration from an input stream
     *
     * @param inputStream Input stream
     * @return Load Balancer Configuration
     */
    public static SymLoadBalancedConfig loadLoadBalancer(InputStream inputStream) {
        return load(inputStream, SymLoadBalancedConfig.class);
    }

    /**
     * Load custom configuration from a file path
     *
     * @param path Path of the config file
     * @param <T> Class type of config
     * @param clazz Class type of config
     * @return Custom Configuration
     */
    public static <T extends SymConfig> T loadFromFile(String path, Class<T> clazz) {
        try {
            return mapper.readValue(new File(path), clazz);
        } catch (IOException e) {
            logger.error("Unable to load config file: " + path, e);
            return null;
        }
    }

    /**
     * Load custom configuration from an input stream
     *
     * @param inputStream Input stream
     * @param <T> Class type of config
     * @param clazz Class type of config
     * @return Custom Configuration
     */
    public static <T extends SymConfig> T load(InputStream inputStream, Class<T> clazz) {
        try {
            return mapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            logger.error("Unable to load config", e);
            return null;
        }
    }

    /**
     * Automatically load standard configuration from either file or classpath
     *
     * @param configPath path to config file
     * @return Configuration
     */
    public static SymConfig loadConfig(String configPath) {
        return loadConfig(configPath, SymConfig.class);
    }

    /**
     * Automatically load load balancer configuration from either file or classpath
     *
     * @param configPath path to config file
     * @return Configuration
     */
    public static SymLoadBalancedConfig loadLoadBalancerConfig(String configPath) {
        return loadConfig(configPath, SymLoadBalancedConfig.class);
    }

    /**
     * Automatically load custom configuration from either file or classpath
     *
     * @param configPath path to config file
     * @param <T>        Class type of config
     * @param clazz      Class type of config
     * @return Custom configuration
     */
    public static <T extends SymConfig> T loadConfig(String configPath, Class<T> clazz)
        throws NoConfigException {
        if (!configPath.startsWith(File.separator)) {
            configPath = File.separator + configPath;
        }

        T config;
        String externalUrlPath = System.getProperty("user.dir") + configPath;
        if ((new File(externalUrlPath)).exists())
            config = loadFromFile(externalUrlPath, clazz);
        else
            config = load(SymConfigLoader.class.getResourceAsStream(configPath), clazz);

        if (config == null) {
            throw new NoConfigException(String.format("Unable to load config from: %s", configPath));
        }
        return config;
    }
}
