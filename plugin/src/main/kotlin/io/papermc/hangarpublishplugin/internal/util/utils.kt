/*
 * Hangar Publish Plugin Gradle Plugin
 * Copyright (c) 2023 HangarMC Contributors
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
package io.papermc.hangarpublishplugin.internal.util

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider

fun String.capitalized(): String {
    return replaceFirstChar(Char::uppercase)
}

fun <T> NamedDomainObjectContainer<T>.maybeRegister(name: String, action: Action<T>): NamedDomainObjectProvider<T> {
    return if (name in names) {
        named(name, action)
    } else {
        register(name, action)
    }
}

fun <T> Closure<T>.runWithDelegate(t: T) {
    delegate = t
    run()
    delegate = null
}
