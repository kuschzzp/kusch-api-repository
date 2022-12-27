# vedio-download

#### 介绍

springboot 使用RestTemplate进行视频下载功能。

**2022-12-26：**

慢是因为后台使用http请求下载了一遍视频，练习写法而已，可以直接把url返回，浏览器直接下载就会很快了，但有的平台会出现没有权限，那就只能后台下载，然后用流写回前台了。

下载链接例子： http://127.0.0.1:17777/download/common?videoUrl=https://v.kuaishou.com/CHwRjS&way=0

way参数：0代表使用流下载，1代表直接返回视频下载链接

#### 目前支持

1. 抖音
2. 皮皮虾
3. 最右
4. 快手
5. bilibili