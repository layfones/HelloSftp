package com.layfones.hellosftp.sftp;


public interface Callback {

    void onProgress(long progress);

    void onSuccess();

    void onFailure(Work work, Exception e);

}
