![动态注册bean，Spring官方套路：使用ImportBeanDefinitionRegistrar](https://pic1.zhimg.com/v2-24c583d8281b8b1d55661f6e79f898b1_r.jpg)

# 动态注册bean，Spring官方套路：使用ImportBeanDefinitionRegistrar

[![李佳明](https://pic3.zhimg.com/v2-47671bd2ed8b0230f83ad874b435af39_xs.jpg)](https://www.zhihu.com/people/li-jia-ming-70)[李佳明](https://www.zhihu.com/people/li-jia-ming-70)

<!-- react-empty: 33 -->



## 动态注册bean，Spring官方套路：使用ImportBeanDefinitionRegistrar

[上一篇文章][1]中介绍了Spring提供的动态注册bean的方法。这里会介绍一下Spring官方实现动态注册bean的套路。

## ImportBeanDefinitionRegistrar

Spring官方在动态注册bean时，大部分套路其实是使用**ImportBeanDefinitionRegistrar**接口。

所有实现了该接口的类的都会被**ConfigurationClassPostProcessor**处理，**ConfigurationClassPostProcessor**实现了**BeanFactoryPostProcessor**接口，所以**ImportBeanDefinitionRegistrar**中动态注册的bean是优先与依赖其的bean初始化的，也能被aop、validator等机制处理。

使用方法

**ImportBeanDefinitionRegistrar**需要配合@Configuration和@Import注解，@Configuration定义Java格式的Spring配置文件，@Import注解导入实现了**ImportBeanDefinitionRegistrar**接口的类。

例子：

要实现的效果如下，在接口上使用注解定义url、http方法类型等信息，程序根据这些信息动态生成实现类

```language-java
@Component
    @HTTPUtil
    public interface IRequestDemo {
        //调用test1时，会对http://abc.com发送get请求
        @HTTPRequest(url = "http://abc.com")

        HttpResult<String> test1();
        //调用test2时，会对http://test2.com发送post请求
        @HTTPRequest(url = "http://test2.com", httpMethod = HTTPMethod.POST)
        HttpResult<String> test2();
    }
```

例子思路来自于我的同事[晓风轻][2]的文章[《编写简陋的接口调用框架》][3]。

此处为了简化，我并没有实现完整的http请求代理，而是把注意力集中到**ImportBeanDefinitionRegistrar**的实现上。

对完整实现感兴趣的，可以参考项目[https://github.com/xwjie/MyRestUtil](http://link.zhihu.com/?target=https%3A//github.com/xwjie/MyRestUtil)

例子编写步骤

1. 首先编写核心**ImportBeanDefinitionRegistrar**接口，重要代码如下：

主要思路是利用**ClassPathScanningCandidateComponentProvider**获取标注了**HTTPUtil**注解的接口，并使用JDK动态代理为期生成代理对象。然后使用**DefaultListableBeanFactory**将代理对象注册到容器中。如下：

```language-java
@Slf4j
public class HTTPRequestRegistrar implements ImportBeanDefinitionRegistrar,
       ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware {
   @Override
   public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
       registerHttpRequest(beanDefinitionRegistry);
   }

   /**
    * 注册动态bean的主要方法
    *
    * @param beanDefinitionRegistry
    */
   private void registerHttpRequest(BeanDefinitionRegistry beanDefinitionRegistry) {
       ClassPathScanningCandidateComponentProvider classScanner = getClassScanner();
       classScanner.setResourceLoader(this.resourceLoader);
       //指定只关注标注了@HTTPUtil注解的接口
       AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HTTPUtil.class);
       classScanner.addIncludeFilter(annotationTypeFilter);
       //指定扫描的基础包
       String basePack = "com.example.registerbean";
       Set<BeanDefinition> beanDefinitionSet = classScanner.findCandidateComponents(basePack);
       for (BeanDefinition beanDefinition : beanDefinitionSet) {
           if (beanDefinition instanceof AnnotatedBeanDefinition) {
               registerBeans(((AnnotatedBeanDefinition) beanDefinition));
           }
       }
   }

   /**
    * 创建动态代理，并动态注册到容器中
    *
    * @param annotatedBeanDefinition
    */
   private void registerBeans(AnnotatedBeanDefinition annotatedBeanDefinition) {
       String className = annotatedBeanDefinition.getBeanClassName();
       ((DefaultListableBeanFactory) this.beanFactory).registerSingleton(className, createProxy(annotatedBeanDefinition));
   }

   /**
    * 构造Class扫描器，设置了只扫描顶级接口，不扫描内部类
    *
    * @return
    */
   private ClassPathScanningCandidateComponentProvider getClassScanner() {
       return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

           @Override
           protected boolean isCandidateComponent(
                   AnnotatedBeanDefinition beanDefinition) {
               if (beanDefinition.getMetadata().isInterface()) {
                   try {
                       Class<?> target = ClassUtils.forName(
                               beanDefinition.getMetadata().getClassName(),
                               classLoader);
                       return !target.isAnnotation();
                   } catch (Exception ex) {
                       log.error("load class exception:", ex);
                   }
               }
               return false;
           }
       };
   }

   /**
    * 创建动态代理
    *
    * @param annotatedBeanDefinition
    * @return
    */
   private Object createProxy(AnnotatedBeanDefinition annotatedBeanDefinition) {
       try {
           AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
           Class<?> target = Class.forName(annotationMetadata.getClassName());
           InvocationHandler invocationHandler = createInvocationHandler();
           Object proxy = Proxy.newProxyInstance(HTTPRequest.class.getClassLoader(), new Class[]{target}, invocationHandler);
           return proxy;
       } catch (ClassNotFoundException e) {
           log.error(e.getMessage());
       }
       return null;
   }

   /**
    * 创建InvocationHandler，将方法调用全部代理给DemoHttpHandler
    *
    * @return
    */
   private InvocationHandler createInvocationHandler() {
       return new InvocationHandler() {
           private DemoHttpHandler demoHttpHandler = new DemoHttpHandler();

           @Override
           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

               return demoHttpHandler.handle(method);
           }
       };
   }        
    ... 省略setter代码   
}
```

2.编写注解，并在其中使用@Import导入第1步编写的**HTTPRequestRegistrar**。

```language-text
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(HTTPRequestRegistrar.class)
public @interface EnableHttpUtil {
}
```

3.将@EnableHttpUtil添加到@Configuration注解下，如果使用了Spring-Boot，由于@SpringBootApplication注解包含了@Configuration注解，可以将@EnableHttpUtil添加到@SpringBootApplication注解下。

```language-java
@SpringBootApplication
@EnableHttpUtil
public class RegisterbeanImportBeanDefinitionRegistrarApplication {

	public static void main(String[] args) {
SpringApplication.run(RegisterbeanImportBeanDefinitionRegistrarApplication.class, args);
	}
}
```

4.使用，直接注入**IRequestDemo**即可

```language-java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RegisterbeanImportBeanDefinitionRegistrarApplicationTests {
   @Autowired
   IRequestDemo iRequestDemo;

   @Test
   public void test1() {
       HttpResult<String> result = this.iRequestDemo.test1();
       String response = result.getResponse();
log.info(">>>>>>>>>>{}", response);
       assertEquals("http request: url=http://abc.com and method=GET",response);
   }

   @Test
   public void test2() {
       HttpResult<String> result = this.iRequestDemo.test2();
       String response = result.getResponse();
log.info(">>>>>>>>>>{}", response);
       assertEquals("http request: url=http://test2.com and method=POST",response);
   }

}
```

完整可以运行demo代码在[https://github.com/pkpk1234/registerbean-ImportBeanDefinitionRegistrar](http://link.zhihu.com/?target=https%3A//github.com/pkpk1234/registerbean-ImportBeanDefinitionRegistrar)

[1]: [Spring动态注册bean](https://zhuanlan.zhihu.com/p/30070328)

[2]: [知乎用户](https://www.zhihu.com/people/xiaofengqing/activities)

[3]: [编写简陋的接口调用框架 - 动态代理学习](https://zhuanlan.zhihu.com/p/29348799)
