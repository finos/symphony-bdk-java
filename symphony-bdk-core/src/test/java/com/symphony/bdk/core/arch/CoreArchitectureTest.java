package com.symphony.bdk.core.arch;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.conditions.ArchConditions.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

import com.symphony.bdk.core.service.user.mapper.UserDetailMapper;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import org.apiguardian.api.API;
import org.slf4j.Logger;

/**
 * Validate basic architecture rules using <a href="https://www.archunit.org/getting-started">ArchUnit</a>.
 *
 * <p>
 *   Rules currently checked:
 *   <ul>
 *     <li>all classes must be annotated with @API</li>
 *     <li>loggers must be 'private static final'</li>
 *     <li>Guava usage is not allowed (at the moment). This way we can control when and if we want to import it</li>
 *     <li>usage of System.out and System.err is not allowed</li>
 *     <li>usage of java.util.logging is not allowed</li>
 *   </ul>
 * </p>
 */
@AnalyzeClasses(
    packages = "com.symphony.bdk.core",
    importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
    }
)
public class CoreArchitectureTest {

  @ArchTest
  void classes_should_be_annotated_with_api_guardian(JavaClasses classes) {
    classes()
        .that()
          .doNotImplement(UserDetailMapper.class) // MapStruct generated code cannot be annotated with @API
        .and()
          .areNotAnonymousClasses()
        .should()
          .beAnnotatedWith(API.class)
    .check(classes);
  }

  @ArchTest
  void loggers_should_be_private_static_final(JavaClasses classes) {
    fields()
        .that()
          .haveRawType(Logger.class)
        .should()
          .bePrivate()
        .andShould()
          .beStatic()
        .andShould()
          .beFinal()
    .check(classes);
  }

  public static final ArchCondition<JavaClass> USE_GUAVA =
      dependOnClassesThat(resideInAPackage("com.google.guava"))
          .and(resideInAnyPackage("com.google.common"))
          .as("use Guava");

  @ArchTest
  void no_guava(JavaClasses classes) {
    noClasses().should(USE_GUAVA)
        .because("Guava is not allowed at the moment").check(classes);
  }

  @ArchTest
  void no_access_to_standard_streams(JavaClasses classes) {
    NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(classes);
  }

  @ArchTest
  void no_java_util_logging(JavaClasses classes) {
    NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.check(classes);
  }
}
