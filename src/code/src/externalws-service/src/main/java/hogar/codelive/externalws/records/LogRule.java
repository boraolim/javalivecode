package hogar.codelive.externalws.records;

import java.util.function.Consumer;
import java.util.function.Predicate;

public record LogRule
(
    Predicate<RequestLogContext> predicate, 
    Consumer<RequestLogContext> action
) { }
