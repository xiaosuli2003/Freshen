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

import java.lang.management.ManagementFactory
import java.net.InetAddress
import java.net.NetworkInterface

/**
 *
 * 雪花算法 ID 生成器。
 *
 *  * 最高 1 位固定值 0，因为生成的 ID 是正整数；
 *  * 接下来 41 位存储毫秒级时间戳，2 ^ 41 / ( 1000 * 60 * 60 * 24 * 365) = 69，大概可以使用 69 年；
 *  * 再接下 10 位存储机器码，包括 5 位 dataCenterId 和 5 位 workerId，最多可以部署 2 ^ 10 = 1024 台机器；
 *  * 最后 12 位存储序列号，同一毫秒时间戳时，通过这个递增的序列号来区分，即对于同一台机器而言，同一毫秒时间戳下，可以生成 2 ^ 12 = 4096 个不重复 ID。
 *
 * 优化自开源项目：[Sequence](https://gitee.com/yu120/sequence)
 *
 * @author 王帅
 * @since 2023-05-12
 *
 * 修改人：是晓酥梨呀（2060988267@qq.com）
 * 二次修改说明：
 * * 将原文件翻译为Kotlin
 * * 修改了此注释
 * * 修改了抛出的异常
 * * 修改了name的判空（在第111行）
 * * 删除了generate方法的参数
 */
open class SnowFlakeIDKeyGenerator : IKeyGenerator {
    /**
     * 工作机器 ID。
     */
    private val workerId: Long

    /**
     * 数据中心 ID。
     */
    private val dataCenterId: Long

    /**
     * IP 地址信息，用来生成工作机器 ID 和数据中心 ID。
     */
    private var address: InetAddress? = null

    /**
     * 同一毫秒内的最新序号，最大值可为（2^12 - 1 = 4095）。
     */
    private var sequence: Long = 0

    /**
     * 上次生产 ID 时间戳。
     */
    private var lastTimeMillis: Long = -1L

    /**
     * 根据 IP 地址计算数据中心 ID 和工作机器 ID 生成数据库 ID。
     *
     * @param address IP 地址
     */
    /**
     * 雪花算法 ID 生成器。
     */
    @JvmOverloads
    constructor(address: InetAddress? = null) {
        this.address = address
        this.dataCenterId = getDataCenterId(MAX_DATA_CENTER_ID)
        this.workerId = getWorkerId(dataCenterId, MAX_WORKER_ID)
    }

    /**
     * 根据数据中心 ID 和工作机器 ID 生成数据库 ID。
     *
     * @param workerId     工作机器 ID
     * @param dataCenterId 数据中心 ID
     */
    constructor(workerId: Long, dataCenterId: Long) {
        require(!(workerId > MAX_WORKER_ID || workerId < 0)) {
            String.format(
                "workerId must be greater than 0 and less than %d.",
                MAX_WORKER_ID
            )
        }
        require(!(dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0)) {
            String.format(
                "dataCenterId must be greater than 0 and less than %d.",
                MAX_DATA_CENTER_ID
            )
        }
        this.workerId = workerId
        this.dataCenterId = dataCenterId
    }

    /**
     * 根据 MAC + PID 的 hashCode 获取 16 个低位生成工作机器 ID。
     */
    private fun getWorkerId(dataCenterId: Long, maxWorkerId: Long): Long {
        val mpId = StringBuilder()
        mpId.append(dataCenterId)
        val name = ManagementFactory.getRuntimeMXBean().name
        if (name.isNotBlank()) {
            // GET jvmPid
            mpId.append(name.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        }
        // MAC + PID 的 hashCode 获取16个低位
        return (mpId.toString().hashCode() and 0xffff) % (maxWorkerId + 1)
    }

    /**
     * 根据网卡 MAC 地址计算余数作为数据中心 ID。
     */
    private fun getDataCenterId(maxDataCenterId: Long): Long {
        var id = 0L
        try {
            if (address == null) {
                address = InetAddress.getLocalHost()
            }
            val network = NetworkInterface.getByInetAddress(address)
            if (null == network) {
                id = 1L
            } else {
                val mac = network.hardwareAddress
                if (null != mac) {
                    id =
                        ((0x000000FFL and mac[mac.size - 2].toLong()) or (0x0000FF00L and ((mac[mac.size - 1].toLong()) shl 8))) shr 6
                    id %= (maxDataCenterId + 1)
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("dataCenterId: ${e.message}")
        }
        return id
    }

    override fun generate(): Any {
        return nextId()
    }

    /**
     * 获取下一个 ID。
     */
    @Synchronized
    private fun nextId(): Long {
        var currentTimeMillis = System.currentTimeMillis()
        // 当前时间小于上一次生成 ID 使用的时间，可能出现服务器时钟回拨问题。
        if (currentTimeMillis < lastTimeMillis) {
            val offset = lastTimeMillis - currentTimeMillis
            // 在可容忍的时间差值之内等待时间恢复正常
            if (offset <= offsetPeriod) {
                try {
                    (this as Object).wait(offset shl 1L.toInt())
                    currentTimeMillis = System.currentTimeMillis()
                    if (currentTimeMillis < lastTimeMillis) {
                        throw RuntimeException("Clock moved backwards, please check the time. Current timestamp: ${currentTimeMillis}, last used timestamp: $lastTimeMillis")
                    }
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            } else {
                throw RuntimeException("Clock moved backwards, please check the time. Current timestamp: ${currentTimeMillis}, last used timestamp: $lastTimeMillis")
            }
        }

        if (currentTimeMillis == lastTimeMillis) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) and SEQUENCE_MASK
            if (sequence == 0L) {
                // 同一毫秒的序列数已经达到最大
                currentTimeMillis = tilNextMillis(lastTimeMillis)
            }
        } else {
            // 不同毫秒内，序列号置为 0。
            sequence = 0L
        }

        // 记录最后一次使用的毫秒时间戳
        lastTimeMillis = currentTimeMillis

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return (((currentTimeMillis - twepoch) shl TIMESTAMP_SHIFT.toInt())
                or (dataCenterId shl DATA_CENTER_ID_SHIFT.toInt())
                or (workerId shl WORK_ID_SHIFT.toInt())
                or sequence)
    }

    /**
     * 获取指定时间戳的接下来的时间戳。
     */
    private fun tilNextMillis(lastTimestamp: Long): Long {
        var currentTimeMillis = System.currentTimeMillis()
        while (currentTimeMillis <= lastTimestamp) {
            currentTimeMillis = System.currentTimeMillis()
        }
        return currentTimeMillis
    }

    companion object {
        /**
         * 工作机器 ID 占用的位数（5bit）。
         */
        private const val WORKER_ID_BITS = 5L

        /**
         * 数据中心 ID 占用的位数（5bit）。
         */
        private const val DATA_CENTER_ID_BITS = 5L

        /**
         * 序号占用的位数（12bit）。
         */
        private const val SEQUENCE_BITS = 12L

        /**
         * 工作机器 ID 占用 5bit 时的最大值 31。
         */
        private const val MAX_WORKER_ID = (-1L shl WORKER_ID_BITS.toInt()).inv()

        /**
         * 数据中心 ID 占用 5bit 时的最大值 31。
         */
        private const val MAX_DATA_CENTER_ID = (-1L shl DATA_CENTER_ID_BITS.toInt()).inv()

        /**
         * 序号掩码，用于与自增后的序列号进行位“与”操作，如果值为 0，则代表自增后的序列号超过了 4095。
         */
        private const val SEQUENCE_MASK = (-1L shl SEQUENCE_BITS.toInt()).inv()

        /**
         * 工作机器 ID 位需要左移的位数（12bit）。
         */
        private const val WORK_ID_SHIFT = SEQUENCE_BITS

        /**
         * 数据中心 ID 位需要左移的位数（12bit + 5bit）。
         */
        private const val DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS

        /**
         * 时间戳需要左移的位数（12bit + 5bit + 5bit）。
         */
        private const val TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS

        /**
         * 时间起始标记点，一旦确定不能变动（2023-04-02 13:01:00）。
         */
        var twepoch: Long = 1680411660000L

        /**
         * 可容忍的时间偏移量。
         */
        var offsetPeriod: Long = 5L
    }
}
