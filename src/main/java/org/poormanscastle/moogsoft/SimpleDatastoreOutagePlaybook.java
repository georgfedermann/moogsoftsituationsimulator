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
    int orderServiceApmIntervalInSeconds = 5;
    int orderServiceLogMonitoringIntervalInSeconds = 5;

    boolean isUserDataServiceImpacted = false;
    int userDataServiceApmIntervalInSeconds = 5;
    int userDataServiceLogMonitoringIntervalInSeconds = 5;

    boolean isSkuServiceImpacted = false;
    int skuServiceApmIntervalInSeconds = 5;
    int skuServiceLogMonitoringIntervalInSeconds = 5;


    @Override
    public void perform() {
        new Thread(new DataStorageAlerter()).start();
        new Thread(new DatabaseAlerter()).start();
        new Thread(new OrderServiceAlerter()).start();
        new Thread(new OrderServicePerformanceAlerter()).start();
        new Thread(new UserDataServiceAlerter()).start();
        new Thread(new UserDataServicePerformanceAlerter()).start();
    }

    class SkuServiceAlerter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // check if service just switched back to working again
                    if (isSkuServiceImpacted && isDatabaseWorking) {
                        // database just got operative again. System reports a recovery event to moogsoft
                        Event event = Event.getSkuServiceBackToNormal();
                        logger.info(StringUtils.join("SkuServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    }

                    isSkuServiceImpacted = !isDatabaseWorking;
                    if (isSkuServiceImpacted) {
                        logger.debug(StringUtils.join("********************* SkuServiceAlerter if branch, isSkuServiceImpacted=",
                                isSkuServiceImpacted));

                        Event event = Event.getSkuServiceError();
                        logger.info(StringUtils.join("skuServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    } else {
                        logger.debug(StringUtils.join("********************* UserDataServiceAlerter else branch, isUserDataServiceImpacted=",
                                isUserDataServiceImpacted));
                    }
                    logger.debug(StringUtils.join("SkuServiceAlerter going to sleep for ",
                            skuServiceLogMonitoringIntervalInSeconds, "s."));
                    Thread.sleep(skuServiceLogMonitoringIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("UserDataServiceAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... UserDataServiceAlerter will continue trying to send events anyway.");
                    logger.warn(errMsg, e);
                }
            }
        }
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
                    }
                    logger.debug(StringUtils.join("UserDataServicePerformanceAlerter going to sleep for ",
                            userDataServiceApmIntervalInSeconds, "s."));
                    Thread.sleep(userDataServiceApmIntervalInSeconds * 1000);
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
                    // check if service just switched back to working again
                    if (isUserDataServiceImpacted && isDatabaseWorking) {
                        // database just got operative again. System reports a recovery event to moogsoft
                        Event event = Event.getUserDataServiceBackToNormal();
                        logger.info(StringUtils.join("UserDataServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    }

                    isUserDataServiceImpacted = !isDatabaseWorking;
                    if (isUserDataServiceImpacted) {
                        logger.debug(StringUtils.join("********************* UserDataServiceAlerter if branch, isUserDataServiceImpacted=",
                                isUserDataServiceImpacted));

                        Event event = Event.getUserDataServiceError();
                        logger.info(StringUtils.join("UserDataServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    } else {
                        logger.debug(StringUtils.join("********************* UserDataServiceAlerter else branch, isUserDataServiceImpacted=",
                                isUserDataServiceImpacted));
                    }
                    logger.debug(StringUtils.join("UserDataServiceAlerter going to sleep for ",
                            userDataServiceLogMonitoringIntervalInSeconds, "s."));
                    Thread.sleep(userDataServiceLogMonitoringIntervalInSeconds * 1000);
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
                    }
                    logger.info(StringUtils.join("OrderServicePerformanceAlerter going to sleep for ",
                            orderServiceApmIntervalInSeconds, "s."));
                    Thread.sleep(orderServiceApmIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("OrderServicePerformanceAlerter run into exception: ",
                            e.getClass().getName(), " - ", e.getMessage(),
                            " ... OrderServicePerformanceAlerter will continue trying to send events anyway.");
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
                    // check if service just switched back to working again
                    if (isOrderServiceImpacted && isDatabaseWorking) {
                        // database just got operative again. System reports a recovery event to moogsoft
                        Event event = Event.getOrderServiceBackToNormalEvent();
                        logger.info(StringUtils.join("OrderServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    }
                    isOrderServiceImpacted = !isDatabaseWorking;
                    if (isOrderServiceImpacted) {
                        logger.debug(StringUtils.join("********************* OrderServiceAlerter if branch, isOrderServiceImpacted=",
                                isOrderServiceImpacted));

                        Event event = Event.getOrderServiceError();
                        logger.info(StringUtils.join("OrderServiceAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    } else {
                        logger.debug(StringUtils.join("********************* OrderServiceAlerter else branch, isOrderServiceImpacted=",
                                isOrderServiceImpacted));
                    }
                    logger.debug(StringUtils.join("OrderServiceAlerter going to sleep for ",
                            orderServiceLogMonitoringIntervalInSeconds, "s."));
                    Thread.sleep(orderServiceLogMonitoringIntervalInSeconds * 1000);
                } catch (AuthenticationException | IOException | InterruptedException e) {
                    String errMsg = StringUtils.join("OrderServiceAlerter run into exception: ",
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
                        // inform event correlation system that the root cause has been resolved
                        Event resolvedEvent = Event.getStorageResolvedEvent(currentCapacity);
                        logger.info(StringUtils.join("DataStorageAlerter going to send event to moogsoft: ",
                                event.toString()));
                        dataUplink.sendEvent(event);
                    }
                    logger.info(StringUtils.join("DataStorageAlerter going to sleep for ",
                            dataStorageWarningIntervalInSeconds, "s."));
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
                    logger.info(StringUtils.join("DatabaseAlerter going to sleep for ",
                            databaseErrorIntervalInSeconds, "s."));
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
