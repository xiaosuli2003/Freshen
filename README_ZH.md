<h4 align="right"><a href="./README.md">English</a> | <strong>简体中文</strong></h4>

# Freshen：一个基于JDBC的kotlinDSL风格的库，但是玩具（正在开发中）

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

<hr/>

## 特征
<hr/>

#### 1. 很轻量
> Freshen只依赖了Kotlin的反射库，无其他任何第三方依赖。

#### 2. 只增强
> Freshen只对JDBC进行封装，支持CRUD、分页查询、批量操作。

#### 3. 更灵动
> Freshen支持数据自动映射（可修改默认行为）、设置表名统一前缀、逻辑删除、乐观锁、驼峰自动转下划线、事务管理、自动生成主键（感谢Mybatis-Flex）、SQL审计等特性。

## 开始
<hr/>

- [快速开始](https://xiaosuli.cn)
- 示例 1：[Freshen 原生（非 Spring）](./freshen-test/freshen-native-test)
- 示例 2：[Freshen with Spring](./freshen-test/freshen-spring-test)
- 示例 3：[Freshen with Spring boot](./freshen-test/freshen-spring-boot-test)

## hello world（原生）

**第 1 步：编写 Entity 实体类**

```kotlin

```

**第 2 步：开始查询数据**

示例 1：查询1条数据

```kotlin

```

示例 2：查询列表

```kotlin

```

示例 3：分页查询

```kotlin

```

## QueryDSL示例

### select *

```kotlin

```
### select columns

```kotlin

```

使用`as`：
```kotlin

```

### select functions

```kotlin

```

### where
```java

```

## 更多文档
<hr/>

- [Freshen](https://xiaosuli.cn)

## License
<hr/>
Freshen is Open Source software released under the Apache 2.0 license.