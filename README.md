# article
## 百库十表实现
- 近日在公司一项目独自实现了订单表拆分，采用百库十表的水平分法。即取订单尾三位数字，前两位用于映射库名，后一位映射表名。本人将架构组开发的中间件剥离敏感内容后，整合到common-mysql模块，仅具备分库分表功能（包括百库十表，多set百库十表，年库天表），原有的多数据源功能有空再整合了。


- 在测试本例之前需要用脚本一键创建“百库十表”，具体操作和脚本请见：http://note.youdao.com/noteshare?id=ffaea74a6a19e65a12439d3789dc98e6&sub=285737A8384D4839B3387ED62118351E


- 本例mybatis配置以xml文件形式配置，更好的方式是在common-mysql模块整合DataSource，MySqlSessionTemplate等
