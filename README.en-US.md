

# Green Shield Decrypt
# [Domestic Link](https://gitee.com/zlhy7/ldDecrypt)
## Release Download
[gitee-releases](https://gitee.com/zlhy7/ldDecrypt/releases)

## Service Prerequisites, MUST READ

- The computer must have a JDK environment installed. If not, refer to this [tutorial](https://zlhy7.gitee.io/znote/views/notes/installation_tutorial/jdk.html)
- The computer must have the Green Shield environment installed, otherwise decryption will fail

## FAQ
1. Will this be detected/monitored?
> I don't think so, as the entire process is local and does not request Green Shield decryption from the server. If you are concerned, I recommend not using it. For personal use only; do not distribute.

2. What is the underlying principle?
> My understanding is that encrypted files can be opened normally on computers with the Green Shield environment. Since the software can open them directly, my program can do the same. It reads the opened file as a character stream and writes it back to the disk. As long as you don't move it around, it remains in a decrypted state.

## Common Commands
```shell
# package
mvnd clean package -Dmaven.test.skip=true
# start via jar
java -jar -Dserver.port=980 ldDecrypt.jar
```

## Green Shield Decrypt Service Usage
### Method 1: API Decryption

1. Place the three files **ldDecrypt.exe**, **ldDecrypt.jar**, and **ldDecrypt.xml** into a directory with the same name `ldDecrypt`, as shown below.
   Note, Note, Note
- **ldDecrypt.exe** is not meant to be run by double-clicking.
- **ldDecrypt.exe** is not meant to be run by double-clicking.
- **ldDecrypt.exe** is not meant to be run by double-clicking.

![pSrUlGV.png](https://s1.ax1x.com/2023/02/02/pSrUlGV.png)

2. Modify the configuration in **ldDecrypt.xml** as needed. Generally, no changes are required.

```xml
<service> <!-- Service ID: Start, stop, and delete operations all use this ID. Keep it consistent with the jar name -->
   <id>ldDecrypt</id> <!-- Service name, consistent with the jar name -->
   <name>ldDecrypt</name> <!-- Service description -->
   <description>This is a test program for WinSW</description> <!-- If the current computer has JAVA_HOME configured, just write "java"; otherwise, you can write something like this: D:\develop\jdk1.8\jre\bin\java -->
   <executable>java</executable> <!-- Startup arguments -->
   <arguments>-jar -Dserver.port=980 ldDecrypt.jar</arguments> <!-- Log path %BASE% represents the directory where the service is installed -->
   <logpath>%BASE%\log</logpath> <!-- Log mode -->
   <logmode>rotate</logmode>
</service>
```

3. Execute commands to install the service

```shell
# Stop service and redeploy. Skip this for the first installation.
net stop ldDecrypt
sc delete ldDecrypt
# Install service
ldDecrypt.exe install
# Start service
net start ldDecrypt
```

4. API Call

![pSrUbZj.png](https://s1.ax1x.com/2023/02/02/pSrUbZj.png)

5. API Documentation

POST Local Address: `http://127.0.0.1:980/test/ldDecrypt`
POST Safulin Address: `http://zlhy7:980/test/ldDecrypt`

Request Parameters

| Name         | Location | Type           | Required | Description                                                |
| ------------ | -------- | -------------- | -------- | ---------------------------------------------------------- |
| body         | body     | object         | No       |                                                            |
| » file       | body     | string(binary) | No       | File to decrypt. Multiple files are allowed.               |
| » deleteFlag | body     | integer        | No       | 0: do not delete, 1: delete. Default is to delete the decrypted file. |

Personally, I recommend just using Safulin's service directly to save trouble. His PC never turns off anyway.

### Method 2: Local File Monitoring
By default, the service monitors file changes in the `D:\fileWatch` directory and places decrypted files into `D:\fileWatch_解密`. Simply drop files into the monitored directory.

View local file monitoring logs at [http://127.0.0.1:980/page/localMonitorLog](http://127.0.0.1:980/page/localMonitorLog)
### Method 3: Custom Decryption Directory
This decryption method compensates for the limitations of Method 2, as it requires moving all encrypted files to the monitored directory. If you can specify a custom decryption directory, there's no need to move them.
1. After starting the project, visit [http://127.0.0.1:980/page](http://127.0.0.1:980/page) in your browser.
2. Enter the directory containing the files to be decrypted, and the directory for the output decrypted files.
3. Click the Decrypt button to view the log output.

### Method 3 - Notes
1. If you select a root path, include the root path in both the decryption and generation directories.
   For example: Under `D:/soft/`, there are `1.txt`, `dir2`, `dir3`,
   If you want the `soft` level to also exist in the output directory, you must specify it manually in the output directory path:
```shell
Decryption Directory: D:/soft/
Output Directory: D:/fileWatch_解密/soft/
# If you do not want to create the soft directory, then
Output Directory: D:/fileWatch_解密/
```
2. You can switch to another directory after decrypting one. If you do not change it, files already in the original directory will be decrypted again.
