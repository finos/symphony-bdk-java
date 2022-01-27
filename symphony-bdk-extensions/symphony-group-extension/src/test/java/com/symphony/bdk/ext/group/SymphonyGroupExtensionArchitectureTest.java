package com.symphony.bdk.ext.group;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.apiguardian.api.API;

/**
 * Validate basic architecture rules using <a href="https://www.archunit.org/getting-started">ArchUnit</a>.
 *
 * Rule currently checked: all classes must be annotated with @API
 */
@AnalyzeClasses(
    packages = "com.symphony.bdk.ext.group",
    importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
    }
)
public class SymphonyGroupExtensionArchitectureTest {

  @ArchTest
  void classes_should_be_annotated_with_api_guardian(JavaClasses classes) {
    classes()
        .that()
        .areNotPrivate()
        .and()
        .resideOutsideOfPackage("com.symphony.bdk.ext.group.gen..") // exclude generated classes
        .and()
        .areNotAnonymousClasses()
        .should()
        .beAnnotatedWith(API.class)
        .check(classes);
  }
}
