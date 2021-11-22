# javamesh-samples

## 概述

`javamesh-samples`是`JavaMesh`的样品模块，内含各种功能的插件及其附加件

## 内容列表

- [概述](#概述)
- [背景](#背景)
- [组成部分](#组成部分)
- [打包步骤](#打包步骤)
- [开发环境](#开发环境)
- [插件开发流程](#插件开发流程)
- [附加件开发流程](#附加件开发流程)

## 背景

`javamesh-samples`模块的前身是`plugins`模块，原只用于存放自定义插件，考虑到部分插件可能需要将数据传递到服务器进行处理，因此对`plugins`模块进行整理，现在的`javamesh-samples`模块现在可以存放自定义插件和相应的附加件。

## 组成部分

`javamesh-samples`按照`功能(function)`划分子模块，每个功能子模块中可能包含以下子模块：

- `插件(plugin)`，该模块主要用于声明对宿主应用的增强逻辑
- `服务(service)`，用于为插件包提供服务实现
- `后端(server)`，用于接收插件数据的服务端
- `前端(webapp)`，用于对服务端数据作前端展示
- `其他(other)`，特殊附加件，一般用作调试

`javamesh-samples`中包含以下示例功能模块：

- [javamesh-example(function)](javamesh-example): 示例功能
  - [demo-plugin(plugin)](javamesh-example/demo-plugin): 示例插件
  - [demo-service(service)](javamesh-example/demo-service): 示例插件服务
  - [demo-application(other)](javamesh-example/demo-application): 示例宿主应用，测试用，不参与打包

## 打包步骤

目前[JavaMesh](../pom.xml)的打包过程中，包含`agent`、`ext`、`example`、`package`和`all`
6个步骤，其中与[javamesh-samples](pom.xml)相关的步骤如下：

- `agent`: 对除示例功能[javamesh-example](javamesh-example)外所有`插件(plugin)`和`服务(service)`进行打包，他们将输出到产品`agent/pluginPackage/${功能名称}`目录。
- `ext`: 对所有附加件进行打包，包括`后端(server)`、`前端(webapp)`和`其他(other)`，其中`后端(server)`和`前端(webapp)`将输出到产品的`server/${功能名称}`目录，`其他(other)`一般为调试用的附加件，没有打包要求。
- `example`: 对示例功能[javamesh-example](javamesh-example)进行打包。
- `all`: 对上述的所有内容进行打包。

## 开发环境

HuaweiJDK 1.8 / OpenJDK 1.8 / OracleJDK 1.8 或更高版本
Apache Maven 3.0.0 或更高版本

## 功能开发流程

- 添加`功能(function)`模块，依据该`功能(function)`中涉及的内容，在[javamesh-samples的pom文件](pom.xml)中的特定`profile`中添加相应模块：
  - 必须在`id`为`all`的`profile`中添加该模块。
  - 如果该模块包含`插件(plugin)`，那么需要在`id`为`agent`的`profile`中添加该模块。
  - 如果该模块包含其他内容，则需要在`id`为`ext`的`profile`中添加该模块。
- 在该模块的`pom.xml`中添加以下标签：
  ```xml
  <packaging>pom</packaging>
  ```
  ```xml
  <properties>
    <javamesh.basedir>${pom.basedir}/../../..</javamesh.basedir>
    <package.sample.name>${功能名称}</package.sample.name>
  </properties>
  ```

`功能(function)`模块的子模块开发流程参见一下章节：
- `插件(plugin)`和`服务(service)`开发流程参见[插件开发流程](#插件开发流程)
- `后端(server)`和`前端(webapp)`开发流程参见[附加件开发流程](#附加件开发流程)
- `其他(other)`由于只参与调试，不涉及开发流程限定

## 插件开发流程

本节将介绍插件开发的相关流程，其中涉及的模块为`插件(plugin)`和`服务(service)`。

本节内容如下：

- [添加插件模块](#添加插件模块)
- [添加插件服务模块](#添加插件服务模块)
- [添加配置](#添加配置)
- [示例工程解读](#示例工程解读)
- [注意事项](#注意事项)
- [打包流程](#打包流程)

### 添加插件模块

结合[打包步骤](#打包步骤)中介绍的步骤，与插件开发相关的步骤有`agent`和`all`两个`profile`。如果需要为`功能(function)`模块添加`插件(plugin)`子模块：

- 在`功能(function)`模块的`pom.xml`文件的以下`profile`中添加`module`：
  ```xml
  <profiles>
    <profile>
      <id>agent</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <modules>
        <module>${插件模块名}</module>
      </modules>
    </profile>
    <profile>
      <id>all</id>
      <modules>
        <module>${插件模块名}</module>
      </modules>
    </profile>
  </profiles>
  ```
- 为`插件(plugin)`子模块添加以下参数：
  ```xml
  <properties>
    <package.sample.type>plugin</package.sample.type>
  </properties>
  ```
- 为`插件(plugin)`子模块添加核心包依赖：
  ```xml
  <dependencies>
    <dependency>
      <groupId>com.huawei.javamesh</groupId>
      <artifactId>javamesh-agentcore-core</artifactId>
      <scope>provided</scope>
    </dependency>
  </dependencies>
  ```
  注意，不建议为`插件(plugin)`模块添加或使用其他第三方依赖！
- 为`插件(plugin)`子模块添加`shade`插件，以修正`byte-buddy`依赖的全限定名：
  ```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
  ```

`插件(plugin)`模块的定位是定义宿主应用的增强逻辑，考虑到依赖冲突的问题，其增强的字节码中不能涉及对第三方依赖的使用，这时就需要分两种情况讨论：

- 对于简单的插件，其中编写的插件服务只会使用核心包中的自研功能，不涉及其他需要依赖第三方依赖的复杂功能时，那么只需要开发`插件(plugin)`模块即可。
- 对于一些复杂的插件，如果涉及需要依赖第三方依赖的复杂功能时，就需要设计服务接口，并编写`服务(service)`模块予以实现。

### 添加插件服务模块

和`插件(plugin)`模块相似，添加`服务(service)`模块步骤如下：

- 在`功能(function)`模块的`pom.xml`文件的以下`profile`中添加`module`：
  ```xml
  <profiles>
    <profile>
      <id>agent</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <modules>
        <module>${插件服务模块名}</module>
      </modules>
    </profile>
    <profile>
      <id>all</id>
      <modules>
        <module>${插件服务模块名}</module>
      </modules>
    </profile>
  </profiles>
  ```
- 为`服务(service)`子模块添加以下参数：
  ```xml
  <properties>
    <package.sample.type>service</package.sample.type>
  </properties>
  ```
- 为`服务(service)`子模块添加核心包和相关插件包的依赖：
  ```xml
  <dependencies>
    <dependency>
      <groupId>com.huawei.javamesh</groupId>
      <artifactId>javamesh-agentcore-core</artifactId>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>com.huawei.javamesh</groupId>
      <artifactId>${插件模块名}</artifactId>
      <version>${插件模块版本}</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
  ```
  `服务(service)`中允许按需添加第三方依赖！
- 为`服务(service)`子模块添加`shade`插件(或其他打包插件)打包：
  ```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
  ```

### 添加配置

如果`插件(plugin)`和`服务(service)`模块中需要使用插件配置`PluginConfig`，那么需要添加插件的配置文件：

- 在`功能(function)`模块的主目录下添加`config`文件夹。
- 在`config`文件夹中添加一个`config.yaml`文件，这就是插件的配置文件。
- 在`功能(function)`模块的任意子模块(一般取第一个子模块)中添加以下配置以启动资源拷贝：
  ```xml
  <properties>
    <config.skip.flag>false</config.skip.flag>
  </properties>
  ```
  如果配置上述参数的子模块不在`功能(function)`模块的目录下，还需要根据该模块与`config`目录的相对位置设置以下参数：
  ```xml
  <properties>
    <config.source.dir>../config</config.source.dir>
  </properties>
  ```

### 示例工程解读

`javamesh-example`模块是一个示例功能模块，其中涉及了大部分开发插件时可能碰到的场景，本节将会对该模块的内容进行解读，以帮助读者能尽快上手开发插件功能。

- 增强定义示例，见于[definition](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/definition)包：
  - [DemoAnnotationDefinition](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/definition/DemoAnnotationDefinition.java)展示如何通过修饰类的注解定位到被增强的类。
  - [DemoNameDefinition](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/definition/DemoNameDefinition.java)展示如何通过名称定位到被增强的类。
  - [DemoSuperTypeDefinition](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/definition/DemoSuperTypeDefinition.java)展示如何通过超类定位到被增强的类。
  - 上述三者都可以看出如何声明用于增强构造函数、静态函数和实例函数的拦截器。
  - 需要添加[EnhanceDefinition](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/agent/definition/EnhanceDefinition.java)的[spi配置文件](javamesh-example/demo-plugin/src/main/resources/META-INF/services/com.huawei.apm.core.agent.definition.EnhanceDefinition)。
- 拦截器示例，见于[interceptor](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/interceptor)包：
  - [DemoConstInterceptor](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/interceptor/DemoConstInterceptor.java)展示了如何编写一个用于增强构造函数的拦截器。
  - [DemoStaticInterceptor](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/interceptor/DemoStaticInterceptor.java)展示了如何编写一个用于增强静态函数的拦截器。
  - [DemoInstInterceptor](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/interceptor/DemoInstInterceptor.java)展示了如何编写一个用于增强实例函数的拦截器。
- 日志系统使用示例：
  - [DemoLoggerInterceptor](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/interceptor/DemoLoggerInterceptor.java)展示了如何在拦截器中使用日志系统。
  - [DemoSimpleService](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/service/DemoSimpleService.java)中展示了如何在插件服务中使用日志系统。
- 心跳功能使用示例，如[DemoHeartBeatService](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/service/DemoHeartBeatService.java)服务所示，通常编写插件服务注册即可。
- 链路功能使用示例，如[DemoTraceInterceptor](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/interceptor/DemoTraceInterceptor.java)所示，通常链路功能应用于拦截器，对宿主应用的方法调用过程进行增强，捕获其相关的数据信息并上报。
- 增强原生类示例，如[DemoBootstrapDefinition](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/definition/DemoBootstrapDefinition.java)所示，与普通类的增强方式无异。但是，考虑到修改原生类是非常危险的且风险扩散的操作，不建议对原生类进行增强。
- 插件配置示例：插件配置是统一配置系统的特化，遵循统一配置系统的规则。[config.yaml](javamesh-example/config/config.yaml)是示例工程的配置文件，其中包含[DemoConfig](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/config/DemoConfig.java)和[DemoServiceConfig](javamesh-example/demo-service/src/main/java/com/huawei/example/demo/config/DemoServiceConfig.java)两个配置类对应的配置信息，从配置的定义和调用可以看到：
  - 每个功能的配置文件仅能有1个，即`config.yaml`文件。
  - 插件包的配置类如果有拦截器别名的设定，可以继承[AliaConfig](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/config/AliaConfig.java)类，其他情况都实现[PluginConfig](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/config/PluginConfig.java)接口。
  - 无论继承哪个类，都需要添加[PluginConfig](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/config/PluginConfig.java)的[spi配置文件](javamesh-example/demo-plugin/src/main/resources/META-INF/services/com.huawei.apm.core.plugin.config.PluginConfig)。
  - 配置类的属性类型可以是布尔、数字、字符串、枚举、复杂对象，以及他们的数组、列表和字典类型。
  - 普通字符串和复杂对象的字符串字段支持`${key:default}`风格的映射，优先级如下：
    ```
    javaagent启动参数 > 当前类的其他属性 > 环境变量 > 系统变量 > 默认值
    ```
    但是，被数组、列表和字典类型包装的字符串将不会做上述映射。
  - 配置类支持使用[ConfigTypeKey](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/config/common/ConfigTypeKey.java)注解修改限定名，但是需要确保，所有统一配置的限定名(无论是否修改)不可重复。
  - 配置类及其复杂对象的属性支持使用[ConfigFieldKey](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/config/common/ConfigFieldKey.java)注解修改字段的名称。但是，被数组、列表和字典类型包装的复杂对象的属性则不支持修改。
  - 通过以下代码可以获取配置对象：
    ```java
    ConfigManager.getConfig(ConfigType.class)
    ```
- 插件服务示例：插件服务是核心服务系统的特化，遵循核心服务系统的规则。
  - 如[DemoSimpleService](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/service/DemoSimpleService.java)所示，是一个简单的插件服务，他编写于`插件(plugin)`模块中。鉴于简单的插件服务的定位，他只能使用java原生api以及核心包中自研的api，不能使用任何第三方api(无论核心包是否引入)。
  - [DemoComplexService](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/service/DemoComplexService.java)接口是示例`插件(plugin)`模块中定义复杂服务接口，该接口将会在`服务(service)`模块中实现。
  - [DemoComplexServiceImpl](javamesh-example/demo-service/src/main/java/com/huawei/example/demo/service/DemoComplexServiceImpl.java)是[DemoComplexService](javamesh-example/demo-plugin/src/main/java/com/huawei/example/demo/service/DemoComplexService.java)接口的实现，他编写于`服务(service)`模块中，属于复杂的插件服务，可以按需使用第三方依赖(示例中未使用)。
  - 简单的插件服务和复杂的插件服务接口都需要继承(实现)[PluginService](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/service/PluginService.java)接口。
  - 需要添加[PluginService](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/service/PluginService.java)的[spi配置文件](javamesh-example/demo-plugin/src/main/resources/META-INF/services/com.huawei.apm.core.plugin.service.PluginService)。
  - 通过以下代码可以获取服务对象：
    ```java
    ServiceManager.getService(ServiceType.class)
    ```

### 注意事项

本节将列举一些开发插件过程中容易出现错误的点：

- `插件(plugin)`包不能依赖或使用第三方依赖，如果服务功能较为复杂，必须使用第三方依赖，则可以将他们提取为`服务(service)`包，或者用shade插件隔离(不建议)。
- 同个`功能(function)`中，如果存在多个`服务(service)`包，他们不能存在依赖冲突的问题。
- 继承[AliaConfig](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/config/AliaConfig.java)的插件配置类对应的spi配置文件依然是[PluginConfig](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/plugin/config/PluginConfig.java)。
- 插件的配置文件有且只能有一个，即`config.yaml`。
- `config.yaml`中，被数组、列表和字典包装的复杂对象将不支持[ConfigFieldKey](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/config/common/ConfigFieldKey.java)属性别名，被包装的字符串也将不支持`${xxx}`映射。
- 配置文件中对应配置类的限定名不能重复，该规则针对全局所有统一配置有效。
- `插件(plugin)`模块和`服务(service)`模块如果不是定义在`功能(function)`模块的目录下，需要留意输出的jar包和配置路径是否正确。
- 拦截器的类型需要和[MethodInterceptPoint](../javamesh-agentcore/javamesh-agentcore-core/src/main/java/com/huawei/apm/core/agent/definition/MethodInterceptPoint.java)的对应方法保持一致，注意别写错。

### 打包流程

- `插件(plugin)`模块的产品输出到整个产品的`agent/pluginPackage/${功能名称}/plugin`目录。`插件(plugin)`涉及到`byte-buddy`包的使用，通常需要使用`maven-shade-plugin`插件作包名修正，添加如下标签即可：
  ```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
  ```
- `服务(service)`模块的产品输出到整个产品的`agent/pluginPackage/${功能名称}/service`目录：
  - 可以选择使用`assembly`插件或`shade`插件，将带依赖的`服务(service)`包打到`agent/pluginPackage/${功能名称}/service`目录。
  - 也可以选择使用`dependency`插件将相关的第三方依赖连同不带依赖的`服务(service)`包打到`agent/pluginPackage/${功能名称}/service`目录下。
  
  通常，如果存在多个`服务(service)`模块，选择使用后者可以有效避免依赖冲突和减少重复依赖。当然，如果单纯为了方便，也可以像`javamesh-example`一样直接使用`shade`插件。

## 附加件开发流程

本节将介绍`后端(server)`和`前端(webapp)`两种附加件的开发流程，由于这两部分是`功能function`中相对独立的内容，因此没有太多开发上的限制。

结合[打包步骤](#打包步骤)中介绍的步骤，与附加件开发相关的步骤有`ext`和`all`两个`profile`。如果需要为`功能(function)`模块添加`后端(server)`或`前端(webapp)`子模块：

- 在`功能(function)`模块的`pom.xml`文件的以下`profile`中添加`module`：
  ```xml
  <profiles>
    <profile>
      <id>ext</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <modules>
        <module>${后端模块名(如果有)}</module>
        <module>${前端模块名(如果有)}</module>
        <module>${其他模块名(如果有)}</module>
      </modules>
    </profile>
    <profile>
      <id>all</id>
      <modules>
        <module>${后端模块名(如果有)}</module>
        <module>${前端模块名(如果有)}</module>
        <module>${其他模块名(如果有)}</module>
      </modules>
    </profile>
  </profiles>
  ```
- 将`后端(server)`子模块和`前端(webapp)`子模块的输出调整至`${package.server.output.dir}`，常见打包插件如下：
  ```xml
  <!-- spring打包插件打包 -->
  <plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>${spring-boot打包插件版本}</version>
    <configuration>
      <mainClass>${main函数入口}</mainClass>
      <outputDirectory>${package.server.output.dir}</outputDirectory>
    </configuration>
    <executions>
      <execution>
        <goals>
          <goal>repackage</goal>
        </goals>
      </execution>
    </executions>
  </plugin>

  <!-- shade插件打包 -->
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>${shade插件版本}</version>
    <configuration>
      <outputDirectory>${package.server.output.dir}</outputDirectory>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
  </plugin>

  <!-- assembly插件打包 -->
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>${assembly插件版本}</version>
    <configuration>
      <outputDirectory>${package.server.output.dir}</outputDirectory>
      <descriptorRefs>
        <descriptorRef>jar-with-dependencies</descriptorRef>
      </descriptorRefs>
    </configuration>
    <executions>
      <execution>
        <id>make-assembly</id>
        <phase>package</phase>
        <goals>
          <goal>single</goal>
        </goals>
      </execution>
    </executions>
  </plugin>
  ```
- `后端(server)`子模块和`前端(webapp)`子模块如何开发依实际情况而定，仅有的要求，就是输出到产品的`server/${功能名称}`目录，同时提供启动他们的脚本或辅助性文本，能够帮助使用者快速启动和关闭即可。
