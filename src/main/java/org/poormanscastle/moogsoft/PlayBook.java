package org.poormanscastle.moogsoft;

/**
 * a PlayBook simulates a scenario which is executed by sending
 * events to Moogsoft to make it believe that something has
 * happened on a simulated infrastructure.
 */
public interface PlayBook {

    void perform();

    /**
     * for your convenience you can provide a pre-configured datauplink for the playbook.
     *
     * @param dataUplink
     */
    void setDataUplink(DataUplink dataUplink);


    static PlayBook getDataStorageOutageSzenario() {
        return new SimpleDatastoreOutagePlaybook();
    }

}
