package com.demo.ioc.services;

import com.demo.ioc.exceptions.BeanInstantiationException;
import com.demo.ioc.exceptions.PreDestroyExecutionException;
import com.demo.ioc.exceptions.ServiceInstantiationException;
import com.demo.ioc.models.ServiceBeanDetails;
import com.demo.ioc.models.ServiceDetails;

public interface ObjectInstantiationService {

    void createInstance(ServiceDetails serviceDetails, Object... constructorParams) throws ServiceInstantiationException;

    void createBeanInstance(ServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException;

    void destroyInstance(ServiceDetails serviceDetails) throws PreDestroyExecutionException;
}
