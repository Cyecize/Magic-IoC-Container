package com.cyecize.ioc.services;

import com.cyecize.ioc.exceptions.PreDestroyExecutionException;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.exceptions.BeanInstantiationException;
import com.cyecize.ioc.exceptions.ServiceInstantiationException;
import com.cyecize.ioc.models.ServiceBeanDetails;

public interface ObjectInstantiationService {

    void createInstance(ServiceDetails serviceDetails, Object... constructorParams) throws ServiceInstantiationException;

    void createBeanInstance(ServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException;

    void destroyInstance(ServiceDetails serviceDetails) throws PreDestroyExecutionException;
}
