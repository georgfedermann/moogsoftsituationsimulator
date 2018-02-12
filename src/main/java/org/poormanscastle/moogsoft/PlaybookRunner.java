package org.poormanscastle.moogsoft;

public class PlaybookRunner {
    public static void main(String[] args) throws Exception {
        DataUplink uplink = new DataUplink();
        uplink.configureUplink("badpush", "webhook_webhook_testwebhook",
                MoogsoftCredentials.getGeorgCredentials());
        PlayBook myScenario = PlayBook.getDataStorageOutageSzenario();
        myScenario.setDataUplink(uplink);
        myScenario.perform();
    }

}
