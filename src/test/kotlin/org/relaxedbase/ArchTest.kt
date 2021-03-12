package org.relaxedbase

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.relaxedbase")

        noClasses()
            .that()
                .resideInAnyPackage("org.relaxedbase.service..")
            .or()
                .resideInAnyPackage("org.relaxedbase.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..org.relaxedbase.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
