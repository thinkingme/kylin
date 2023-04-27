# 本版本仅供交流学习使用

> 基于 go-cqhttp 和 java 的 qq 机器人

## 运行环境
* jdk11
* maven(有gradle文件是个人学习)
## 基本功能
1. 使用go-cqhttp与qq机器人通信（https://github.com/Mrs4s/go-cqhttp）
2. 对接青龙面板（https://github.com/rubyangxg/jd-qinglong）
3. 收到qq好友消息或者临时窗口消息后，进行jd cookie解析并上传到青龙面板
4. 定时检查青龙面板的cookie是否过期并提醒
5. docker部署（文档待写）
6. 