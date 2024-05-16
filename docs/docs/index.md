---
layout: home

hero:
  name: 'Freshen'
  text: '一个基于JDBC的kotlinDSL风格的库，但是玩具'
  tagline: '两行代码就可以查询表所有数据'
  image:
    src: /xiaosuli.svg
    alt: Freshen
  actions:
    - theme: brand
      text: 什么是Freshen?
      link: /intro/what-is-freshen
    - theme: alt
      text: 快速开始
      link: /intro/getting-started
    - theme: alt
      text: GitHub
      link: https://github.com/xiaosuli2003/Freshen

features:
  - title: 很轻量
    details: Freshen只依赖了Kotlin的反射库、协程库，再无其他任何第三方依赖。
  - title: 只增强
    details: Freshen只对JDBC进行封装，支持CRUD、分页查询、批量操作。
  - title: KotlinDSL风格
    details: Freshen所有CRUD函数均使用Kotlin的DSL风格，让你就像写SQL一样写代码，SQL怎么写，代码就怎么写，无任何学习成本。
---
