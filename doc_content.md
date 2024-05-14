## 项目名：Freshen
### 中文意思：
1. vi. 变得新鲜；减少盐分；使自己精神饱满
2. vt. 使清新；使新鲜；使精神焕发

### todo:
1. 多表支持，form两个及以上表，join系列，union、union all、union系列
2. 上面两个写完就写增删改函数
3. 事务支持
4. spring支持
5. 这5个写完，基本功能就写完了，就可以搞文档了

6. where条件的非聚合函数
7. 嵌套查询，子查询
8. 目前select只考虑了单表情况，如果要多表，QueryAsBuilder需要重写

随想：
关于IReop类的构想
https://github.com/baomidou/mybatis-plus/issues/5764

随想：
1. 关于日期时间，强制使用LocalTime、LocalDateTime、LocalTime类型，你用java.util.Date就会报错
2. 关于结果集映射，默认按类型映射，如果你在crud函数中，指定了映射器，则按照属性映射。
   如果使用类型映射（显示在crud函数中指定映射器），需要注意，必须指定map的类型，
   例如：val map:Map<KProperty1<*, *>,JDBCType> = mapOf(
   Student2::id to JDBCType.BIGINT,
   Student2::name to JDBCType.VARCHAR,
   Student2::gender to JDBCType.VARCHAR,
   Student2::birthday to JDBCType.TIMESTAMP,
   Student2::phoneNumber to JDBCType.VARCHAR,
   Student2::address to JDBCType.VARCHAR
   )，属性的类型必须是KProperty1<*, *>，否则会报
   类型不匹配。
   要求:Map<KProperty1<*, *>, JDBCType>?
   实际:Map<KProperty1<Student2, {Comparable*>? & java.io.Serializable?}>, JDBCType>
3. where和having的条件，所有条件需要链式调用，就算用lambda，内部也需要链式调用
9. where(condition)和where(lambda)区别是，where(condition)不加括号，where(lambda)会给lambda中的条件整体加上括号

随想：
我本来想把FreshenRuntimeConfig弄成internal的，避免外部调用，
又因为crud函数是public的顶层函数，不能调用被internal修饰的函数，
所以Freshen的核心配置类FreshenRuntimeConfig是public，
所以你完全可以不走runFreshen()函数，直接调用FreshenRuntimeConfig的各个参数逐个赋值也是可以的，
但是这样不好，你必须初始化该类中的所有属性，否则可能会报未初始化错误，建议走runFreshen()函数
而且FreshenConfig对于除了dataSource以外的其他属性，都是有默认值的，所以你只需要配置dataSource即可

随想：
表名映射的优先级
1. @Table注解，解析该注解，拿到定义的表名
2. 启动配置类中的tablePrefix，如果有值，则在实体类名（自动驼峰转换）前拼接上该值，
3. 前两个都没有（使用Freshen默认配置），则使用实体类名（自动驼峰转换）