package garbagecollector;

import javax.annotation.Resource;
import javax.jms.*;

public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "redpod1")
    private static Queue queue1;

    @Resource(lookup = "redpod11")
    private static Queue queue2;

    @Resource(lookup = "redpod2")
    private static Queue queueB1;

    @Resource(lookup = "redpod22")
    private static Queue queueB2;

    public static void main(String[] args) {
        flushQueue(queue1, "redpod1");
        flushQueue(queue2, "redpod11");
        flushQueue(queueB1, "redpod2");
        flushQueue(queueB2, "redpod22");
    }

    private static void flushQueue(Queue queue, String queueName) {
        System.out.println("Flushing queue: " + queueName);

        try (JMSContext context = connectionFactory.createContext()) {
            JMSConsumer consumer = context.createConsumer(queue);

            int messageCount = 0;
            Message message;
            while ((message = consumer.receiveNoWait()) != null) {
                messageCount++;
                System.out.println("Message removed: " + message.getJMSMessageID());
            }

            if (messageCount == 0) {
                System.out.println("Queue " + queueName + " is already empty.");
            } else {
                System.out.println("Flushed " + messageCount + " messages from queue: " + queueName);
            }
        } catch (Exception e) {
            System.err.println("Error while flushing queue: " + queueName);
            e.printStackTrace();
        }
    }
}
