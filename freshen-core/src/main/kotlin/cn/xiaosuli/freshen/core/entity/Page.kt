/*
 * Copyright 2024 是晓酥梨呀(2060988267@qq.com)
 *
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
 */

package cn.xiaosuli.freshen.core.entity

/**
 * 分页数据。
 *
 * @property pageNumber 当前页码
 * @property pageSize 每页数据数量
 * @property totalPage 总页数
 * @property totalRow 总数据数量
 * @property records 当前页数据集合
 */
data class Page<T>(
    /**
     * 当前页码。
     */
    val pageNumber: Long,
    /**
     * 每页数据数量。
     */
    val pageSize: Long,
    /**
     * 总页数。
     */
    val totalPage: Long,
    /**
     * 总数据数量。
     */
    val totalRow: Long,
    /**
     * 当前页数据。
     */
    val records: List<T>
)