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
     * flags over which the various thread communicate.
     * e.g. when data storage is full, systems like the database will fail
     */
    boolean isDataStorageFullFlag = false;
    int dataStorageWarningIntervalInSeconds = 30;

    boolean isDatabaseWorking = true;
    int databaseErrorIntervalInSeconds = 30;

    boolean isOrderServiceImpacted = false;
    int orderServiceWarningIntervalInSeconds = 5;
    int orderServiceMonitoringIntervalInSeconds = 5;

    boolean isUserDataServiceImpacted = false;
    int userDataServiceWarningIntervalInSeconds = 5;
    int userDataServiceMonitoringIntervalInSeconds = 5;


    @Override
    public void perform() {
        new Thread(new DataStorageAlerter()).start();
        new Thread(new DatabaseAlerter()).start();
        new Thread(new OrderServiceAlerter()).start();
        new Thread(new OrderServicePerformanceAlerter()).start();
        new Thread(new UserDataServiceAlerter()).start();
        new Thread(new UserDataServicePerformanceAlerter()).start();
    }

    class UserDataServicePerformanceAlerter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (isUserDataServiceImpacted) {
                        Event event = Event.getUserDataServicePerformanceImpactedWarning();
                        logger.info(StringUtils.join("UserDataServicePerformanceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                        logger.info(StringUtils.join("UserDataServicePerformanceAlerter going to sleep for ",
                                userDataServiceWarningIntervalInSeconds, "s."));
                    }
                    Thread.sleep(userDataServiceWarningIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("UserDataServicePerformanceAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... UserDataServicePerformanceAlerter will continue trying to send events anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
    }

    class UserDataServiceAlerter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    isUserDataServiceImpacted = !isDatabaseWorking;
                    if (isUserDataServiceImpacted) {
                        logger.info(StringUtils.join("********************* UserDataServiceAlerter if branch, isUserDataServiceImpacted=",
                                isUserDataServiceImpacted));

                        Event event = Event.getUserDataServiceError();
                        logger.info(StringUtils.join("UserDataServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                        logger.info(StringUtils.join("UserDataServiceAlerter going to sleep for ",
                                userDataServiceMonitoringIntervalInSeconds, "s."));
                    } else {
                        logger.info(StringUtils.join("********************* UserDataServiceAlerter else branch, isUserDataServiceImpacted=",
                                isUserDataServiceImpacted));
                    }
                    Thread.sleep(userDataServiceMonitoringIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("UserDataServiceAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... UserDataServiceAlerter will continue trying to send events anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
    }

    class OrderServicePerformanceAlerter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (isOrderServiceImpacted) {
                        Event event = Event.getOrderServicePerformanceImpactedWarning();
                        logger.info(StringUtils.join("OrderServicePerformanceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                        logger.info(StringUtils.join("OrderServicePerformanceAlerter going to sleep for ",
                                orderServiceWarningIntervalInSeconds, "s."));
                    }
                    Thread.sleep(orderServiceWarningIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("OrderServicePerformanceAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... OrderServiceAlerter will continue trying to send events anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
    }

    class OrderServiceAlerter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    isOrderServiceImpacted = !isDatabaseWorking;
                    if (isOrderServiceImpacted) {
                        logger.info(StringUtils.join("********************* OrderServiceAlerter if branch, isOrderServiceImpacted=",
                                isOrderServiceImpacted));

                        Event event = Event.getOrderServiceError();
                        logger.info(StringUtils.join("OrderServicePerformanceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                        logger.info(StringUtils.join("OrderServicePerformanceAlerter going to sleep for ",
                                orderServiceMonitoringIntervalInSeconds, "s."));
                    } else {
                        logger.info(StringUtils.join("********************* OrderServiceAlerter else branch, isOrderServiceImpacted=",
                                isOrderServiceImpacted));
                    }
                    Thread.sleep(orderServiceMonitoringIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("OrderServicePerformanceAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... OrderServiceAlerter will continue trying to send events anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
    }

    class DataStorageAlerter implements Runnable {
        @Override
        public void run() {
            int repetitionCounter = 0;
            int currentCapacity = 72;
            // TODO maybe add some logic to suppress repeated green messages
            // TODO what is the best practice: sending OK events repeatedly or just once.
            while (true) {
                try {
                    currentCapacity = Math.min(currentCapacity + 1, 100);
                    Event event = currentCapacity < 75 ?
                            Event.getStorageOkEvent(currentCapacity) : Event.getStorageWarning(currentCapacity);
                    logger.info(StringUtils.join("DataStorageAlerter going to send event to moogsoft: ",
                            event.toString()));
                    dataUplink.sendEvent(event);
                    if (currentCapacity > 99) {
                        isDataStorageFullFlag = true;
                        repetitionCounter++;
                    }
                    if (repetitionCounter >= 25) {
                        currentCapacity = 60; // at this point, the admin adds storage (magically)
                        isDataStorageFullFlag = false;
                        repetitionCounter = 0;
                    }
                    logger.info(StringUtils.join("DataStorageAlerter going to sleep for ",
                            dataStorageWarningIntervalInSeconds * 1000, "s."));
                    Thread.sleep(dataStorageWarningIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("DataStorageAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... DataStorageAlter will continue trying to send events anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
    }

    class DatabaseAlerter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    isDatabaseWorking = !isDataStorageFullFlag;
                    Event event = isDatabaseWorking ? Event.getDatabaseIsWorkingEvent() : Event.getDatabaseIsDownError();
                    logger.info(StringUtils.join("DatabaseAlerter going to send event to moogsoft: ",
                            event.toString()));
                    dataUplink.sendEvent(event);
                    Thread.sleep(databaseErrorIntervalInSeconds * 1000);

                } catch (AuthenticationException | InterruptedException | IOException e) {
                    String errMsg = StringUtils.join("DatabaseAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... DatabaseAlerter will continue trying to send events anyway.");
                }
            }
        }
    }

    @Override
    public void setDataUplink(DataUplink dataUplink) {
        this.dataUplink = dataUplink;
    }
}
