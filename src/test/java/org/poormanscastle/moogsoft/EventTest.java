package org.poormanscastle.moogsoft;


import static org.junit.Assert.assertTrue;

public class EventTest {

    @org.junit.Test
    public void getAgent_time() {
        Event event = Event.getStandardEvent();
        // tests that the returned value is a Unix time stamp in s and not in ms
        assertTrue("time is probaby number of ms since 1970, should be s since 1970", event.getAgentTime() < 2_000_000_000);
        assertTrue("time should be well beyond 1.5 billion seconds now.", event.getAgentTime() > 1_518_000);
    }

}
