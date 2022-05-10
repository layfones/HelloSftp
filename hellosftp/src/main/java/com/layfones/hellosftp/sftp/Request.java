package com.layfones.hellosftp.sftp;

public class Request {

    private String srcPath;
    private String dstPath;
    @SftpClient.Mode
    private int mode;

    public Request(String srcPath, String dstPath, @SftpClient.Mode int mode) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.mode = mode;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDstPath() {
        return dstPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
