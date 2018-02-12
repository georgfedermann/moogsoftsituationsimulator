package org.poormanscastle.moogsoft;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class SimpleDatastoreOutagePlaybook implements PlayBook {

    private final static Logger logger = Logger.getLogger(SimpleDatastoreOutagePlaybook.class);

    DataUplink dataUplink;

    SimpleDatastoreOutagePlaybook() {
        dataUplink = new DataUplink();
    }

    /*
     * a flag over which the various thread communicate.
     * e.g. when data storage is full, systems like the database will fail
     */
    boolean isDataStorageFullFlag = false;
    int dataStorageWarningIntervalInSeconds = 10;

    @Override
    public void perform() {
    }

    class DataStorageAlerter implements Runnable {
        @Override
        public void run() {
            int currentCapacity = 75;
            while (true) {
                try {
                    dataUplink.sendEvent(Event.getStorageWarning(currentCapacity++));
                    if (currentCapacity > 99) {
                        isDataStorageFullFlag = true;
                    }
                    Thread.sleep(dataStorageWarningIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("DataStorageAlerter run into exception: ", e.getClass().getName()
                            , " - ", e.getMessage(), " ... DataStorageAlter will continue trying to send alerts anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
    }

}
