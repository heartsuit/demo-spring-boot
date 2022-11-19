## 背景

最近接手了一个祖传项目，一个十几年前的 `.Net` 客户端项目，近期需要修改一个小功能，项目用到了 `Oracle` 数据库，以下是我在 `Windows 7` 旗舰版虚拟机上安装使用 `Oracle 11g` 的记录。

在 `Windows 7` 虚拟机上安装了 `Oracle` 服务端、客户端以及图形工具 `PL/SQL Developer` ，然后配置远程连接，在宿主机上通过 `SpringBoot` 与 `MyBatis` 集成 `Oracle` ，并进行了基本的备份与恢复操作。

## 下载Oracle

我这里根据操作系统，选择 `Windows 64` 位版本的 `Oracle` ，我这里使用迅雷下载，免登录，而且快，飞快~

* 服务端

http://download.oracle.com/otn/nt/oracle11g/112010/win64_11gR2_database_1of2.zip

http://download.oracle.com/otn/nt/oracle11g/112010/win64_11gR2_database_2of2.zip

* 客户端

http://download.oracle.com/otn/nt/oracle11g/112010/win64_11gR2_client.zip

![2022-11-19-Thunder.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-Thunder.jpg)

## 安装Oracle

* 服务端

具体安装过程可参考：[Oracle11g的安装和卸载教程](https://blog.csdn.net/zouyujie1127/article/details/22508195)

根据安装教程，对HR和SCOTT用户设置了新密码（系统默认将所有账户都锁定不可用了，除sys和system账户可用外）。安装完成后服务默认启动了，可通过 `SQL Plus` 工具测试安装效果。

![2022-11-19-SQLPlus.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-SQLPlus.jpg)

* 客户端

客户端安装时选择了默认的第一个 `InstantClient` 最小化安装。

* 图形工具PL/SQL Developer

下载地址：https://pan.baidu.com/s/1ewWtg-hQnB38C27sofA2jA

提取码：qwer

Note: 建议使用 `.msi` 安装方式。

## PL/SQL Developer连接

由于图形工具 `PL/SQL Developer` 与数据库服务在同一台主机上，所以直接连接即可。

![2022-11-19-PLSQLQuery.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-PLSQLQuery.jpg)

![2022-11-19-PLSQLLogin.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-PLSQLLogin.jpg)

## 远程连接

接下来，我想通过宿主机来访问 `Oracle` 数据库。

一、关闭防火墙
首先，分别关闭虚拟机与宿主机的防火墙，然后互相 `ping` ，网络是通的。

![2022-11-19-Ping.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-Ping.jpg)

二、测试端口

然后，测试下 `Oracle` 的默认端口 `1521` 是否可以连接： `telnet 192.168.44.153 1521` ，显然无法访问。

![2022-11-19-TelnetFail.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-TelnetFail.jpg)

在 `Oracle` 所在虚拟机上查看当前数据库的端口信息： `netstat -aon | findstr 1521` ，仅有127.0.0.1:1521，表示只能本机连接 `Oracle` 。

![2022-11-19-Port1521.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-Port1521.jpg)

三、开放服务

在虚拟机上数据库的安装目录 `E:\app\Administrator\product\11.2.0\dbhome_1\NETWORK\ADMIN` 下有两个文件： `listener.ora` 与 `tnsnames.ora`

，分别将其中的 `HOST` 改为虚拟机的IP地址。

> HOST = 192.168.44.153

![2022-11-19-ListenerConfig.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-ListenerConfig.jpg)

![2022-11-19-TnsConfig.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-TnsConfig.jpg)

`Win+R` ，键入 `services.msc` ，重启 `Oracle` 相关的两个服务： `OracleOraDb11g_home1TNSListener` 与 `OracleServiceORCL` 。

![2022-11-19-OracleService.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-OracleService.jpg)

四、远程连接

再次在 `Oracle` 所在虚拟机上查看当前数据库的端口信息，多了一个虚拟机IP，端口号为1521的进程。之后， `telnet 192.168.44.153 1521` 成功建立连接。

![2022-11-19-Port1521OK.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-Port1521OK.jpg)

## SpringBoot集成Oracle

新建 `SpringBoot` 项目，常规操作，在依赖中选择 `Web` , `Lombok` , `Oracle` ，附件 `MyBatis Plus` 。

### 核心依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.oracle.database.jdbc</groupId>
            <artifactId>ojdbc8</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>com.oracle.database.nls</groupId>
            <artifactId>orai18n</artifactId>
            <version>21.7.0.0</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

Note：

1. 其中的`com.oracle.database.nls.orai18n`依赖，是为解决报错：`不支持的字符集 (在类路径中添加 orai18n.jar): ZHS16GBK`，添加这个依赖即可。

```xml
        <dependency>
            <groupId>com.oracle.database.nls</groupId>
            <artifactId>orai18n</artifactId>
            <version>21.7.0.0</version>
        </dependency>
```

2. 此外，如果在创建项目时没有选择`Oracle`的依赖，也可以使用`Oracle`安装目录下的`jar`包作为依赖：`E:\app\Administrator\product\11.2.0\dbhome_1\jdbc\lib\ojdbc6.jar`。

* 安装到本地依赖

> mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.1.0 -Dpackaging=jar

* 在项目中添加依赖

```xml
        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc6</artifactId>
            <version>11.2.0.1.0</version>
        </dependency>
```

### 配置文件

```yaml
mybatis-plus:
  configuration:
    # 开启下划线转驼峰
    map-underscore-to-camel-case: true
    # 指定默认枚举类型的类型转换器
    default-enum-type-handler: com.baomidou.mybatisplus.extension.handlers.MybatisEnumTypeHandler
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    banner: false
    db-config:
      # 逻辑删除（软删除）
      logic-delete-value: NOW()
      logic-not-delete-value: 'NULL'
  mapper-locations: classpath:mapper/*.xml

spring:
  datasource:
    driver-class-name: oracle.jdbc.driver.OracleDriver
    url: jdbc:oracle:thin:@192.168.44.153:1521:orcl
    username: "scott"
    password: "Oracle11"
```

### 核心代码

通过集成 `MyBatis Plus` ， `Service` 、 `Mapper` 分别继承 `IService` 与 `BaseMapper` ，不贴代码了，具体见文末 `GitHub` 源码。数据表直接使用Scott用户下的 `EMP` 表。

* 实体

```java
@Data
@TableName("EMP")
public class Employee {
    @TableId
    private Integer empno;

    private String ename;
    private String job;
    private Integer mgr;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date hiredate;

    private Integer sal;
    private Integer comm;
    private Integer deptno;
}
```

* 控制器

分别写了接口测试 `Oracle` 数据库的：列表查询、新增、修改、删除、事务功能。

![2022-11-19-APIList.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-APIList.jpg)

```java
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("list")
    public List<Employee> list(){
        return employeeService.list();
    }

    @PostMapping("save")
    public boolean save(){
        Employee employee = new Employee();
        employee.setEmpno(6666);
        employee.setEname("John");
        employee.setJob("PM");
        employee.setMgr(7782);
        employee.setHiredate(new Date());
        employee.setSal(1000);
        employee.setComm(0);
        employee.setDeptno(10);

        return employeeService.save(employee);
    }

    @PutMapping("update")
    public boolean update(){
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<>();
        return employeeService.update(updateWrapper.lambda().set(Employee::getJob, "CTO").eq(Employee::getEmpno, 6666));
    }

    @DeleteMapping("delete/{id}")
    public boolean deleteByCondition(@PathVariable Integer id){
        return employeeService.removeById(id);
    }

    @DeleteMapping("deleteByCondition")
    public boolean deleteByCondition(){
        return employeeService.remove(new QueryWrapper<Employee>().lambda().eq(Employee::getEmpno, 6666));
    }

    @PostMapping("saveTransaction")
    @Transactional
    public boolean saveWithTransaction(){
        Employee employee = new Employee();
        employee.setEmpno(7777);
        employee.setEname("Wick");
        employee.setJob("CEO");
        employee.setMgr(7782);
        employee.setHiredate(new Date());
        employee.setSal(1000);
        employee.setComm(0);
        employee.setDeptno(10);
        employeeService.save(employee);

        // Exception
        int x = 1/0;

        employee.setEmpno(8888);
        employee.setEname("Tada");
        employee.setJob("CFO");
        employee.setMgr(7782);
        employee.setHiredate(new Date());
        employee.setSal(1000);
        employee.setComm(0);
        employee.setDeptno(10);

        return employeeService.save(employee);
    }
}
```

## 用户

在进行备份及恢复操作时，需要具有管理员权限，因此先了解下用户的。

| 用户名 / 密码               | 登录身份        | 说明 |
| ------------------ | ---------- | --------------------- |
| sys/change_on_install | 	SYSDBA 或 SYSOPER 	| 不能以 NORMAL 登录，可作为默认的系统管理员| 
| system/manager | 	SYSDBA 或 NORMAL|  	不能以 SYSOPER 登录，可作为默认的系统管理员| 
| sysman/oem_temp | 	  	 | sysman 为 oms 的用户名| 
| scott/tiger | 	NORMAL 	| 普通用户| 
| aqadm /aqadm | 	SYSDBA 或 NORMAL 	| 高级队列管理员| 
| Dbsnmp/dbsnmp | 	SYSDBA 或 NORMAL 	| 复制管理员sysman 为 oms 的用户名| 
| scott/tiger | 	NORMAL 	普通用户|
| aqadm /aqadm | 	SYSDBA 或 NORMAL 	| 高级队列管理员| 
| Dbsnmp/dbsnmp | 	SYSDBA 或 NORMAL 	| 复制管理员| 

* 查看被锁的用户

> select LOCK_DATE, username from dba_users;

Note： `LOCK_DATE` 为空说明没有锁定，非空为锁定。

* 解锁方法

> ALTER USER USER_NAME ACCOUNT UNLOCK;

## 备份恢复

可通过工具或者命令的方式进行数据表的导入、导出，即备份与恢复。

采用图形工具 `PL/SQL Developer` 的方式进行导出，见下图。

![2022-11-19-ExportTool.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-ExportTool.jpg)

### 备份

这里以将数据库中scott用户与hr用户的表导出为例说明Oracle数据库备份的命令。

> exp system/manager@orcl file=E:\backup\data.dmp owner=(scott, hr)

我在执行上述命令时遇到以下错误。

* 报错信息：

EXP-00056: 遇到 ORACLE 错误 1017
ORA-01017: invalid username/password; logon denied

* 解决方案：

在提示的用户名处，输入 `system as sysdba` ，再输入口令即可。

用户名: system as sysdba
口令:

![2022-11-19-ExportCmd.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-11-19-ExportCmd.jpg)

### 恢复

将上述导出的数据进行还原。

> imp system/manager@orcl file=E:\backup\data.dmp full=y ignore=y

* 报错信息：

IMP-00031: 必须指定 FULL=Y 或提供 FROMUSER/TOUSER 或 TABLES 参数

* 解决方案：

添加full=y参数。

* 报错信息：

IMP-00015: 由于对象已存在, 下列语句失败

* 解决方案：

添加ignore=y参数。

更多的备份与恢复操作可参考[Oracle数据库备份与还原命令](https://blog.csdn.net/weixin_42290280/article/details/93725795)

此外，除了EXP和IMP这种备份还原方式，从Oracle 10g开始提供了称为数据泵的新工具expdp/impdp，它为Oracle数据提供高速并行及大数据的迁移，如有大量数据需要备份可以做进一步探索。

Note：

1. EXP和IMP是客户端工具程序，它们既可以在客户端使用，也可以在服务端使用。
2. EXPDP和IMPDP是服务端的工具程序，他们只能在ORACLE服务端使用，不能在客户端使用。
3. IMP只适用于EXP导出的文件，不适用于EXPDP导出文件；IMPDP只适用于EXPDP导出的文件，而不适用于EXP导出文件。

## Source Code

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-mybatisplus-oracle)
