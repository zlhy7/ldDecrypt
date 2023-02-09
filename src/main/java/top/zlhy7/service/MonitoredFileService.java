package top.zlhy7.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import top.zlhy7.controller.LdDecryptController;
import top.zlhy7.listener.FileListener;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

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
    public static MultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file",
                MediaType.MULTIPART_FORM_DATA_VALUE, true, file.getName());
        try (InputStream is = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(is, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }
        return new CommonsMultipartFile(item);
    }

    /**
     * 解密
     * @param file
     */
    public void decrypt(File file) throws Exception {
        log.info("开始解密：{}",file.getAbsolutePath());
        MultipartFile multipartFile = getMultipartFile(file);
        System.out.println(multipartFile.getSize());
        LdDecryptController ldDecryptController = new LdDecryptController();
        File file1 = null;
        try {
            file1 = ldDecryptController.batchTransferTo(new MultipartFile[]{multipartFile});
            log.info("解密完毕：{}",file1.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 移动文件到解密目录,覆盖已存在
        Path path = Files.move(Paths.get(file1.getAbsolutePath()), Paths.get(monitoredDecryptPath,file1.getName()), StandardCopyOption.REPLACE_EXISTING);
        log.info("移动文件到解密目录：{}",path);
        log.info("转化完毕");
    }
}
