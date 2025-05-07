<div align="center">

<img src="app/src/main/res/drawable/ic_banner.webp" style="border-radius: 24px; margin-top: 32px;"/>

# BV
## Best Video

[![Android Sdk Require](https://img.shields.io/badge/Android-5.0%2B-informational?logo=android)](https://apilevels.com/#:~:text=Jetpack%20Compose%20requires%20a%20minSdk%20of%2021%20or%20higher)
[![GitHub](https://img.shields.io/github/license/aaa1115910/bv)](https://github.com/aaa1115910/bv)

# 该改版是给带键鼠的安卓设备使用的，严禁tv端使用，若有tv端使用者，请在24小时内删除否则后果自负,若有tv端使用需求，请下载官方小电视！[![云视听小电视](https://img.shields.io/badge/bilibili-下载-informational?logo=bilibili)](https://app.bilibili.com)
</div>

---
BV 是一款 [哔哩哔哩](https://www.bilibili.com) 的第三方 `Android` 应用，使用 `Jetpack Compose` 开发。
首先致敬一下原作者，对其持之以恒的更新保持respect🫡。
本项目也会持续合入作者的更新，以及不断加入我自己的定制修改

# 修改
主要在原bv的基础上做了一些符合我个人喜好的修改，包括（只有标Done的部分才是已经做完的，其它都是画饼）：
![screenshots.webp](screenshots.webp)

### UI变更
1. 移除首页左侧的抽屉展开效果，避免展开时占用首页信息流的展示（Done✅）
2. 缩减视频列表的各种间距（Done✅）
3. 视频标题从只显示1行改为3行（Done✅）
4. 播放页进度条展示优化，现在的太大有点影响视频画面
### 交互变更
1. 点击封面直接播放视频而不是跳转信息页
2. 调整播放时上下左右呼出的内容，主要配合第一点，不展示详情页后需要给出详情页入口
3. 优化各种case下优化返回键逻辑，比如拉起进度条时返回是关闭进度条而不是关闭播放页
4. 左右拖动后立刻快进快退不需要再点确认
### 功能变更
1. 播放页和详情页增加点赞、投币、一键三连入口，支持Up创作，不知道作者为什么没加
2. 动态列表增加按时间轴跳转（比如按天快速跳，方便从几天前开始追更），或者支持一键跳到上次看到的时间段（todo 可能难度比较大）
3. 增加快捷调整倍速播放，现在的调整路径有点长

# 安装
### [Release](https://github.com/Leelion96/bv/releases)

# License
[MIT](LICENSE) © aaa1115910