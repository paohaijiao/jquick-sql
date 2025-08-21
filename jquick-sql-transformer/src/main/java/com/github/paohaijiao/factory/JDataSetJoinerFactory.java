/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.factory;


import com.github.paohaijiao.enums.JEngineEnums;
import com.github.paohaijiao.flink.JFlinkJoiner;
import com.github.paohaijiao.forkjoin.JForkJoinJoiner;
import com.github.paohaijiao.lamda.JLamdaJoinJoinerHandler;
import com.github.paohaijiao.mr.JMapReduceJoiner;
import com.github.paohaijiao.spark.JSparkJoiner;
import com.github.paohaijiao.thread.JMultiThreadedJoiner;

/**
 * packageName com.github.paohaijiao.factory
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JDataSetJoinerFactory {

    public static JDataSetJoinerStrategy createJoiner(JEngineEnums type) {
        switch (type) {
            case MULTI_THREADED:
                return new JMultiThreadedJoiner();
            case LAMBDA:
                return new JLamdaJoinJoinerHandler();
            case SPARK:
                return new JSparkJoiner();
            case MAP_REDUCE:
                return new JMapReduceJoiner();
            case FORK_JOIN:
                return new JForkJoinJoiner();
            case FLINK:
                return new JFlinkJoiner();
            default:
                return new JLamdaJoinJoinerHandler();
        }
    }
}
