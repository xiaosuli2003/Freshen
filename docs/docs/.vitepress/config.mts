import {defineConfig} from 'vitepress'

export default defineConfig({
  title: 'Freshen',
  description: '一个基于JDBC的kotlinDSL风格的库，但是玩具',
  head: [['link', {rel: 'icon', href: '/favicon.ico'}]],
  lang: 'zh-CN',
  lastUpdated: true,

  themeConfig: {
    logo: '/xiaosuli.svg',
    outline: {level: 2, label: '页面导航'},
    nav: [{text: '指南', link: '/intro/what-is-freshen'}],

    sidebar: [
      {
        text: '简介',
        items: [
          {text: 'Freshen是什么', link: '/intro/what-is-freshen'},
          {text: '快速开始', link: '/intro/getting-started'},
          {text: '致谢', link: '/intro/thanks'},
          {text: '优势？劣势！', link: '/intro/good-and-bad'},
          {text: '支持的数据库', link: '/intro/support-database'},
          {text: '待办事项清单', link: '/intro/to-do-list'},
        ],
      },
      {
        text: '基础功能',
        items: [
          {text: '增', link: '/base/insert'},
          {text: '删', link: '/base/delete'},
          {text: '改', link: '/base/update'},
          {text: '查', link: '/base/query'},
          {text: '自动映射', link: '/base/auto-mapping'},
        ],
      },
      {
        text: '更多功能',
        items: [
          {text: 'IRepository', link: '/more/repository'},
          {text: '@Table注解', link: '/more/table'},
          {text: '@Id注解', link: '/more/id'},
          {text: '@Column注解', link: '/more/column'},
          {text: '逻辑删除', link: '/more/logic-delete'},
          {text: '乐观锁', link: '/more/version'},
          {text: 'SQL打印', link: '/more/sql-print'},
        ],
      },
    ],

    search: {
      provider: 'local'
    },

    socialLinks: [
      {
        icon: {
          svg: '<svg t="1708681657268" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1437" width="300" height="300"><path d="M512 512m-494.933333 0a494.933333 494.933333 0 1 0 989.866666 0 494.933333 494.933333 0 1 0-989.866666 0Z" fill="#C71D23" p-id="1438"></path><path d="M762.538667 457.045333h-281.088a24.4736 24.4736 0 0 0-24.439467 24.405334v61.098666c-0.034133 13.5168 10.922667 24.439467 24.405333 24.439467h171.1104c13.5168 0 24.439467 10.922667 24.439467 24.439467v12.219733a73.3184 73.3184 0 0 1-73.3184 73.3184h-232.209067a24.439467 24.439467 0 0 1-24.439466-24.439467v-232.174933a73.3184 73.3184 0 0 1 73.3184-73.3184h342.152533c13.482667 0 24.405333-10.922667 24.439467-24.439467l0.034133-61.098666a24.405333 24.405333 0 0 0-24.405333-24.439467H420.352a183.296 183.296 0 0 0-183.296 183.296V762.538667c0 13.482667 10.922667 24.439467 24.405333 24.439466h360.516267a164.9664 164.9664 0 0 0 165.000533-165.000533v-140.526933a24.439467 24.439467 0 0 0-24.439466-24.439467z" fill="#FFFFFF" p-id="1439"></path></svg>'
        },
        link: 'https://gitee.com/xiaosuli2003/Freshen',
      },
      {icon: 'github', link: 'https://github.com/xiaosuli2003/Freshen'},
    ],

    footer: {
      message: 'Released under the <a href="http://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache 2.0 license</a>',
      copyright: 'Copyright © 2023-present <a href="https://github.com/xiaosuli2003">xiaosuli</a>'
    }
  },
})
