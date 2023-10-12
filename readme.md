# 绿盾解密
## 成品下载
[gitee-releases](https://gitee.com/zlhy7/ldDecrypt/releases)

## 服务使用前提，一定要看

- 电脑必须装有jdk环境，没有的可以[参考博文](https://zlhy7.gitee.io/znote/views/notes/installation_tutorial/jdk.html)
- 电脑必须装有绿盾环境，否则无法解密成功

## 回答一些提问
1. 这个会被监控到么？
> 我认为不会，因为全程都是在本地操作，都没有发起绿盾解密申请。如果比较怕，建议不要用。建议自用不外传。

2. 这个是啥原理？
> 我理解就是利用加密文件在绿盾环境电脑上可以正常打开。既然软件能直接打开，我通过程序也就能直接打开，将能打开的文件，通过字符流重新写入到磁盘，只要不随便移动，那就是解密状态

## 常用命令
```shell
# 打包
mvnd clean package -Dmaven.test.skip=true
# jar包方式启动
java -jar -Dserver.port=980 ldDecrypt.jar
```

## 绿盾解密服务使用
### 方式一：接口解密

1. 将**ldDecrypt.exe**,**ldDecrypt.jar**,**ldDecrypt.xml** 三个文件，放置到同名目录 `ldDecrypt`中，如图
注意注意注意
- **ldDecrypt.exe**文件不是双击运行的
- **ldDecrypt.exe**文件不是双击运行的
- **ldDecrypt.exe**文件不是双击运行的

![pSrUlGV.png](https://s1.ax1x.com/2023/02/02/pSrUlGV.png)

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

![pSrUbZj.png](https://s1.ax1x.com/2023/02/02/pSrUbZj.png)

5. 接口文档

POST 本地地址: `http://127.0.0.1:980/test/ldDecrypt`
POST 沙福林地址: `http://zlhy7:980/test/ldDecrypt`

请求参数

| 名称         | 位置   | 类型           | 必选 | 说明                                       |
| ------------ | ------ | -------------- | ---- | ------------------------------------------ |
| body         | body   | object         | 否   |                                            |
| » file       | body   | string(binary) | 否   | 待解密文件，允许上传多个                   |
| » deleteFlag | body   | integer        | 否   | 0不删除，1删除，默认删除 转化后文件        |

个人推荐直接用沙福林的服务就行了，省事，反正他电脑永不关机

### 方式二：本地文件监控
默认服务监控 `D:\fileWatch` 目录文件变化，会将解密文件放到`D:\fileWatch_解密`里 ,直接往监控目录里丢文件就完事了

本地文件监控日志查看[http://127.0.0.1:980/page/localMonitorLog](http://127.0.0.1:980/page/localMonitorLog)
### 方式三：自定义解密目录
这种解密方式是为了弥补解密方式2的不足，因为还要将加密文件都挪到监控目录下才可以，如果可以自定义解密目录，那就不用挪动了
1. 启动项目后浏览器访问 [http://127.0.0.1:980/page](http://127.0.0.1:980/page)
2. 填写待解密的目录，以及生成解密文件的目录。
3. 点击解密按钮,可查看日志输出

### 方式三-注意事项
1. 如果你选择的是根路径，解密目录里也写上根路径
例如：在`D:/soft/`目录下有 `1.txt`,`目录2`,`目录3`,
如果想在生成目录里也有`soft`这一级，就要自己在生成目录里填写
```shell
解密目录： D:/soft/
生成目录： D:/fileWatch_解密/soft/
# 如果不需要生成soft目录，则
生成目录： D:/fileWatch_解密/
```
2. 每解密一个目录可以换下一个目录，如果不更换，存在于原目录的文件会被再次解密