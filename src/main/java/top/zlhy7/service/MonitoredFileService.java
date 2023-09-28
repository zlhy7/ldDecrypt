package top.zlhy7.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import top.zlhy7.listener.FileListener;
import top.zlhy7.model.DecryptWebSocketBody;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import static top.zlhy7.constant.Constants.FILE_SEPARATOR;

/**
 * @author shafulin
 * @date 2023/2/3 15:20
 * @description 监控本地指定目录文件
 */
@Slf4j
@Service
public class MonitoredFileService {
    /**
     * 被监控的文件目录
     * 放置到此目录的所有文件会解密并放置到同级的 "${monitoredFilePath}_解密"目录中
     */
    @Value("${monitoredPath}")
    private String monitoredFilePath;
    /**
     * 解密后目录
     */
    @Value("${monitoredDecryptPath}")
    private String monitoredDecryptPath;
    /**
     * 被监控的文件目录 file对象
     */
    public static File monitoredFilePathObj;
    /**
     * 解密文件目录 file对象
     */
    public static File monitoredDecryptPathObj;
    @PostConstruct
    public void init(){
        log.info("绿盾文件解密监控目录：{}",monitoredFilePath);
        log.info("初始化相关目录对象");
        monitoredFilePathObj = new File(monitoredFilePath);
        monitoredDecryptPathObj = new File(monitoredDecryptPath);
        if (!monitoredFilePathObj.exists()){
            monitoredFilePathObj.mkdirs();
        }
        if (!monitoredDecryptPathObj.exists()){
            monitoredDecryptPathObj.mkdirs();
        }
        //开启监听指定目录
        startFileWatch();
    }
    /**
     * 开启监听指定目录
     * @return
     * @author 任勇 on 2020/8/26 10:07
     */
    public void startFileWatch(){
        log.info("***************开启监听指定目录 {}***************",monitoredFilePath);
        // 监控目录
        String rootDir = monitoredFilePath;
        // 轮询间隔 5 秒
        long interval = TimeUnit.SECONDS.toMillis(1);
        // 创建过滤器·文件夹
        IOFileFilter directories = FileFilterUtils.and(
                FileFilterUtils.directoryFileFilter(),
                HiddenFileFilter.VISIBLE);
        // 创建过滤器·文件
        IOFileFilter filter = FileFilterUtils.or(directories, FileFilterUtils.fileFileFilter());
        // 使用过滤器
        FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir), filter);
        //添加监听器
        observer.addListener(new FileListener());
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        try{
            monitor.start();
            log.info("***************监控中***************");
        }catch (Exception e){
            log.error("异常处理 {}",e.getMessage());
        }
    }

    /**
     * 解密
     * @param file
     *
     */
    public void decrypt(File file) throws Exception {
        System.out.printf("开始解密：%s,原文件大小：%d\n",file.getAbsolutePath(),file.length());
        //region 解密目标地址
        String path2 = file.getAbsolutePath().replace("\\",FILE_SEPARATOR)
                .replace("//",FILE_SEPARATOR)
                .replace(monitoredFilePath,"");
        File decryptFile = new File(monitoredDecryptPath + path2);
        if (!decryptFile.getParentFile().exists()) {
            decryptFile.getParentFile().mkdirs();
        }
        //endregion
        try (InputStream fis = new FileInputStream(file)){
            Files.copy(fis,Paths.get(decryptFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                System.out.printf("解密完毕：%s\n",decryptFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        decrypt(file,monitoredFilePath,monitoredDecryptPath);
    }
    /**
     * 手动指定解密目录
     * @param monitoredFilePath 监控目录
     * @param monitoredDecryptPath 解密文件生成目录
     * @return
     * @author 沙福林 on 2023/9/22 12:10
     */
    public void decrypt(String monitoredFilePath,String monitoredDecryptPath) throws Exception {
        // 手动解密
        MonitoredFileService monitoredFileService = new MonitoredFileService();
        monitoredFileService.monitoredFilePath = "E:/download/";
        monitoredFileService.monitoredDecryptPath = "E:/fileWatch_解密/";
        Files.walk(Paths.get(monitoredFilePath))
                .map(Path::toFile)
                .filter(file -> !file.isDirectory())
                .forEach(file -> {
                    try {
                        monitoredFileService.decrypt(file,monitoredFilePath,monitoredDecryptPath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    /**
     * 手动指定解密目录
     * @param file 文件目录
     * @param monitoredFilePath 监控目录
     * @param monitoredDecryptPath 解密文件生成目录
     * @return
     * @author 沙福林 on 2023/9/22 12:10
     */
    public void decrypt(File file,String monitoredFilePath,String monitoredDecryptPath){
        System.out.printf("开始解密：%s,原文件大小：%d\n",file.getAbsolutePath(),file.length());
        //region 解密目标地址
        String path2 = file.getAbsolutePath().replace("\\",FILE_SEPARATOR)
                .replace("//",FILE_SEPARATOR)
                .replace(monitoredFilePath,"");
        File decryptFile = new File(monitoredDecryptPath + path2);
        if (!decryptFile.getParentFile().exists()) {
            decryptFile.getParentFile().mkdirs();
        }
        //endregion
        try (InputStream fis = Files.newInputStream(file.toPath())){
            Files.copy(fis,Paths.get(decryptFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.printf("解密完毕：%s\n",decryptFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 网页指定解密目录
     * @param decryptWebSocketBody websocket消息体
     * @param webSocketService websocket服务
     * @return
     * @author 沙福林 on 2023-09-27 20:10:11
     */
    public void decrypt(DecryptWebSocketBody decryptWebSocketBody,WebSocketService webSocketService) throws Exception {
        // 监控目录
        String monitoredFilePath = decryptWebSocketBody.getMonitoredFilePath();
        // 解密生成目录
        String monitoredDecryptPath = decryptWebSocketBody.getMonitoredDecryptPath();
        // 文件路径分隔符以及末尾符号处理
        String regex = "[\\\\|/]+";
        monitoredFilePath = monitoredFilePath.replaceAll(regex,FILE_SEPARATOR);
        if(!monitoredFilePath.endsWith(FILE_SEPARATOR)){
            monitoredFilePath += FILE_SEPARATOR;
        }
        monitoredDecryptPath = monitoredDecryptPath.replaceAll(regex,FILE_SEPARATOR);
        if(!monitoredDecryptPath.endsWith(FILE_SEPARATOR)){
            monitoredDecryptPath += FILE_SEPARATOR;
        }
        // 监控目录
        File monitoredFile = new File(monitoredFilePath);
        File monitoredDecryptFile = new File(monitoredDecryptPath);
        if (!monitoredDecryptFile.exists()) {
            // 解密目录不存在就创建
            monitoredDecryptFile.mkdirs();
        }
        webSocketService.sendPathMsg("开始解密目录：%s",monitoredFile.getAbsolutePath());
        String finalMonitoredFilePath = monitoredFilePath;
        String finalMonitoredDecryptPath = monitoredDecryptPath;
        StopWatch stopWatch = new StopWatch();
        Files.walk(monitoredFile.toPath()).forEach(path->{
            stopWatch.start("解密文件："+path);
            //region 解密目标地址
            File file = path.toFile();
            String decryptFilePath2 = finalMonitoredDecryptPath+ file.getAbsolutePath().replaceAll(regex,FILE_SEPARATOR)
                    .replace(finalMonitoredFilePath,"");
            File decryptFile = new File(decryptFilePath2);
            if (!decryptFile.getParentFile().exists()) {
                decryptFile.getParentFile().mkdirs();
            }
            // 自己就是目录则直接创建就可以了
            if (file.isDirectory()) {
                decryptFile.mkdirs();
                webSocketService.sendPathMsg("创建目录：%s",decryptFile.getAbsolutePath());
                stopWatch.stop();
                return;
            }
            //endregion
            try (InputStream fis = Files.newInputStream(file.toPath())){
                Files.copy(fis,decryptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                stopWatch.stop();
                webSocketService.sendPathMsg("解密完毕：%s,耗时：%d ms",
                        decryptFile.getAbsolutePath(),stopWatch.getLastTaskTimeMillis());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static void main(String[] args) throws Exception {
        // 手动解密
        MonitoredFileService monitoredFileService = new MonitoredFileService();
        // 注意文件夹末尾必须带“/”
        monitoredFileService.decrypt("E:/deskTop/日常工作添加/需求文档/","E:/fileWatch_解密/");
    }
}
