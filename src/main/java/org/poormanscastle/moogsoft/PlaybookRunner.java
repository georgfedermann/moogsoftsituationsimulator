package org.poormanscastle.moogsoft;

public class PlaybookRunner {
    public static void main(String[] args) throws Exception {
        DataUplink uplink = new DataUplink();
        uplink.configureUplink("ruderest", "webhook_testwebhook",
                MoogsoftCredentials.getKevinCredentials());
        PlayBook myScenario = PlayBook.getDataStorageOutageSzenario();
        myScenario.setDataUplink(uplink);
        myScenario.perform();
    }

}
