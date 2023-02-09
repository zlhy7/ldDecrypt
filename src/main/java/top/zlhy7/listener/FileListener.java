package top.zlhy7.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Component;
import top.zlhy7.service.MonitoredFileService;
import top.zlhy7.util.SpringUtils;

import java.io.File;

/**
 * @author 任勇
 * @date 2020/8/26 9:43
 * @description 文件变化监听器
 */
@Component
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {
    /**
     * 监控文件
     */
    private MonitoredFileService monitoredFileService = SpringUtils.getBean(MonitoredFileService.class);
    /**
     * 文件夹新增
     * @param directory
     */
    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
    }

    /**
     * 文件夹更新
     * @param directory
     */
    @Override
    public void onDirectoryChange(File directory) {
        super.onDirectoryChange(directory);
    }

    /**
     * 文件夹删除
     * @param directory
     */
    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
    }

    /**
     * 文件新增
     * @param file
     */
    @Override
    public void onFileCreate(File file) {
        log.info("有新文件了 {}", file.getAbsoluteFile());
        try {
            monitoredFileService.decrypt(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件更新
     * @param file
     */
    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
    }

    /**
     * 文件删除
     * @param file
     */
    @Override
    public void onFileDelete(File file) {
        super.onFileDelete(file);
    }

    /**
     * 启动
     * @param observer
     */
    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    /**
     * 停止
     * @param observer
     */
    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }
}