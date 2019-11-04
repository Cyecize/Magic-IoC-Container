
Magic-IoC-Container
-----------------------
Custom made dependency container replicating the functionalities of Spring Boot.

Main Functionalities
-----------------------
* Create instance of your services
* Automatically resolve dependencies
* Create Beans
* Add your own custom mapping annotations for services and beans as a configuration
* Manage instantiated services (get them by annotation or by type)
* Reload instantiated services
* Use of proxy classes to improve consistency when reloading services.

The Goal
-----------------------
This project is created with the purpose to help understand how IoC Containers work under the hood.
There is a video series on youtube where I am building this project:
https://www.youtube.com/watch?v=JHxFJwxb0FQ
* Note that after the video I did some changes to the app so if you are coming from there, please checkout to the following commit to be able to follow more easily.
* commit hash: 4ba9ad9eee6014598e356f2bb16e6819e53948fb -> Added project source code.

How to run the app?
------------------
* Run 'mvn package' and get the generated jar file.
* Import it into your project
* In your main method call 'MagicInjector.run(YourStartupClass.class);'
* Annotate your startup class with @Service
* Create a void method with 0 params and annotate it with @StartUp

You can also run the app with 'MagicInjector.run(YourStartupClass.class, new MagicConfiguration());'.

Supported annotations by default: 
* Bean
* Service
* Autowired
* PostConstruct
* PreDestroy
* StartUp

You can use the configuration to provide custom annotations that can act like @Bean and @Service.
The benefit of that is that you might have some services that you might want to filter out by something and 
custom annotations is a good approach.

Also you can access the DependencyContainer instance this way 'MagicInjector.dependencyContainer'.

More info
-------------
If you are having trouble running the app, contact me at ceci2205@abv.bg .
