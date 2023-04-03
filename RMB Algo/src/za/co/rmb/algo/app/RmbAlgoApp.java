package za.co.rmb.algo.app;

import za.co.rmb.algo.app.model.AddOrderMessage;
import za.co.rmb.algo.app.model.Order;
import za.co.rmb.algo.app.model.OrderMessage;
import za.co.rmb.algo.app.model.OrderReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.function.Function;

public class RmbAlgoApp {


    public static void main(String[] args) {

        Path file = Paths.get("orders.xml");
        processOrders(file);

    }

    private static void processOrders(Path ordersFile) {
        try (OrderReader reader = OrderReader.from(ordersFile.toFile())) {
            RmbAlgoOrderPool bookPool = new RmbAlgoOrderPool();

            // Read and process one order at a time
            while (reader.hasNext()) {
                OrderMessage msg = reader.next();
                if (OrderMessage.EMPTY != msg) {
                    Order order = null;
                    if (msg.isDeleteMessage()) {
                        order = new Order.Builder()
                                .id(msg.getOrderId())
                                .operationType(OperationType.DELETE)
                                .build();
                    } else if (msg.isAddMessage()) {
                        order  = MESSAGE_TO_ORDER.apply(msg.asAddMessage());
                    }

                    bookPool.process(msg.getBookId(), order);
                }
            }

            bookPool.finishProcessing();

            // Print results
            bookPool.printBookContent(System.out);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static final Function<AddOrderMessage, Order> MESSAGE_TO_ORDER = new Function<AddOrderMessage, Order>() {
        @Override
        public Order apply(AddOrderMessage message) {
            Order order = null;

            if (message != null) {
                order = new Order.Builder()
                        .id(message.getOrderId())
                        .operationType(OperationType.valueOf(message.getOperation()))
                        .price(message.getPrice())
                        .volume(message.getVolume())
                        .build();
            }

            return order;
        }
    };
}
