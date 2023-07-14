# kusch-api-repository

## 介绍

**练习**Springboot 使用RestTemplate进行接口请求的项目，仅仅是为了学习写法, 如果您认为我的学习代码侵犯了您的权益，联系我，我将立刻删除。

### 视频解析下载

目前写了：

1. 抖音
2. 皮皮虾
3. 最右
4. 快手
5. bilibili

> 下载链接例子,例如你要下载douyin视频的分享链接是：**https://v.douyin.com/hWVVhYe/** 那么你使用的地址就是这样的：   
> **http://127.0.0.1:17777/video?url=https://v.douyin.com/hWVVhYe/&way=1**  
> way参数：0代表使用流下载，1代表直接返回视频下载链接，推荐是1，0的话会很卡。  

0 慢是因为后台使用http请求下载了一遍视频，练习写法而已，可以直接把url返回，浏览器直接下载就会很快了，
但某些平台（比如BILIBILI）会出现没有权限的报错，那就只能使用流下载了。

### 获取每日白嫖的clashnode节点接口，可以直接将这个链接填入 clash

http://127.0.0.1:17777/freeNode?type=yaml

> 源网站仅有 txt 和 yaml 格式的，想要哪种，改type