package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
/**
 * WARNING   When using a custom AggregationStrategy with the Splitter, it’s
important to know that you’re responsible for handling exceptions. If you
don’t propagate the exception back, the Splitter will assume you’ve handled
the exception and will ignore it.
 */
public class MyPropagateFailureAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                     // check if there was an exception thrown

        if (newExchange.getException() != null) {
            if (oldExchange == null) {
                return newExchange;
            } else {
                oldExchange.setException(newExchange.getException()); // Propagates exception
                return oldExchange;
            }
        }
        if (oldExchange == null) {
            // this is the first time so no existing aggregated exchange
            return newExchange;
        }

        // append the new word to the existing
        String body = newExchange.getIn().getBody(String.class);
        String existing = oldExchange.getIn().getBody(String.class);

        oldExchange.getIn().setBody(existing + "+" + body);
        return oldExchange;
    }
}
