package com.symphony.bdk.core.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.symphony.bdk.core.service.user.mapper.UserDetailMapper;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.apiguardian.api.API;

/**
 * Validate basic architecture rules using <a href="https://www.archunit.org/getting-started">ArchUnit</a>.
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
}
