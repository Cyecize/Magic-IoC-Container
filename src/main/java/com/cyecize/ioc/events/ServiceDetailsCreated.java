package com.cyecize.ioc.events;

import com.cyecize.ioc.models.ServiceDetails;

@FunctionalInterface
public interface ServiceDetailsCreated {
    void serviceDetailsCreated(ServiceDetails serviceDetails);
}
