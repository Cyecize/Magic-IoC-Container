
Magic-IoC-Container
-----------------------
Custom made dependency container replicating the functionality of Spring Boot.

Main Features
-----------------------
* Create instance of your services
* Automatically resolve dependencies
* Create Beans
* Add your own custom mapping annotations for services and beans
* Manage instantiated services (get them by annotation, qualifier, type)
* Reload instantiated services
* Handle custom service and bean scopes by using proxies.

The Goal
-----------------------
This project is created with the purpose to help understand how IoC Containers work under the hood.
There is a video series on youtube where I am building this project:
https://www.youtube.com/watch?v=JHxFJwxb0FQ
* Note that after the video I've made changes to the app so if you are coming from there, please checkout to the following commit to be able to follow more easily.
* commit hash: 4ba9ad9eee6014598e356f2bb16e6819e53948fb -> Added project source code.

How to run the app?
------------------
* Run 'mvn package' and get the generated jar file.
* Import it into your project
* In your main method call 'MagicInjector.run(YourStartupClass.class);'
* Annotate your startup class with @Service
* Create a void method and annotate it with @StartUp

You can also run the app with 'MagicInjector.run(YourStartupClass.class, new MagicConfiguration());'.

Supported annotations by default: 
* Bean - Specify bean producing method.
* Service - Specify service.
* Autowired - Specify which constructor will be used to create instance of a service.
also you can annotate fields with this annotation.
* PostConstruct - Specify a method that will be executed after the service has been created.
* PreDestroy - Specify a method that will be executed just before the service has been disposed.
* StartUp - Specify the startup method for the app.
* AliasFor - You can use this annotation to integrate your own annotations with MagicInjector
AliasFor works with Autowired, NamedInstance, Nullable, PostConstruct, PreDestroy, Qualifier.
* NamedInstance - Specify the name of the service / bean.
* Nullable - required dependency can be null.
* Qualifier - Specify the name of the dependency that you are requiring.
* Scope - Specify the scope of the service. SINGLETON, PROTOTYPE or PROXY.

You can use the configuration to provide custom annotations that can act like @Bean and @Service.

The benefit of that is that you might have some services that you might want to filter out by something and 
custom annotations is a good approach.

Also you can access the DependencyContainer instance this way 'DependencyContainer dc = MagicInjector.run(...)' or
by requiring it from the startup method.

Documentation
------------
Currently there is no real documentation from where you can read, but 
you can read the javadoc or you can check out the integration tests located::: TODO place link
Also you can check out [Java Web Server](https://github.com/Cyecize/java-web-server/) 
where this app is heavily utilized from the MVC framework.

More info
-------------
If you are having trouble running the app, contact me at ceci2205@abv.bg .
