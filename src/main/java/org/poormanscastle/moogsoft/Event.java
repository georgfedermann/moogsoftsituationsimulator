package org.poormanscastle.moogsoft;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * An abstraction of an event that will be send to MoogSoft to inform the
 * event correlation system of events that occurred in the system.
 * The field are engineered close to the use documentation document as can be found
 * in the MoogSoft user interface in the Webhook integration section.
 * The first line of each field's javadoc is quoted directly from the Moogsoft
 * documentation. The rest of the comment is what we make out of the Moogsoft documentation.
 */
public class Event {

    /**
     * signature	String	Used to identify the event. Usually source:class:type.
     * usually a combination of the values of source, class and type,
     * separated by a colon ":".
     */
    // String signature = "my_test_box:application_server:server";  // used to identify the id, fqdn:network/hardware/app:storage/latency
    /**
     * source	String	Hostname or FQDN of the source machine that generated the event.
     * we go for:
     * ServiceHost.example.org, StorageHost.example.org, NAS_Host.example.org, FrontendHost
     */
    String source = "Kevins Macbook";   //
    /**
     * source_id	String	Unique identifier for the source machine.
     * we go for configuration management database (CMDB) ID here.
     */
    String source_id = "198.51.200.3";  // CMDB_1, CMDB_2, CMDB_3, CMDB_4
    /**
     * external_id	String	Unique identifier for the event source.
     * again: no idea. we go for "dummy"
     */
    String external_id = "id-1234";
    /**
     * agent_location	String	Geographical location of the agent that created the event.
     * we go for a data center in Frankfurt, floor 2, cage 25:
     * DC-Fra-02-25
     */
    String agent_location = "London";   // DataCenter, Floor, Cage, Fra-02-25
    /**
     * severity	Integer	Severity level of the event from 0-5 (clear - critical).
     * A scala of severity to help moogsoft appraise the weight of the reported event
     * 0 .. Clear
     * 1 .. Indeterminate
     * 2 .. Warning
     * 3 .. Minor
     * 4 .. Major
     * 5 .. Critical
     */
    Integer severity = 1;
    /**
     * type	String	Level of classification for the event. Follows hierarchy class then type.
     * we go for the following values:
     * application:performance, application:network, server:storage/network/application, networkdevice:router
     */
    String type = "Server";
    /**
     * manager	String	General identifier of the event generator or intermediary.
     * again: no idea. We go for "Testkoch", somebody who is responsible for the given box.
     */
    String manager = "Testkoch";
    /**
     * class	String	Level of classification for the event. Follows hierarchy class then type.
     * we go for: application, database, infrastructure, storage
     */
    String eventClass = "server";
    /**
     * description	String	Text description of the event.
     * A descriptive value that probably helps moogsoft to provide context for the event
     * and connect it with other events, probably by looking for key words like server names,
     * application names, stuff like that.
     */
    String description = "My MacPro is going nuts";
    /**
     * agent_time	String	Timestamp of when the event occurred in Unix epoch time.
     * The actual time when the event was raised.
     */
    Long agent_time = new Date().getTime() / 1000;

    public String getJson() {
        return "{" +
                "'signature':'" + getSignature() + "'," +
                "'source_id':'" + getSourceId() + "'," +
                "'external_id':'" + getExternalId() + "'," +
                "'manager':'" + getManager() + "'," +
                "'source':'" + getSource() + "'," +
                "'class':'" + getEventClass() + "'," +
                "'agent_location':'" + getAgentLocation() + "'," +
                "'type':'" + getType() + "'," +
                "'severity':" + getSeverity() + "," +
                "'description':'" + getDescription() + "'," +
                "'agent_time':'" + getAgentLocation() + "'" +
                "}";
    }

    private Event() {
    }

    @Override
    public String toString() {
        return "Event{" +
                "source='" + source + '\'' +
                ", source_id='" + source_id + '\'' +
                ", external_id='" + external_id + '\'' +
                ", agent_location='" + agent_location + '\'' +
                ", severity=" + severity +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", manager='" + manager + '\'' +
                ", eventClass='" + eventClass + '\'' +
                ", agent_time=" + agent_time +
                '}';
    }

    /**
     * delivers an event that is configured with standard values.
     * only thing changing is the agent time when the event occurred.
     *
     * @return
     */
    public static Event getStandardEvent() {
        return new Event();
    }

    /**
     * creates a WARNING message that the storage is running out of storage.
     * Please specify how much of the capacity has been used up so far.
     *
     * @param capacity an integer value stating how much of the capacity has been used up so far.
     *                 75 would lead to a message that 75 % of the storage has been used so far. Please
     *                 provide values between 0 and 100.
     * @return a warning event that can be sent to moogsoft.
     */
    public static Event getStorageWarning(Integer capacity) {
        Event result = new Event();
        result.setSource("DataStorage_NAS001");
        result.setSourceId("CMDB_1");
        result.setAgentLocation("DC_FRA_02_25");
        result.setSeverity(2);
        result.setType("server/storage");
        result.setEventClass("storage");
        result.setDescription(StringUtils.join("The data storage runs full. Capacity used is ", capacity, "%!"));
        return result;
    }

    public static Event getStorageOkEvent(Integer capacity) {
        Event result = new Event();
        result.setSource("DataStorage_NAS001");
        result.setSourceId("CMDB_1");
        result.setAgentLocation("DC_FRA_02_25");
        result.setSeverity(0);
        result.setType("server/storage");
        result.setEventClass("storage");
        result.setDescription(StringUtils.join("The data storage is ok. Capacity used is ", capacity, "%!"));
        return result;
    }

    public static Event getDatabaseIsWorkingEvent() {
        Event result = new Event();
        result.setSource("Database MySQL");
        result.setSourceId("CMDB_2");
        result.setAgentLocation("DC_FRA_02_25");
        result.setSeverity(0);
        result.setType("server/storage");
        result.setEventClass("storage");
        result.setDescription(StringUtils.join("Just meant to say: the database is running alrighty-right!"));
        return result;
    }

    public static Event getDatabaseIsDownError() {
        Event result = new Event();
        result.setSource("Database MySQL");
        result.setSourceId("CMDB_2");
        result.setAgentLocation("DC_FRA_02_25");
        result.setSeverity(5);
        result.setType("server/storage");
        result.setEventClass("storage");
        result.setDescription(StringUtils.join("The database just crashed! Error message is: The data storage is full. Capacity used is 100%"));
        return result;
    }

    public static Event getOrderServicePerformanceImpactedWarning() {
        Event result = new Event();
        result.setSource("Order Service APM");
        result.setSourceId("CMDB_3");
        result.setAgentLocation("DC_FRA_02_26");
        result.setSeverity(2);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("OrderService performance impacted. Service not answering for prolonged intervals."));
        return result;
    }

    public static Event getOrderServiceError() {
        Event result = new Event();
        result.setSource("Order Service Logfile Monitor");
        result.setSourceId("CMDB_3");
        result.setAgentLocation("DC_FRA_02_26");
        result.setSeverity(5);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("Order Service logs error message: database connection timeout. java.net.SocketException: java.net.ConnectException: Connection timed out: connect"));
        return result;
    }

    public static Event getOrderServiceBackToNormalEvent() {
        Event result = new Event();
        result.setSource("Order Service Logfile Monitor");
        result.setSourceId("CMDB_3");
        result.setAgentLocation("DC_FRA_02_26");
        result.setSeverity(0);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("Order Service logs that the service is back to normal."));
        return result;
    }

    public static Event getUserDataServiceError() {
        Event result = new Event();
        result.setSource("Userdata Service APM");
        result.setSourceId("CMDB_4");
        result.setAgentLocation("DC_FRA_04_01");
        result.setSeverity(2);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("Userdata Service logs error message: database connection timeout. java.net.SocketException: java.net.ConnectException: Connection timed out: connect"));
        return result;
    }

    public static Event getUserDataServiceBackToNormal() {
        Event result = new Event();
        result.setSource("Userdata Service APM");
        result.setSourceId("CMDB_4");
        result.setAgentLocation("DC_FRA_04_01");
        result.setSeverity(0);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("Userdata Service logs that the service is back to normal"));
        return result;
    }

    public static Event getUserDataServicePerformanceImpactedWarning() {
        Event result = new Event();
        result.setSource("Userdata Service Logfile Monitor");
        result.setSourceId("CMDB_4");
        result.setAgentLocation("DC_FRA_04_01");
        result.setSeverity(5);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("UserDataService performance impacted. Service not answering for prolonged intervals."));
        return result;
    }

    public static Event getSkuServiceError() {
        Event result = new Event();
        result.setSource("SKU Service Logfile Monitor");
        result.setSourceId("CMDB_5");
        result.setAgentLocation("DC_FRA_02_27");
        result.setSeverity(5);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("SKU Service logs error message: database connection timeout. java.net.SocketException: java.net.ConnectException: Connection timed out: connect."));
        return result;
    }

    public static Event getSkuServiceBackToNormal() {
        Event result = new Event();
        result.setSource("SKU Service Logfile Monitor");
        result.setSourceId("CMDB_5");
        result.setAgentLocation("DC_FRA_02_27");
        result.setSeverity(0);
        result.setType("service/backend");
        result.setEventClass("service");
        result.setDescription(StringUtils.join("Userdata Service logs that the service is back to normal"));
        return result;
    }

    public static Event getStandardEventAtSeverityLevel(int severity) {
        Event event = new Event();
        event.setSeverity(severity);
        return event;
    }

    public String getSignature() {
        return StringUtils.join(getSource(), ":", getEventClass(), ":", getType());
    }

    public String getSourceId() {
        return source_id;
    }

    public void setSourceId(String source_id) {
        this.source_id = source_id;
    }

    public String getExternalId() {
        return external_id;
    }

    public void setExternalId(String external_id) {
        this.external_id = external_id;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }

    public String getAgentLocation() {
        return agent_location;
    }

    public void setAgentLocation(String agent_location) {
        this.agent_location = agent_location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAgentTime() {
        return agent_time;
    }

    public void setAgentTime(Long agent_time) {
        this.agent_time = agent_time;
    }

}
