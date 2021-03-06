// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.util.IncorrectOperationException
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.util.Constants

/**
 * Templates factory that generates Gitignore file and its content.
 */
class IgnoreTemplatesFactory(private val fileType: IgnoreFileType) : FileTemplateGroupDescriptorFactory {

    override fun getFileTemplatesDescriptor() = FileTemplateGroupDescriptor(
        fileType.ignoreLanguage.id,
        fileType.icon
    ).apply {
        addTemplate(fileType.ignoreLanguage.filename)
    }

    /**
     * Creates new Gitignore file or uses an existing one.
     *
     * @param directory working directory
     * @return file
     */
    @Throws(IncorrectOperationException::class)
    fun createFromTemplate(directory: PsiDirectory): PsiFile {
        val filename = fileType.ignoreLanguage.filename
        directory.findFile(filename)?.let {
            return it
        }

        val language = fileType.ignoreLanguage
        val content = when {
            language.isSyntaxSupported && IgnoreBundle.Syntax.GLOB != language.defaultSyntax -> StringUtil.join(
                IgnoreBundle.Syntax.GLOB.presentation,
                Constants.NEWLINE,
                Constants.NEWLINE
            )
            else -> ""
        }
        val file = PsiFileFactory.getInstance(directory.project).createFileFromText(filename, fileType, content)
        return directory.add(file) as PsiFile
    }
}
