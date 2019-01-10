使用jmeter压测hessian接口的插件

1. 使用mvn clean package打包
2. 把打包好的jar包和pom配置的依赖jar包扔到${jmeter目录}/lib/ext下。这里需要加入到ext目录下的包有jmeter-hessian-1.0.jar，hessian-4.0.53.jar，fastjson-1.2.44.jar
3. 在jmeter中添加测试计划，线程组，java请求。在类名称中选择com.watson.hessian.HessianRequest已经添加相应参数即可进行压测