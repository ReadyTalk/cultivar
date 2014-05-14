package org.waterprinciple.cultivar.locks;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.Key;
import com.google.inject.Module;

@SuppressWarnings("ConstantConditions")
public class AbstractReaperModuleBuilderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private TestModuleBuilder builder;

    private static class TestModuleBuilder extends AbstractReaperModuleBuilder<TestModuleBuilder> {
        @Override
        public Module build() {
            throw new UnsupportedOperationException();
        }
    }

    @Before
    public void setUp() throws Exception {
        builder = new TestModuleBuilder();
    }

    @Test
    public void leaderPath_NullPath_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        builder.leaderPath(null);
    }

    @Test
    public void leaderPath_String_ReturnsSelf() {
        assertEquals(builder, builder.leaderPath("/test"));
    }

    @Test
    public void leaderPath_String_SetsLeaderPath() {
        builder.leaderPath("/test");
        assertEquals("/test", builder.getLeaderPath());
    }

    @Test
    public void exectuor__NullKey_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        builder.exectuor(null);
    }

    @Test
    public void executor_Valid_ReturnsSelf() {
        assertEquals(builder, builder.exectuor(Key.get(ListeningScheduledExecutorService.class)));
    }

    @Test
    public void executor_Valid_SetsService() {
        Key<ListeningScheduledExecutorService> executorServiceKey = Key.get(ListeningScheduledExecutorService.class);
        builder.exectuor(executorServiceKey);

        assertEquals(executorServiceKey, builder.getService());
    }

    @Test
    public void reapingThreshold__NullUnit_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        builder.reapingThreshold(500, null);
    }

    @Test
    public void reapingThreshold__ZeroValue_ThrowsIAE() {
        thrown.expect(IllegalArgumentException.class);

        builder.reapingThreshold(0, TimeUnit.MILLISECONDS);
    }

    @Test
    public void reapingThreshold_Valid_ReturnsSelf() {
        assertEquals(builder, builder.reapingThreshold(500, TimeUnit.MILLISECONDS));
    }

    @Test
    public void reapingThreshold_Valid_SetsThresholdInMillis() {
        builder.reapingThreshold(5, TimeUnit.SECONDS);

        assertEquals(TimeUnit.SECONDS.toMillis(5), builder.getReapingThresholdMillis());
    }

}
