package com.symphony.bdk.ext.group;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleName;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.apiguardian.api.API;

/**
 * Validate basic architecture rules using <a href="https://www.archunit.org/getting-started">ArchUnit</a>.
 *
 * Rules currently checked:
 * <ul>
 *   <li>all classes must be annotated with @API</li>
 *   <li>JSR-305 nullability annotations are not allowed (superseded by JSpecify)</li>
 * </ul>
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
        .and(not(simpleName("package-info")))
        .should()
        .beAnnotatedWith(API.class)
        .check(classes);
  }

  @ArchTest
  void no_jsr305_nullability_annotations(JavaClasses classes) {
    noClasses()
        .that()
          .resideOutsideOfPackage("com.symphony.bdk.ext.group.gen..") // generator's jersey2 templates still emit JSR-305
        .should()
          .dependOnClassesThat(resideInAPackage("javax.annotation"))
        .because("JSR-305 is unmaintained; nullability is expressed with JSpecify (@Nullable / @NullMarked)")
        .check(classes);
  }
}
