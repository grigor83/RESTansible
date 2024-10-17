package mtel.models;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({FIELD,METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPhoneNumber {
    String message() default "Phone number is not valid!";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
