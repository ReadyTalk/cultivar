package com.readytalk.cultivar.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.readytalk.cultivar.util.PropertyReader;
import com.readytalk.cultivar.util.mapping.ByteArrayMapper;

@RunWith(MockitoJUnitRunner.class)
public class PropertyOverrideNodeContainerTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static final String VALUE = "value";
    private static final byte[] VALUE_BYTES = "value".getBytes(Charsets.UTF_8);

    @Mock
    private ByteArrayMapper<String> mapper;

    @Mock
    private NodeCacheListener listener;

    private final Optional<String> propOverride = Optional.of("foo");

    private PropertyOverrideNodeContainer<String> container;

    @Before
    public void setUp() throws Exception {
        container = new PropertyOverrideNodeContainer<String>(mapper, propOverride);

        when(mapper.map(eq(VALUE_BYTES))).thenReturn(VALUE);
        when(mapper.map(eq(VALUE_BYTES), anyString())).thenReturn(VALUE);
        when(mapper.map(Matchers.isNull(byte[].class), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[1];
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        PropertyReader.reset();

        try {
            container.stopAsync();
        } catch (Exception ex) {
            // Ignore.
        }
    }

    @Test
    public void cons_propOverrideNotSet_ThrowsISE() {
        thrown.expect(IllegalStateException.class);
        new PropertyOverrideNodeContainer<String>(mapper, Optional.<String> absent());
    }

    @Test
    public void get_PropertySet_ReturnsProperty() {
        PropertyReader.setProperties(ImmutableMap.of("foo", VALUE));
        assertEquals(VALUE, container.get());
    }

    @Test
    public void get_PropertyNotSet_ReturnsNull() {
        PropertyReader.setProperties(ImmutableMap.<String, String> of());
        assertNull(container.get());
    }

    @Test
    public void get_WithDefault_PropertySet_ReturnsProperty() {
        PropertyReader.setProperties(ImmutableMap.of("foo", VALUE));
        assertEquals(VALUE, container.get("bar"));
    }

    @Test
    public void get_WithDefault_PropertyNotSet_ReturnsProperty() {
        PropertyReader.setProperties(ImmutableMap.<String, String> of());
        assertEquals("bar", container.get("bar"));
    }

    @Test
    public void runOneIteration_PropertiesChangeFromLastRead_NotifiesListeners() throws Exception {
        PropertyReader.setProperties(ImmutableMap.of("foo", VALUE));
        container.addListener(listener);

        container.startUp();

        PropertyReader.setProperties(ImmutableMap.of("bar", VALUE));

        container.runOneIteration();

        verify(listener).nodeChanged();
    }

    @Test
    public void runOneIteration_PropertiesNotChanged_DoesNotNotifyListeners() throws Exception {
        PropertyReader.setProperties(ImmutableMap.of("foo", VALUE));
        container.addListener(listener);

        container.startUp();

        container.runOneIteration();

        verify(listener, never()).nodeChanged();
    }

    @Test
    public void rebuild_PropertiesChangeFromLastRead_NotifiesListeners() throws Exception {
        PropertyReader.setProperties(ImmutableMap.of("foo", VALUE));
        container.addListener(listener);

        container.startUp();

        PropertyReader.setProperties(ImmutableMap.of("bar", VALUE));

        container.rebuild();

        verify(listener).nodeChanged();
    }

}
