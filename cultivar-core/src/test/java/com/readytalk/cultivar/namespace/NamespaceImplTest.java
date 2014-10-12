package com.readytalk.cultivar.namespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings({ "ConstantConditions", "ObjectEqualsNull" })
public class NamespaceImplTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    public static class NamespaceView {
        @Namespace("foo")
        public String value;
    }

    @Test
    public void equals_NamedAnnotation_AnnotationOnClass_True() throws Exception {
        Field f = NamespaceView.class.getDeclaredField("value");

        Namespace namespace = f.getAnnotation(Namespace.class);

        assertEquals(namespace, new NamespaceImpl("foo"));
    }

    @Test
    public void hashCode_NamedAnnotation_AnnotationOnClass_Equals() throws Exception {
        Field f = NamespaceView.class.getDeclaredField("value");

        Namespace namespace = f.getAnnotation(Namespace.class);

        assertEquals(namespace.hashCode(), new NamespaceImpl("foo").hashCode());
    }

    @Test
    public void toString_NamedAnnotation_AnnotationOnClass_Equals() throws Exception {
        Field f = NamespaceView.class.getDeclaredField("value");

        Namespace namespace = f.getAnnotation(Namespace.class);

        assertEquals(namespace.toString(), new NamespaceImpl("foo").toString());
    }

    @Test
    public void annotationType_NamedAnnotation_AnnotationOnClass_Equals() throws Exception {
        Field f = NamespaceView.class.getDeclaredField("value");

        Namespace namespace = f.getAnnotation(Namespace.class);

        assertEquals(namespace.annotationType(), new NamespaceImpl("foo").annotationType());
    }

    @Test
    public void value_Provided_IsSame() throws Exception {
        assertEquals("foo", new NamespaceImpl("foo").value());
    }

    @Test
    public void cons_Null_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        new NamespaceImpl(null);
    }

    @Test
    public void hashCode_DifferentlyNamedAnnotation_AnnotationOnClass_NotEquals() throws Exception {
        Field f = NamespaceView.class.getDeclaredField("value");

        Namespace namespace = f.getAnnotation(Namespace.class);

        assertFalse(namespace.hashCode() == new NamespaceImpl("bar").hashCode());
    }

    @Test
    public void equals_DifferentlyNamedAnnotation_AnnotationOnClass_False() throws Exception {
        Field f = NamespaceView.class.getDeclaredField("value");

        Namespace namespace = f.getAnnotation(Namespace.class);

        assertFalse(namespace.equals(new NamespaceImpl("bar")));
    }

    @Test
    public void equals_Null_False() throws Exception {

        assertFalse(new NamespaceImpl("bar").equals(null));
    }

    @Test
    public void equals_Object_False() throws Exception {

        assertFalse(new NamespaceImpl("bar").equals(new Object()));
    }
}
