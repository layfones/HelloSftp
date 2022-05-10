package com.layfones.hellosftp.sftp;

public class SftpUser {

    private String host;
    private String port;
    private String username;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SftpUser sftpUser = (SftpUser) o;

        if (host != null ? !host.equals(sftpUser.host) : sftpUser.host != null) return false;
        if (port != null ? !port.equals(sftpUser.port) : sftpUser.port != null) return false;
        if (username != null ? !username.equals(sftpUser.username) : sftpUser.username != null)
            return false;
        return password != null ? password.equals(sftpUser.password) : sftpUser.password == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
