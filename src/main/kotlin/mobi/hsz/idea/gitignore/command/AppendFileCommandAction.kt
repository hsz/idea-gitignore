// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.command

import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.ContentGenerator
import mobi.hsz.idea.gitignore.util.Notify
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Command action that appends specified file to rules list.
 */
class AppendFileCommandAction(
    private val project: Project,
    private val file: PsiFile,
    private val content: MutableSet<String>,
    private val ignoreDuplicates: Boolean = false,
    private val ignoreComments: Boolean = false,
) : CommandAction<PsiFile?>(project) {

    private val settings = service<IgnoreSettings>()

    /**
     * Adds [.content] to the given [.file]. Checks if file contains content and sends a notification.
     *
     * @return previously provided file
     */
    @Suppress("ComplexMethod", "LongMethod", "NestedBlockDepth")
    override fun compute(): PsiFile {
        if (content.isEmpty()) {
            return file
        }
        val manager = PsiDocumentManager.getInstance(project)
        manager.getDocument(file)?.let { document ->
            var offset = document.textLength

            file.acceptChildren(
                object : IgnoreVisitor() {
                    override fun visitEntry(entry: IgnoreEntry) {
                        val moduleDir = Utils.getModuleRootForFile(file.virtualFile, project)
                        if (content.contains(entry.text) && moduleDir != null) {
                            Notify.show(
                                project,
                                IgnoreBundle.message("action.appendFile.entryExists", entry.text),
                                IgnoreBundle.message(
                                    "action.appendFile.entryExists.in",
                                    Utils.getRelativePath(moduleDir, file.virtualFile)
                                ),
                                NotificationType.WARNING
                            )
                            content.remove(entry.text)
                        }
                    }
                }
            )

            if (settings.insertAtCursor) {
                EditorFactory.getInstance().getEditors(document).firstOrNull()?.let { editor ->
                    editor.selectionModel.selectionStartPosition?.let { position ->
                        offset = document.getLineStartOffset(position.line)
                    }
                }
            }

            val generatedContent = ContentGenerator.generate(document.text, content, ignoreDuplicates, ignoreComments)
            document.insertString(offset, generatedContent)
            manager.commitDocument(document)
        }
        return file
    }
}
