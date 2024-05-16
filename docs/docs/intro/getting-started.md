# 快速开始

## 环境要求
- [JDK 1.8+](https://www.oracle.com/cn/java/technologies/downloads/#java8)
- [IntelliJ IDEA](https://www.jetbrains.com.cn/idea/download/?section=windows) 或其他支持Kotlin语言的IDE，如 [IntelliJ IDEA Community](https://www.jetbrains.com.cn/idea/download/?section=windows) 或 [Fleet](https://www.jetbrains.com.cn/fleet/download/#section=windows)
- [Maven](https://maven.apache.org/download.cgi) 或 [Gradle](https://gradle.org/releases/)

## 在开始之前，我们假定您已经：
- 熟悉 Kotlin 语言
- 熟悉 MySQL 数据库
- 熟悉 Maven 或 Gradle 构建工具

## hello world 

**第 1 步：创建数据库表**
```sql
create table student
(
    id           tinyint auto_increment
        primary key,
    name         varchar(255) not null,
    gender       varchar(1)   not null,
    birthday     datetime     not null,
    phone_number varchar(11)  not null,
    address      varchar(255) null
);
```

**第 2 步：创建 Kotlin 项目，并添加 Maven / Gradle 依赖**

- Gradle(Kotlin)依赖如下：
```kotlin
implementation("cn.xiaosuli:freshen-core:0.1.0-alpha")
```
- Gradle(Groovy)依赖如下：
```groovy
implementation 'cn.xiaosuli:freshen-core:0.1.0-alpha'
```
- Maven依赖如下：
```xml
<dependency>
    <groupId>cn.xiaosuli</groupId>
    <artifactId>freshen-core</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

**第 3 步：Freshen 启动！**
- 这里使用Druid连接池，如果你不想使用Druid，可以替换成其他连接池，只要该数据源可以返回JDBC标准的`javax.sql.DataSource`即可。
```kotlin
implementation("com.alibaba:druid:1.2.22")
```
- 创建一个数据源，并在main函数中调用runFreshen函数，建议在main函数第一行就调用runFreshen函数。
```kotlin
fun main() {
    runFreshen(getDruidDataSource())
}

private fun getDruidDataSource(): DataSource = DruidDataSource().apply {
    driverClassName = "com.mysql.cj.jdbc.Driver"
    url = "jdbc:mysql://localhost:3306/db_freshen?serverTimezone=Asia/Shanghai"
    username = "root"
    password = "******"
}
```

**第 4 步：编写 Entity 实体类**

```kotlin
data class Student(
    val id: Long,
    val name: String,
    val gender: String,
    val birthday: LocalDateTime,
    val phoneNumber: String,
    val address: String? = null
)
```

**第 5 步：开始查询数据**

在Main函数添加以下代码：
```kotlin
query<Student>().forEach(::println)
```
控制台输出：
```kotlin
Student(id=1, name=张三, gender=男, birthday=2024-05-09T22:36:54, phoneNumber=17745654321, address=浙江省杭州市)
Student(id=2, name=李四, gender=男, birthday=2024-05-09T22:36:57, phoneNumber=13245657274, address=广东省广州市)
```