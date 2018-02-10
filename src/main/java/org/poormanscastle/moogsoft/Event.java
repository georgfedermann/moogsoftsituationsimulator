package org.poormanscastle.moogsoft;

import java.util.Date;

public class Event {

    String signature = "my_test_box:application_server:server";
    String source_id = "198.51.200.3";
    String external_id = "id-1234";
    String manager = "Kevin";
    String source = "Kevins Macbook";
    String eventClass = "server";
    String agent_location = "London";
    String type = "Server";
    Integer severity = 1;
    String description = "My MacPro is going nuts";
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
