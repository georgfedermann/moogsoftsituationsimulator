package org.poormanscastle.moogsoft;

public interface MoogsoftCredentials {

    String getPassword();

    String getUsername();

    static MoogsoftCredentials getKevinCredentials() {
        return new MoogsoftCredentialsImpl("Webhook", "GXKGO7bA7GezHM2y");
    }

    static MoogsoftCredentials getGeorgCredentials() {
        return new MoogsoftCredentialsImpl("Webhook", "PuWL7QiRz3rutilV");
    }

    static MoogsoftCredentials getCredentials(String username, String password) {
        return new MoogsoftCredentialsImpl(username, password);
    }

}
