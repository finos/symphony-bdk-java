package com.symphony.bdk.examples.spring.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.parsing.Cashtag;
import com.symphony.bdk.core.activity.parsing.Hashtag;
import com.symphony.bdk.core.activity.parsing.Mention;
import com.symphony.bdk.spring.annotation.Slash;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <a href="https://www.archunit.org/getting-started">ArchUnit</a> Demo.
 *
 * <p>
 *    Description of the tests:
 *    <ol>
 *      <li>methods annotated with {@link Slash} must have 1 parameter of type {@link CommandContext}</li>
 *      <li>{@link AbstractActivity} classes must be annotated with {@link Service} or {@link Component}</li>
 *    </ol>
 * </p>
 */
@AnalyzeClasses(packages = "com.symphony.bdk.examples.spring")
public class ArchitectureTest {

  @ArchTest
  void all_slash_methods_must_have_at_least_one_command_context_parameter(JavaClasses classes) {
    methods()
        .that()
          .areAnnotatedWith(Slash.class)
        .should()
          .haveRawParameterTypes(DescribedPredicate.describe("First parameter must be of type CommandContext, others of type String, Mention, Cashtag or Hashtag",
              l -> l.get(0).isEquivalentTo(CommandContext.class)
                  && l.subList(1, l.size()).stream().allMatch(t -> t.isEquivalentTo(String.class) || t.isEquivalentTo(
                  Mention.class) ||t.isEquivalentTo(Cashtag.class) ||t.isEquivalentTo(Hashtag.class))))
    .check(classes);
  }

  @ArchTest
  void an_activity_must_be_service_or_component(JavaClasses classes) {
    classes()
        .that()
          .areAssignableTo(AbstractActivity.class)
        .should()
          .beAnnotatedWith(Component.class)
            .orShould()
          .beAnnotatedWith(Service.class)
    .check(classes);
  }
}
