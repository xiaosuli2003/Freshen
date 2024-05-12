## 项目名：Freshen
### 中文意思：
1. vi. 变得新鲜；减少盐分；使自己精神饱满
2. vt. 使清新；使新鲜；使精神焕发

### todo:
1. 启动类支持配置文件方式启动Freshen
2. where条件的非聚合函数
3. where和having的条件拼接（难题）
4. 反射和考虑@Id，@Column注解
5. 多表支持，form两个及以上表，join系列，union、union all、union distinct系列
6. 嵌套查询，子查询
7. 目前select只考虑了单表情况，如果要多表，QueryAsBuilder需要重写
8. 虽然搞了prepareStatement，但是没有使用，因为参数在sql中写死了（涉及limit，where，having）
9. 对于`where(方法链式调用)`生成的sql优先级默认为书写顺序（需要优化）
10. 对于`where(lambda表达式)`生成的sql有问题，由于lambda默认返回最后一行代码，所以内嵌多个lambda，只有最后一个拼接进去，需要处理。

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