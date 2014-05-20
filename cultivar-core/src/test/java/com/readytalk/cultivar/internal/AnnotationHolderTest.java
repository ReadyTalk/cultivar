package com.readytalk.cultivar.internal;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.readytalk.cultivar.Curator;

import com.google.inject.name.Names;
import com.google.inject.util.Types;

@SuppressWarnings("ConstantConditions")
public class AnnotationHolderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Annotation ann = Names.named("");
    private final Class<? extends Annotation> annClass = Curator.class;

    private final Type testType = Types.setOf(String.class);

    private AnnotationHolder holderAnnotation;
    private AnnotationHolder holderClass;

    @Before
    public void setUp() {
        holderAnnotation = AnnotationHolder.create(ann);
        holderClass = AnnotationHolder.create(annClass);
    }

    @Test
    public void cons_NullAnnotationAndClass_ThrowsIAE() {
        thrown.expect(IllegalArgumentException.class);

        new AnnotationHolder(null, null);
    }

    @Test
    public void cons_NonNullAnnotationAndClass_ThrowsIAE() {
        thrown.expect(IllegalArgumentException.class);

        new AnnotationHolder(Names.named(""), Curator.class);
    }

    @Test
    public void create_Annotation_Null_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        AnnotationHolder.create((Annotation) null);
    }

    @Test
    public void create_Class_Null_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        AnnotationHolder.create((Class<? extends Annotation>) null);
    }

    @Test
    public void generateKey_AnnotationPresent_UsesAnnotation() {
        assertEquals(ann, holderAnnotation.generateKey(testType).getAnnotation());
    }

    @Test
    public void generateKey_ClassPresent_UsesClass() {
        assertEquals(annClass, holderClass.generateKey(testType).getAnnotationType());
    }
}
