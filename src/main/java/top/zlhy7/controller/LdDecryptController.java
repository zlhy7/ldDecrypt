package top.zlhy7.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 沙福林
 * @date 2022/12/31 17:08
 * @description 测试控制器
 */
@Slf4j
@RequestMapping("test")
@RestController
public class LdDecryptController {
    /**
     * 测试绿盾解密
     * 其实绿盾解密的本质就是，绿盾的电脑上文件读取正常，重新创建，下载下来第一次的位置选好不会变就不会加密了
     * @param files 多文件上传
     * @param deleteFlag 删除标记，默认允许删除
     * @return
     * @throws Exception
     */
    @PostMapping("ldDecrypt")
    public ResponseEntity<byte[]> upload(@RequestParam("file") MultipartFile[] files,
                                         @RequestParam(required = false,defaultValue = "true") boolean deleteFlag) throws Exception {
        File file = batchTransferTo(files);
        log.info("解密成功：{},文件大小{}",file.getAbsolutePath(),file.length());

        //获取指定文件
        byte[] body = null;
        try (FileInputStream fis = new FileInputStream(file.getName())){
            body = IOUtils.toByteArray(fis);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        // 处理完后把本地文件删除，清理本地转化文件，不留档
        if (deleteFlag){
            file.delete();
        }
        //设置消息响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));
        return new ResponseEntity(body, headers, HttpStatus.OK);
    }
    /**
     * 批量转化
     * @param files
     * @return
     * @author shafulin on 2023/2/2 16:54
     */
    public File batchTransferTo(MultipartFile[] files) throws Exception {
        if(files==null || files.length==0){
            throw new RuntimeException("请上传转化文件");
        }
        File file = null;
        if (files.length==1) {
            // 只有一个文件直接返回
            file = new File(System.getProperty("user.dir")+"/"+files[0].getOriginalFilename());
            files[0].transferTo(file);
            return file;
        }
        // 收集上传文件

        List<File> fileList = new ArrayList<>(files.length);
        for (MultipartFile multipartFile : files) {
            file = new File(System.getProperty("user.dir")+"/"+multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);
            fileList.add(file);
        }
        // 压缩文件
        file = new File(System.getProperty("user.dir")+"/解密.zip");
        zipFileChannel(file,fileList);
        // 删除压缩文件内的文件
        fileList.forEach(File::delete);
        return file;
    }
    /**
     * 解压文件 nio方式
     * @param zipFile 压缩文件
     * @param fileList 被压缩文件列表
     * @return
     * @author 任勇 on 2020/12/22 11:26
     */
    public static void zipFileChannel (File zipFile, List<File> fileList) {
        //开始时间
        long beginTime = System.currentTimeMillis();
        try(
            OutputStream os = new FileOutputStream(zipFile);
            ZipOutputStream zipOut = new ZipOutputStream(os);
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)
        ) {
            for (int i = 0; i < fileList.size(); i++) {
                try (FileChannel fileChannel = new FileInputStream(fileList.get(i)).getChannel()){
                    zipOut.putNextEntry(new ZipEntry(fileList.get(i).getName()));
                    fileChannel.transferTo(0,fileList.get(i).length(),writableByteChannel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long e = System.currentTimeMillis() - beginTime;
        log.info("压缩文件耗时 {} ms",e);
    }
}
