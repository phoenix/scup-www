package edu.scup.data.excel;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelHeader {
    String title() default "";

    int headerOrder();

    String[] validData() default {};

    String dict() default "";

    String formatter() default "";
}
