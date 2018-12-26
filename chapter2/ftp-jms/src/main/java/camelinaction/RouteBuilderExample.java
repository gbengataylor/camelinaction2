package camelinaction;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.component.jms.JmsComponent;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class RouteBuilderExample {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();
        
                // connect to embedded ActiveMQ JMS broker
                ConnectionFactory connectionFactory = 
                new ActiveMQConnectionFactory("vm://localhost");
            context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                // try auto complete in your IDE on the line below
                from("ftp:://rider.com/orders?username=rider&password=secret"). // consumer
                // add process
                    process(new Processor(){ //processor
                    
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            System.out.println("We just downloaded: "
                            + exchange.getIn().getHeader("CamelFileName"));
                            // get the In message in that the consumer got in the exchange
                            /**
                             * NOTE   Many components, such as FileComponent and FtpComponent , set useful
headers describing the payload on the incoming message. In the previous exam-
ple, you used the CamelFileName header to retrieve the filename of the file that
was downloaded via FTP. The component pages of the online documentation
contain information about the headers set for each individual component. You’ll
find information about the FTP component at http://camel.apache.org/ftp.
html
                             */
                        }
                    }).
                    to("jms:incomingOrders"); // producer
                    /*
                    One thing you may have noticed is that we didn’t do any conversion from the FTP file
                    type to the JMS message type—this was done automatically by Camel’s type-converter
                    facility. You can force type conversions to occur at any time during a route, but often
                    you don’t have to worry about them at all. Data transformation and type conversion is
                    covered in detail in chapter 3
*/
            }
        });

        context.start();
        Thread.sleep(10000);
        context.stop();
    }
}
