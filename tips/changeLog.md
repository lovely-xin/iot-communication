## v1.2.7
- 更新时间：2023.03.16
- 修复下S7序列化读取时，由于实体类字段超过18个且读取数据量较小导致报文大小超过PDU的BUG
- S7通信支持短连接操作，默认长连接
- 添加S7协议的服务端模拟器，支持本地简单功能模拟
- 添加相关TCP和UDP服务端以及客户端的基类，便于后续使用
- 添加Slf4j日志

## v1.2.6
- 更新时间：2022.12.06
- 紧急修复RequestItem中存储区参数一直为DB块导致无法访问其他区的问题
- lombok依赖版本升级到推荐版本

## v1.2.5
- 更新时间：2022.12.01
- 修改基础的sourceTSAP和destinationTSAP
- SocketBasic的方法暴露出来
- ByteReadBuff和ByteWriteBuff添加littleEndian字段

## v1.2.4
- 更新时间：2022.11.09
- 完善pduLength的数据大小约束
- 自动分割数据量大于pduLength的报文，支持大数据量的读写

## v1.2.3
- 更新时间：2022.10.31
- 添加S7协议的序列化访问功能
- 添加字节数组序列化解析功能

## v1.2.2
- 更新时间：2022.09.29
- 添加对200smart的PLC兼容，其V区就是DB1区

## v1.2.1
- 更新时间：2022.09.27
- 重构字节数组的读写方式，添加ByteReadBuff和ByteWriteBuff两个类

## v1.2.0
- 更新时间：2022.07.08
- 添加modbusTcp通信协议

## v1.1.1
- 更新时间：2022.05.17
- 移除没用的依赖包

## v1.1.0
- 更新时间：2022.05.15
- 添加S7通信协议

## v1.0.2
- 更新时间：2022.04.28
- 修复int16,uint16,int32,uint32的解析错

## v1.0.1
- 更新时间：2021.02.25
- 修复float32和float64数据解析成list时候的错误

## v1.0.0
- 搭建时间：2021.02.17
- 目的：为了休闲乐趣，简单小尝试
- 项目初始化，添加字节数据解析功能


