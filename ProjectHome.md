## SmartRCP是什么 ##

> SmartRCP是一个使用flex来写eclipse rcp 桌面程序的eclipse开源插件。

它继承了java与flex的优势，并扬长避短，将它们的优势发挥得淋漓尽致。使用了SmartRCP后，您可以用flex的mxml快速构建出漂亮的程序界面，用java来处理复杂的业务逻辑，使界面显示层与业务逻辑层相分离，实现了桌面程序的分层设计。

> 从此以后界面与美工可以专注于flex的界面开发部分，Java开发人员专注于业务逻辑部分的开发。

## SmartRCP的目标 ##

  * 做开发便捷高效的桌面程序开发平台
  * 平台的高扩展性

## SmartRCP的特点 ##

  1. 完全使用flex做界面开发，充分发挥flex界面开发的便捷与高效优势，提高了工作效率。
  1. 使用flex大量的GUI库可以设计出漂亮的界面。
  1. flex可以轻松调用所有java运行库与API，使flex的功能更加强大。
  1. flex与java之间的事件通信接口，为多线程与复杂任务的处理提供了支持。
  1. java负责业务逻辑，flex负责界面展示，充分发挥各自的优势，使程序的逻辑与开发流程更加清晰。
  1. 与flex builder IDE完全融合，界面的设计与程序的debug工作轻松搞定。

## SmartRCP的比较优势 ##

  * 比SWT开发更快捷
  * 比MFC，WinForm更容易上手，开发更方便

## SmartRCP开发计划 ##
1：稳定和完善cn.smartinvoke.smartrcp.core这个eclipse plugIn
> 它是SmartRCP的核心，就更osgi之余eclipse一样。

2：丰富SmartRCP的调用库。SmartRCP的库分两部分，第一部分是Java
> 的服务类部分，第二部分是对于的Flex代理类部分。
> 当然Flex的代理类部分很简单，可以用工具自动生成，然后稍微改动下
> 就可以了

> 比如文件访问库；网络套接字库；数据库访问库。

> 这些库都可以作为独立项目来开展。

3：将SmartRCP做成一个浏览平台将SmartRCP应用程序像网页一样加载和使用。


## 使用SmartRCP出现问题怎么办？ ##

  1. 加入QQ群109914128寻找答案
  1. 加入[讨论区](https://groups.google.com/group/smartrcp)，讨论组一般总是有人在线，你的问题会很快得到响应

## 源代码说明 ##

cn.smartinvoke.smartrcp.core

是smartrcp的核心，所有功能都是在这个插件中实现的

cn.smartinvoke.smartrcp

是smartrcp的壳，它的作用就是将cn.smartinvoke.smartrcp.core插件与程序需要
的插件与库打包成一个可以双击运行的RCP程序

cn.smartinvoke.smartrcp-flex

SmartRCP的Flex部分代码