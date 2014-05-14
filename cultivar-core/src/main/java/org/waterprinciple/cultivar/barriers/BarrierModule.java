package org.waterprinciple.cultivar.barriers;

import com.google.common.annotations.Beta;
import com.google.inject.PrivateModule;

@Beta
public class BarrierModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(DistributedBarrierFactory.class);

        expose(DistributedBarrierFactory.class);

        bind(DistributedDoubleBarrierFactory.class);

        expose(DistributedDoubleBarrierFactory.class);

    }

}
