# 个人云盘项目
## 项目描述
一个仿百度云盘面向C端用户的网盘项目，包括用户注册，QQ快捷登录，文件上传，分片上传，断点断传，秒传，文件在线预览，包括文本、图片、视频、音频、Excel、Word、PDF等文件的在线预览，文件分享等功能

## 技术选型

SpringBoot+Mybatis+MySQL+Redis+ffmpeg

## 负责内容

1. 用户注册，登录，QQ快捷登录，发送邮箱验证码，找回密码
2. 文件分片上传，秒传，新建目录，预览，文件重命名，文件移动，文件分享，删除，下载等功能
3. 文件分享列表，取消分享
4. 回收站功能，还原文件，彻底删除
5. 设置模块
   1. 超级管理员角色查询所有用户上传的文件，可以对文件下载，删除
   2. 超级管理员可以对用户进行管理，给用户分配空间，禁用、启用用户
   3. 超级管理员可以对系统进行设置，设置邮箱模板，设置用户注册初始化空间大小
6. 用户通过分享链接和分享码预览下载其他人分享的文件，同时也可以将文件保存到自己的网盘

## 项目难点

1. 文件分片上传，通过文件`MD5`实现文件秒传。文件分片上传后，异步对文件进行合并处理，视频文件，调用`ffmpeg`生成视频缩略图，将文件分片成`ts`文件
2. 通过`Redis`缓存实时计算用户上传过程中空间占用情况
3. 多级目录线性展示，通过递归查询，查询目录的所有父级目录
4. 用户上传文件，同一级目录重名文件自动重命名，文件移动，同名文件自动重命名

## 项目收获

熟悉第三方登录接入流程，比如QQ登录。

熟练使用`SpringBoot`，采用`Spring`的`AOP`的注解方式实现了不听的接口权限不一样，比如普通用户和超级管理员权限的区别，同时使用`AOP`和`Java`的反射实现了后端的参数校验

使用`Redis`缓存了一些系统设置，用户上传过程中空间使用实时计算，避免反复查询数据库

项目中解决了如何实现异步调用事务的问题，解决循环依赖的问题，如何调用第三方插件比如`ffmpeg`来实现对文件的分片处理，合并处理

学习到如何从功能点去设计数据库，在数据库设计的时候考虑到后续的扩展，比如文件数据的分表处理，可以根据用户`id hash`取模的方式对文件数据进行分表处理

通过`Spring`的核心`AOP`来实现与事务的解耦