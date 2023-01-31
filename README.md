# kusch-api-repository

## 介绍

**练习**springboot 使用RestTemplate进行接口请求的项目，仅仅是为了学习写法。

**目前有以下几个：**

### 视频解析下载

目前写了：

1. 抖音
2. 皮皮虾
3. 最右
4. 快手
5. bilibili

> 下载链接例子：
> http://127.0.0.1:17777/download/common?videoUrl=https://v.kuaishou.com/CHwRjS&way=0
> > way参数：0代表使用流下载，1代表直接返回视频下载链接

慢是因为后台使用http请求下载了一遍视频，练习写法而已，可以直接把url返回，浏览器直接下载就会很快了，但某些平台会出现没有权限的报错，那就只能使用流下载了。

### 获取每日白嫖的clashnode节点接口

http://127.0.0.1:17777/download/clashNode?type=yaml

> 源网站仅有 txt 和 yaml 格式的，想要哪种，改type