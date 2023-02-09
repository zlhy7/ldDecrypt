# 绿盾解密
打包
```shell
mvnd clean package -Dmaven.test.skip=true
```
成品下载 项目已经打包一个了，直接用

## 绿盾解密服务使用 方式一

1. 将**ldDecrypt.exe**,**ldDecrypt.jar**,**ldDecrypt.xml** 三个文件，放置到同名目录 `ldDecrypt`中，如图

   [![pSrUlGV.png](https://s1.ax1x.com/2023/02/02/pSrUlGV.png)](https://imgse.com/i/pSrUlGV)

2. 根据自己需要看是否要修改**ldDecrypt.xml**里的配置,一般不用改任何

   ```xml
   <service> <!--服务ID：启动、关闭、删除服务时，都是通过ID来操作的,与jar名称保持一致-->
     <id>ldDecrypt</id> <!--服务名称,与jar名称保持一致-->
     <name>ldDecrypt</name> <!-- 服务描述 -->
     <description>这是一个测试WinSW的程序</description> <!--当前电脑配置了java环境变量，直接写成“java”就行；你也可以写成类似这样：D:\develop\jdk1.8\jre\bin\java-->
     <executable>java</executable> <!--启动参数-->
     <arguments>-jar -Dserver.port=980 ldDecrypt.jar</arguments> <!-- 日志地址 %BASE% 就代表了服务安装时的目录-->
     <logpath>%BASE%\log</logpath> <!-- 日志模式 -->
     <logmode>rotate</logmode>
   </service>
   ```

3. 执行命令安装服务

   ```shell
   # 删除服务,重新部署，第一次安装的话就不用执行该服务
   net stop ldDecrypt
   sc delete ldDecrypt
   # 安装服务
   ldDecrypt.exe install
   # 启动服务
   net start ldDecrypt
   ```

4. 接口调用

   [![pSrUbZj.png](https://s1.ax1x.com/2023/02/02/pSrUbZj.png)](https://imgse.com/i/pSrUbZj)

5. 接口文档
   POST 本地地址: `127.0.0.1:980/test/ldDecrypt`
   POST 沙福林地址: `zlhy7:980/test/ldDecrypt`

请求参数

| 名称         | 位置   | 类型           | 必选 | 说明                                       |
| ------------ | ------ | -------------- | ---- | ------------------------------------------ |
| body         | body   | object         | 否   |                                            |
| » file       | body   | string(binary) | 否   | 待解密文件，允许上传多个                   |
| » deleteFlag | body   | integer        | 否   | 0不删除，1删除，默认删除 转化后文件        |

个人推荐直接用沙福林的服务就行了，省事，反正他电脑永不关机


## 绿盾解密服务使用 方式二：本地文件监控
默认服务监控 `D:\fileWatch` 目录文件变化，会将解密文件放到`D:\fileWatch_解密`里

直接往监控目录里丢文件就完事了
### 共享目录地址
注意`zlhy7`为解密电脑主机名，也可用ip代替，个人习惯写主题名
监控目录 `\\zlhy7\fileWatch`
解密目录`\\zlhy7\fileWatch_解密`里