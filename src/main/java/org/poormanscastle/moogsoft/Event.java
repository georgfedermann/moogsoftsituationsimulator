package org.poormanscastle.moogsoft;

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
    String signature = "my_test_box:application_server:server";  // used to identify the id, fqdn:network/hardware/app:storage/latency
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
    String manager = "Kevin";
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
     * The actual time when the event was raised.
     */
    Long agent_time = new Date().getTime();

    public String getJson() {
        return "{" +
                "'signature':'" + signature + "'," +
                "'source_id':'" + source_id + "'," +
                "'external_id':'" + external_id + "'," +
                "'manager':'" + manager + "'," +
                "'source':'" + source + "'," +
                "'class':'" + eventClass + "'," +
                "'agent_location':'" + agent_location + "'," +
                "'type':'" + type + "'," +
                "'severity':" + severity + "," +
                "'description':'" + description + "'," +
                "'agent_time':'" + agent_time + "'" +
                "}";
    }

    private Event() {
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

    public static Event getStandardEventAtSeverityLevel(int severity) {
        Event event = new Event();
        event.setSeverity(severity);
        return event;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
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

    public String getAgent_location() {
        return agent_location;
    }

    public void setAgent_location(String agent_location) {
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

    public Long getAgent_time() {
        return agent_time;
    }

    public void setAgent_time(Long agent_time) {
        this.agent_time = agent_time;
    }
}
