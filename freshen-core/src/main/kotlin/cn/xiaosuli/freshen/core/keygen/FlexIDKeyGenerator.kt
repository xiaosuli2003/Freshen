/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.xiaosuli.freshen.core.keygen

import java.util.concurrent.ThreadLocalRandom

/**
 * Mybatis-Flex独创的 FlexID 算法（简单、好用）:
 *
 * 特点：
 * 1、保证 id 生成的顺序为时间顺序，越往后生成的 ID 值越大；
 * 2、运行时，单台机器并发量在每秒钟 10w 以内；
 * 3、运行时，无视时间回拨；
 * 4、最大支持 99 台机器；
 * 5、够用大概 300 年左右的时间；
 *
 * 缺点：
 * 1、每台机器允许最大的并发量为 10w/s。
 * 2、出现时间回拨，重启机器时，在时间回拨未恢复的情况下，可能出现 id 重复。
 *
 * ID组成：时间（7+）| 毫秒内的时间自增 （00~99：2）| 机器ID（00 ~ 99：2）| 随机数（00~99：2）用于分库分表时，通过 id 取模，保证分布均衡。
 *
 * @param workId 机器ID
 *
 * 修改人：是晓酥梨呀（2060988267@qq.com）
 * 修改说明：将原文件翻译为Kotlin，修改了此注释，删除了generate方法的参数
 */
class FlexIDKeyGenerator(private val workId: Long = 1) : IKeyGenerator {
    private var lastTimeMillis: Long = 0 // 最后一次生成 ID 的时间
    private var clockSeq: Long = 0 // 时间序列

    override fun generate(): Long {
        return nextId()
    }

    @Synchronized
    private fun nextId(): Long {
        // 当前时间
        var currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis == lastTimeMillis) {
            clockSeq++
            if (clockSeq > MAX_CLOCK_SEQ) {
                clockSeq = 0
                currentTimeMillis++
            }
        } else if (currentTimeMillis < lastTimeMillis) {
            currentTimeMillis = lastTimeMillis
            clockSeq++

            if (clockSeq > MAX_CLOCK_SEQ) {
                clockSeq = 0
                currentTimeMillis++
            }
        } else {
            clockSeq = 0
        }

        lastTimeMillis = currentTimeMillis

        val diffTimeMillis = currentTimeMillis - INITIAL_TIMESTAMP

        // ID组成：时间（7+）| 毫秒内的时间自增 （00~99：2）| 机器ID（00 ~ 99：2）| 随机数（00~99：2）
        return diffTimeMillis * 1000000 + clockSeq * 10000 + workId * 100 + randomInt
    }


    private val randomInt: Int= ThreadLocalRandom.current().nextInt(100)

    companion object {
        private const val INITIAL_TIMESTAMP = 1680411660000L
        private const val MAX_CLOCK_SEQ: Long = 99
    }
}
