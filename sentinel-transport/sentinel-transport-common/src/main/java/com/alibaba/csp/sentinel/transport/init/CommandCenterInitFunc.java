/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.transport.init;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.init.InitOrder;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.transport.CommandCenter;

/**
 * @author Eric Zhao
 */
// 控制初始化顺序
@InitOrder(-1)
public class CommandCenterInitFunc implements InitFunc {

    @Override
    public void init() throws Exception {
        // SPI默认实例化"META-INF/services/"路径下所有继承CommandCenter.class的bean
        // ps：CommandCenterInitFunc自己本身也是由InitExecutor.class的SPI实例化
        ServiceLoader<CommandCenter> loader = ServiceLoader.load(CommandCenter.class);
        Iterator<CommandCenter> iterator = loader.iterator();
        if (iterator.hasNext()) {
            CommandCenter commandCenter = iterator.next();
            if (iterator.hasNext()) {
                // 只允许有一个command center
                throw new IllegalStateException("Only single command center can be started");
            } else {
                commandCenter.beforeStart();
                commandCenter.start();
                RecordLog.info("[CommandCenterInit] Starting command center: "
                    + commandCenter.getClass().getCanonicalName());
            }
        }
    }
}
