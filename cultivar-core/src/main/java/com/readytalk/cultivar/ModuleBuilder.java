package com.readytalk.cultivar;

import java.lang.annotation.Annotation;

import com.google.inject.Module;

public interface ModuleBuilder<T extends ModuleBuilder<T>> {
    T framework(Annotation annotation);

    T framework(Class<? extends Annotation> annotation);

    Module build();
}
