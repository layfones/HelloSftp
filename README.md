# HelloSftp
##### 封装JSCH实现Android SFTP文件上传

[![](https://jitpack.io/v/layfones/HelloSftp.svg)](https://jitpack.io/#layfones/HelloSftp)

## Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
  
## Step 2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.layfones:HelloSftp:1.0.0'
}
```

## Step 3. Use

```java
SftpClient.with(new SftpUser()).upload(new Request("src", "dst", RESUME)).enqueue(new Callback() {
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
```
