package com.teamherb.bookstoreback.common.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
public @interface ValueOfEnum {

  Class<? extends Enum<?>> enumClass();

  String message() default "ENUM 타입을 맞춰주세요.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
