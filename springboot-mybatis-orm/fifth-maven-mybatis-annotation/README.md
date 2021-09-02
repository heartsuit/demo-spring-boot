## MyBatis总体流程

    加载配置：MyBatis 应用程序根据 XML 配置文件加载运行环境，创建 SqlSessionFactory，SqlSessionFactory，配置来源于两个地方，一处是配置文件，一处是 Java 代码的注解，将 SQL 的配置信息加载成为一个个 MappedStatement 对象（包括了传入参数映射配置、执行的 SQL 语句、结果映射配置），存储在内存中。

    SQL 解析：当 API 接口层接收到调用请求时，会接收到传入 SQL 的 ID 和传入对象（可以是 Map、JavaBean 或者基本数据类型），Mybatis 会根据 SQL 的 ID 找到对应的 MappedStatement，然后根据传入参数对象对 MappedStatement 进行解析，解析后可以得到最终要执行的 SQL 语句和参数。

    SQL 执行：SqlSession 将最终得到的 SQL 和参数拿到数据库进行执行，得到操作数据库的结果。

    结果映射：将操作数据库的结果按照映射的配置进行转换，可以转换成 HashMap、JavaBean 或者基本数据类型，并将最终结果返回，用完之后关闭 SqlSession。

每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。SqlSessionFactory 是单个数据库映射关系经过编译后的内存映像。SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先定制的 Configuration 的实例构建出 SqlSessionFactory 的实例。SqlSessionFactory 是创建 SqlSession 的工厂。

SqlSession 是执行持久化操作的对象，它完全包含了面向数据库执行 SQL 命令所需的所有方法，可以通过 SqlSession 实例来直接执行已映射的 SQL 语句。在使用完 SqlSession 后我们应该使用 finally 块来确保关闭它。

## MyBatis 配置文件的 configuration 标签主要包括：

    configuration 配置
    properties 属性
    settings 设置
    typeAliases 类型命名
    typeHandlers 类型处理器
    objectFactory 对象工厂
    plugins 插件
    environments 环境
        environment 环境变量
        transactionManager 事务管理器
    databaseIdProvider 数据库厂商标识
    mappers 映射器

## resultMap
resultMap 的子元素包括：
   
       constructor：用来将结果注入到一个实例化好的类的构造方法中
       idArg： ID 参数，标记结果作为 ID
       arg：注入到构造方法的一个普通结果
       id： 一个 ID 结果，标记结果作为 ID
       result：注入到字段或 JavaBean 属性的普通结果
       association：复杂的类型关联，多个结果合成的类型
       嵌入结果映射：结果映射自身的关联，也可以引用一个外部结果映射
       collection：复杂类型的集 也可以引用一个外部结果映射
       discriminator：使用结果值来决定使用哪个结果集
       case：基本一些值的结果映射
           也可以引用一个外部结果映射
   
   resultMap 的属性包括：
   
       id : 当前命名空间中的一个唯一标识，用于标识一个 resultMap
       type：类的全限定名，或者一个类型别名
       autoMapping：为这个 ResultMap 开启或者关闭自动映射，该属性会覆盖全局的属性 autoMappingBehavior。默认值为：unset
