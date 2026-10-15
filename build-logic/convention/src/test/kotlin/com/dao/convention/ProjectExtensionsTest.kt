package com.dao.convention

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.testfixtures.ProjectBuilder

class ProjectExtensionsTest {
    @Test
    fun `should return true for isRoot when project is root project`() {
        val rootProject = ProjectBuilder.builder().build()
        assertTrue(rootProject.isRoot)
    }

    @Test
    fun `should return false for isRoot when project is child project`() {
        val rootProject = ProjectBuilder.builder().withName("root").build()
        val childProject = ProjectBuilder.builder().withName("child").withParent(rootProject).build()
        assertFalse(childProject.isRoot)
    }

    @Test
    fun `should not throw exception for requireIsRoot when project is root project`() {
        val rootProject = ProjectBuilder.builder().build()
        rootProject.requireIsRoot()
    }

    @Test
    fun `should throw IllegalArgumentException for requireIsRoot when project is child project`() {
        val rootProject = ProjectBuilder.builder().withName("root").build()
        val childProject = ProjectBuilder.builder().withName("child").withParent(rootProject).build()

        val exception = assertFailsWith<IllegalArgumentException> {
            childProject.requireIsRoot()
        }

        assertEquals("Project is not root!", exception.message)
    }
}
