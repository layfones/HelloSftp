package com.layfones.hellosftp;

import static com.jcraft.jsch.ChannelSftp.RESUME;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.layfones.hellosftp.sftp.Callback;
import com.layfones.hellosftp.sftp.Request;
import com.layfones.hellosftp.sftp.SftpClient;
import com.layfones.hellosftp.sftp.SftpUser;
import com.layfones.hellosftp.sftp.Work;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SftpClient.with(new SftpUser()).upload(new Request("", "", RESUME)).enqueue(new Callback() {
            @Override
            public void onProgress(long progress) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Work work, Exception e) {

            }
        });

    }
}