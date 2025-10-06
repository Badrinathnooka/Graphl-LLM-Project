package demo;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ConversionFailedException) {
            return GraphqlErrorBuilder.newError(env).message("Invalid input value").build();
        }
        return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
    }
}
