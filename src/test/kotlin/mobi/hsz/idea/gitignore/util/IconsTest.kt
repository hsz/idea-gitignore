// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.util

import mobi.hsz.idea.gitignore.Common
import org.junit.Test
import java.lang.reflect.InvocationTargetException

class IconsTest : Common<Icons>() {

    @Test
    @Throws(
        InvocationTargetException::class,
        NoSuchMethodException::class,
        InstantiationException::class,
        IllegalAccessException::class
    )
    fun testPrivateConstructor() {
        privateConstructor(Icons::class.java)
    }
}
