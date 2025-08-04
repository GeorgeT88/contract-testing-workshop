package com.apenlor.pactflow.student.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks parts of the code that intentionally deviate from best practices
 * for a specific reason, such as for a Proof of Concept or a temporary workaround.
 * It serves as a formal record of technical debt.
 */
@Target({ElementType.TYPE, ElementType.METHOD}) // Can be used on classes or methods
@Retention(RetentionPolicy.SOURCE) // Only needed at compile time, not at runtime!
public @interface TechnicalDebt {

    /**
     * A brief description of the issue or the anti-pattern being used.
     */
    String issue();

    /**
     * A description of the proper solution that should be implemented in the future.
     */
    String solution();
}