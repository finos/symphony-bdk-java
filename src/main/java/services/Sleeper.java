package services;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@API(status = API.Status.INTERNAL)
class Sleeper {

    private static final Logger logger = LoggerFactory.getLogger(Sleeper.class);

    public void sleep(int timeInSecs) {
        try {
            TimeUnit.SECONDS.sleep(timeInSecs);
        } catch (InterruptedException e) {
            logger.error("Thread sleep has failed.", e);
        }
    }
}
