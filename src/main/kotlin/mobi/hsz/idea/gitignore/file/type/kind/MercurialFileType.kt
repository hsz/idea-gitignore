// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file.type.kind

import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.kind.MercurialLanguage

/**
 * Describes Mercurial file type.
 */
class MercurialFileType : IgnoreFileType(MercurialLanguage.INSTANCE) {

    companion object {
        val INSTANCE = MercurialFileType()
    }
}
