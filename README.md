<h4 align="right"><a href="./README.md">English</a> | <strong>简体中文</strong></h4>

# Freshen：一个基于JDBC的kotlinDSL风格的库，但是玩具

<p>
    <a target="_blank" href="https://search.maven.org/search?q=freshen">
        <img src="https://img.shields.io/badge/Maven%20Central-v0.1.0-blue" alt="Maven Central" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">
		<img src="https://img.shields.io/badge/License-Apache2.0-blue" alt="Apache-2.0" />
	</a>
    <br/>
    <a target="_blank" href="https://gradle.org/releases/">
		<img src="https://img.shields.io/badge/Gradle-8.5-%235a966c?logo=java" alt="Gradle" />
	</a>
    <br/>
    <a target="_blank" href="https://kotlinlang.org/">
		<img src="https://img.shields.io/badge/Kotlin-1.9.24-%237f52ff" alt="kotlin" />
	</a>
    <a target="_blank" href="https://www.oracle.com/cn/java/technologies/downloads/#java21">
		<img src="https://img.shields.io/badge/JDK-21-%23c74634?logo=java" alt="jdk-21" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/downloads/#java11">
		<img src="https://img.shields.io/badge/JDK-17-%23c74634?logo=java" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/downloads/#java11">
		<img src="https://img.shields.io/badge/JDK-11-%23c14d3d" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/downloads/#java8">
		<img src="https://img.shields.io/badge/JDK-8-%23c74634?logo=java" alt="jdk-8" />
	</a>
    <br/>
    <a target="_blank" href='https://spring.io/projects/spring-boot#learn'>
		<img src='https://img.shields.io/badge/SpringBoot-v2.x-%236cb52d' alt='SpringBoot v2.x'/>
	</a>
    <a target="_blank" href='https://spring.io/projects/spring-boot#learn'>
		<img src="https://img.shields.io/badge/SpringBoot-v3.x-%236cb52d" alt="SpringBoot v3.x"/>
	</a>
</p>

### Freshen 中文意思：
> 1. vi. 变得新鲜；减少盐分；使自己精神饱满
> 2. vt. 使清新；使新鲜；使精神焕发
<p>

#### Q: 为什么起这个名字呢？
<p><b>A: </b>开始写这个项目的时候，一些配置类就要定和项目名相关的名字，例如`XXXConfig`，因为我比较喜欢清新自然这些词汇，就用翻译软件查相关的单词，就找到了这个单词，一看中文意思还不错，读起来也不难，所以就起名Freshen了。

### 致谢:

Freshen感谢以下项目:
- [Mybatis-Flex](https://github.com/mybatis-flex/mybatis-flex)，特别是flexId和snowFlakeId这两个ID生成器。
- [Mybatis-Flex-Kotlin](https://github.com/mybatis-flex/mybatis-flex)，特别是DbExtensions.kt文件。

[Mybatis-Flex](https://github.com/mybatis-flex/mybatis-flex)和[Mybatis-Flex-Kotlin](https://github.com/mybatis-flex/mybatis-flex)在Apache许可证2.0版的条款下使用。

### 先说缺点

#### 1. 只支持MySQL数据库
> 不要问我为什么只支持MySQL数据库，因为本人没学过其他数据库。

#### 2. 基于JVM，但又不支持Java
> 由于本项目基于Kotlin反射库编写，还大量使用了Kotlin的语法糖，比如内联函数、成员扩展函数等，而且Kotlin的扩展函数，在Java中得用`XXXKt.xxx(receiver, args)`来调用，这样写出来的代码就很丑，我一开始写的时候就没考虑去支持Java，就全部按照Kotlin的风格去写的，所以不支持Java，后面我有可能会尽量去支持Java，但是不要抱希望。

#### 3.有批量添加，但是伪批量
> 写这个项目之前，我对JDBC的各种高级功能一无所知，根本不知道JDBC是支持批量操作的，我代码写完了才知道有这玩意。所以目前只支持伪批量，也就是在复用同一个connection，然后开启事务，循环执行insert语句，等我，等我去B站尚硅谷进修一下，下个版本就改。

### 优点

#### 1. 很轻量
> Freshen只依赖了Kotlin的反射库、协程库，再无其他任何第三方依赖。

#### 2. 只增强
> Freshen只对JDBC进行封装，支持CRUD、分页查询、批量操作。

#### 3. KotlinDSL风格
> Freshen所有CRUD函数均使用Kotlin的DSL风格，让你就像写SQL一样写代码，SQL怎么写，代码就怎么写，无任何学习成本。

#### 4. 更多功能
> Freshen支持数据自动映射（可修改默认行为）、设置表名统一前缀、逻辑删除、乐观锁（正在新建文件夹）、驼峰自动转下划线、事务管理、自动生成主键（感谢Mybatis-Flex）、SQL打印等特性。

### 开始

- [快速开始](https://xiaosuli.cn)
- 示例 1：[Freshen 原生（非 Spring）](./freshen-test/freshen-native-test)
- 示例 2：[Freshen with Spring](./freshen-test/freshen-spring-test)
- 示例 3：[Freshen with Spring boot](./freshen-test/freshen-spring-boot-test)

## **hello world（原生）**

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

示例 1：查询列表

```kotlin
val studentList:List<Student> = query<Student>()
```

示例 2：查询1条数据

```kotlin
// 默认查询数据库第一条数据
val student:Student? = queryOne<Student>()
```

示例 3：分页查询

```kotlin
// 此处page为Freshen定义的实体类，具体详见源码
val page: Page<Student> = paginate<Student>(1, 1)
```

## QueryDSL示例

### select *
```kotlin
// SQL: select * from student
query<Student>()
```

### select columns
```kotlin
// SQL: select id, name from student
query<Student>{
    select(Student::id,Student::name)
}
// SQL: select id, name, gender, birthday, phone_number, address from student
query<Student>{
    select(Student::class.all)
}
```

### 使用`as`
```kotlin
// SQL: select id, name as stuName from student
// 方式一：直接调用`as`方法
queryAs<Student,Student>{
    select(Student::id,Student::name.`as`("stuName"))
}
// 方式一：使用 XX `as` XX这样的语法（得益于Kt的中缀表达式语法糖） 
queryAs<Student,Student>{
    select(Student::id,Student::name `as` "stuName")
}
```

### select functions

```kotlin
// SQL: select max(birthday) from tb_student
queryAs<Student,Student>{
    select(max(Student::birthday))
}
```

### where
```kotlin
// SQL select * from student where name like '%xiaosuli%'
// 方式一：where()
queryAs<Student, Student> {
    where(Student::name like "%xiaosuli")
}
// 方式二：where{}
queryAs<Student,Student>{
    where{
        Student::name like "%xiaosuli"
    }
}
```
两种方式区别，方式一不会给条件加括号，方式二会加括号，这个{}就相当于括号，可以提高优先级。

## 更多文档
- [Freshen文档](https://xiaosuli.cn)

## License
Freshen is Open Source software released under the Apache 2.0 license.