package com.readytalk.cultivar.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Executor;

import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.readytalk.cultivar.util.mapping.ByteArrayMapper;

@SuppressWarnings("ConstantConditions")
@RunWith(MockitoJUnitRunner.class)
public class DefaultNodeContainerTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Object returnObject = new Object();

    @Mock
    private NodeCache cache;

    @Mock
    private Executor executor;

    @Mock
    private NodeCacheListener listener;

    @Mock
    private ByteArrayMapper<Object> mapper;

    @Mock
    private ListenerContainer<NodeCacheListener> listenerContainer;

    @Mock
    private ChildData childData;

    private DefaultNodeContainer<Object> container;

    @Before
    public void setUp() {
        when(cache.getListenable()).thenReturn(listenerContainer);
        when(mapper.map(any(byte[].class), any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[1];
            }
        });

        when(cache.getCurrentData()).thenReturn(childData);

        container = new DefaultNodeContainer<Object>(cache, mapper);
    }

    @Test
    public void startUp_StartsCacheWithCreateInitial() throws Exception {
        container.startUp();

        verify(cache).start(true);
    }

    @Test
    public void shutDown_ClosesCache() throws Exception {
        container.shutDown();

        verify(cache).close();
    }

    @Test
    public void addListener_AddsListenerToCache() {
        container.addListener(listener);

        verify(listenerContainer).addListener(listener);
    }

    @Test
    public void addListener_NullListener_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        container.addListener(null);
    }

    @Test
    public void addListenerWithExecutor_AddsListenerToCache() {
        container.addListener(listener, executor);

        verify(listenerContainer).addListener(listener, executor);
    }

    @Test
    public void addListenerWithExecutor_NullListener_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        container.addListener((NodeCacheListener) null, executor);
    }

    @Test
    public void addListenerWithExecutor_NullExecutor_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        container.addListener(listener, null);
    }

    @Test
    public void get_ChildDataContainsBytes_ReturnMapperResult() {
        byte[] bytes = new byte[] { 0x01 };
        when(childData.getData()).thenReturn(bytes);
        when(mapper.map(bytes)).thenReturn(returnObject);

        assertEquals(returnObject, container.get());
    }

    @Test
    public void get_ChildDataContainsNull_ReturnNull() {
        when(childData.getData()).thenReturn(null);

        assertNull(container.get());
    }

    @Test
    public void get_ChildDataIsNull_ReturnNull() {
        when(cache.getCurrentData()).thenReturn(null);

        assertNull(container.get());
    }

    @Test
    public void getWithDefault_ChildDataContainsBytes_ReturnMapperResult() {
        byte[] bytes = new byte[] { 0x01 };
        when(childData.getData()).thenReturn(bytes);
        when(mapper.map(eq(bytes), any())).thenReturn(returnObject);

        assertEquals(returnObject, container.get(new Object()));
    }

    @Test
    public void getWithDefault_ChildDataContainsNull_ReturnDefault() {

        Object defaultReturn = new Object();

        when(childData.getData()).thenReturn(null);

        assertEquals(defaultReturn, container.get(defaultReturn));
    }

    @Test
    public void getWithDefault_ChildDataIsNull_ReturnDefault() {

        Object defaultReturn = new Object();

        when(cache.getCurrentData()).thenReturn(null);

        assertEquals(defaultReturn, container.get(defaultReturn));
    }

    @Test
    public void rebuild_RebuildsCache() throws Exception {
        container.rebuild();

        verify(cache).rebuild();
    }

    @Test
    public void rebuild_GeneralExceptionWhileRebuilding_DoesNotRethrow() throws Exception {
        doThrow(new Exception()).when(cache).rebuild();

        container.rebuild();
    }

    @Test
    public void rebuild_ISEWhileRebuilding_RethrowsException() throws Exception {
        thrown.expect(IllegalStateException.class);

        doThrow(new IllegalStateException()).when(cache).rebuild();

        container.rebuild();
    }
}
