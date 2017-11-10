### GreenDao简记

#### 一、简介

**简单的讲**，GreenDAO 是一个将对象映射到 SQLite 数据库中的轻量且快速的 ORM 解决方案。

#### 二、ORM概念

**简单的讲**，就是JavaBean和我们的数据库进行一个关系映射，一个实例对象对应数据库的一条记录，每个对象的属性则对应着数据库表的字段。

#### 三、GreenDao自动生成

>Master

Master里面包含了数据库表单的创建以及删除，和升级迁移

>Seesion

Session里面拿到green封装的一个数据库操作session，通过这个可以进行sql操作

>BeanDao

BeanDao是sql语句的封装类

#### 四、二次封装

根据情景，进行封装成manager和utils



###### 参考

http://blog.csdn.net/bskfnvjtlyzmv867/article/details/71250101