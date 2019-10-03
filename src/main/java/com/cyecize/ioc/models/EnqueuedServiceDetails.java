package com.cyecize.ioc.models;

import com.cyecize.ioc.annotations.Nullable;

import java.util.Arrays;

/**
 * Simple POJO class that keeps information about a service, its
 * required dependencies and the ones that are already resolved.
 */
public class EnqueuedServiceDetails {

    /**
     * Reference to the target service.
     */
    private final ServiceDetails serviceDetails;

    /**
     * Array of dependencies that the target constructor of the service requires.
     */
    private final Class<?>[] dependencies;

    /**
     * Keeps track for each dependency whether it is required
     */
    private final boolean[] dependenciesRequirement;

    /**
     * Array of instances matching the types in @dependencies.
     */
    private final Object[] dependencyInstances;

    public EnqueuedServiceDetails(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
        this.dependencies = serviceDetails.getTargetConstructor().getParameterTypes();
        this.dependenciesRequirement = new boolean[this.dependencies.length];
        this.dependencyInstances = new Object[this.dependencies.length];

        Arrays.fill(this.dependenciesRequirement, true);
    }

    public ServiceDetails getServiceDetails() {
        return this.serviceDetails;
    }

    public Class<?>[] getDependencies() {
        return this.dependencies;
    }

    public Object[] getDependencyInstances() {
        return this.dependencyInstances;
    }

    /**
     * Adds the object instance in the array of instantiated dependencies
     * by keeping the exact same position as the target constructor of the service has it.
     *
     * @param instance the given dependency instance.
     */
    public void addDependencyInstance(Object instance) {
        for (int i = 0; i < this.dependencies.length; i++) {
            if (this.dependencies[i].isAssignableFrom(instance.getClass())) {
                this.dependencyInstances[i] = instance;
            }
        }
    }

    /**
     * Checks if all dependencies have corresponding instances.
     *
     * @return true of ann dependency instances are available.
     */
    public boolean isResolved() {

        for (int i = 0; i < this.dependencyInstances.length; i++) {
            if (this.dependencyInstances[i] == null && this.dependenciesRequirement[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a given class type is present in the array of required
     * dependencies.
     *
     * @param dependencyType - the given class type.
     * @return true if the given type is present in the array of required dependencies.
     */
    public boolean isDependencyRequired(Class<?> dependencyType) {
        for (Class<?> dependency : this.dependencies) {
            if (dependency.isAssignableFrom(dependencyType)) {
                return true;
            }
        }

        return false;
    }

    public void setDependencyNotNull(Class<?> dependencyType, boolean isRequired) {
        for (int i = 0; i < this.dependenciesRequirement.length; i++) {
            if (this.dependencies[i].isAssignableFrom(dependencyType)) {
                this.dependenciesRequirement[i] = isRequired;
                return;
            }
        }
    }

    public boolean isDependencyNotNull(Class<?> dependencyType) {
        for (int i = 0; i < this.dependenciesRequirement.length; i++) {
            if (this.dependencies[i].isAssignableFrom(dependencyType)) {
                return this.dependenciesRequirement[i];
            }
        }

        throw new IllegalArgumentException(String.format("Invalid dependency \"%s\".", dependencyType));
    }
}
