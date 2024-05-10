## 项目名：Freshen
### 中文意思：
1. vi. 变得新鲜；减少盐分；使自己精神饱满
2. vt. 使清新；使新鲜；使精神焕发

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