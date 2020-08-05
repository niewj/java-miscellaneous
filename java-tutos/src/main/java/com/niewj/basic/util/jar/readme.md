## 0. jar文件
- 可以包含类文件，也可以包含图像声音等文件
- jar是压缩的，使用了zip压缩技术

```jar cvf jar文件名 文件1 文件2 文件3```
例如：```jar cvf calc.jar *.class icon.gif```

- c 创建新文档
- e 在清单文件加一个entry
- f 将jar文件名指定为第二个命令行参数，不指定的话，jar命令会将结果写到标准输出而不是文件
- m 加一个manifest到jar文件
- v 生成详细输出

## menifest清单文件
```MENIFEST.MF```

```
# main section
Menifest-Version: 1.0

# entry name properties
Name: A.class
this is  a class named A

Name: com/niewj/test
this is a package named ...

```
## 1. jar包制作
### 1.1 编译并打包成可执行的jar:
UnCheckedExceptionTest.java，文件除了这个类外还包含一个MyBox2的class
### 1.2 编译带包的java类文件：
```javac com/niewj/test/UnCheckedExceptionTest.java```
### 1.3 打包jar的命令：
```jar cvfe myapp.jar com.niewj.test.UnCheckedExceptionTest *```    
- 加入一个e参数, 指定程序的条目点   
- 类名后不需要.class
### 1.4 执行可执行的jar包：
```java -jar myapp.jar```即可