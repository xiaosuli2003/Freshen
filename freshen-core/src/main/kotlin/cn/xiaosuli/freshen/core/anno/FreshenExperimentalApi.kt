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

package cn.xiaosuli.freshen.core.anno

/**
 * 被此注解标注的类或函数为Freshen实验性功能
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
@FreshenInternalApi
@RequiresOptIn("该功能为Freshen实验性功能，可能会发生改变，请谨慎使用！",RequiresOptIn.Level.WARNING)
annotation class FreshenExperimentalApi

/**
 * 雪花算法ID生成器在批量插入时100%复现id重复
 * * 所以仅适用于单个插入，若要批量插入，请使用UUID
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@RequiresOptIn("雪花算法ID生成器在批量插入时100%复现id重复，所以仅适用于单个插入，若要批量插入，请使用UUID！",RequiresOptIn.Level.ERROR)
annotation class SnowflakeIdIsExperimentalApi

/**
 * FlexID生成器在批量插入时100%复现id重复
 * * 所以仅适用于单个插入，若要批量插入，请使用UUID
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@RequiresOptIn("FlexID生成器在批量插入时100%复现id重复，所以仅适用于单个插入，若要批量插入，请使用UUID！",RequiresOptIn.Level.ERROR)
annotation class FlexIdIsExperimentalApi
