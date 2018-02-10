package org.poormanscastle.moogsoft;

public class MoogsoftCredentialsImpl implements MoogsoftCredentials {
    private String username;
    private String password;

    public MoogsoftCredentialsImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "MoogsoftCredentialsImpl{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
