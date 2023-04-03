package za.co.rmb.algo.app;
import za.co.rmb.algo.app.model.Order;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
public class OrderBook implements Runnable{
    String id;

    List<Order> buys;

    List<Order> sells;

    Map<Long, Order> ordersMemory = new HashMap<>();

    BlockingQueue<Order> queue;

    public OrderBook(String id, BlockingQueue<Order> queue) {
        this.queue = queue;
        this.id = id;

        buys = new LinkedList();
        sells = new LinkedList();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Order order = queue.take();

                // End of processing?
                if (order == Order.EMPTY) {
                    return;
                }

                switch (order.getOperationType()) {
                    case BUY:
                        buy(order);
                        break;
                    case SELL:
                        sell(order);
                        break;
                    case DELETE:
                        remove(order.getId());
                        break;
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /** Perform a buying operation. */
    private void buy(Order order) {
        //Preconditions.checkArgument(order != null, "Order cannot be null");
        //Preconditions.checkArgument(order.getOperationType() == OperationType.BUY, "Operation must be of type BUY");

        processBuy(order);

        if (order.getVolume() > 0) {
            ordersMemory.put(order.getId(), order);

            buys.add(order);
            Collections.sort(buys, new Comparator<Order>() {
                @Override
                public int compare(Order first, Order second) {
                    if (first.getPrice().equals(second.getPrice())) {
                        return first.getTimestamp().compareTo(second.getTimestamp());
                    } else {
                        return -1 * first.getPrice().compareTo(second.getPrice());
                    }
                }
            });
        }
    }

    private void sell(Order order) {

if(order !=null) {
    if (order.getOperationType() == OperationType.SELL) {
        processSell(order);

        if (order.getVolume() > 0) {
            ordersMemory.put(order.getId(), order);

            sells.add(order);

            Collections.sort(sells, new Comparator<Order>() {
                @Override
                public int compare(Order first, Order second) {
                    if (first.getPrice().equals(second.getPrice())) {
                        return first.getTimestamp().compareTo(second.getTimestamp());
                    } else {
                        return first.getPrice().compareTo(second.getPrice());
                    }
                }
            });
        }
    }
}
    }

    private void processSell(Order sell) {
        for (Order buy : buys) {
            if (buy.getPrice().compareTo(sell.getPrice()) < 0
                    || sell.getVolume() == 0) {
                break;
            }

            long contractVolume = Math.min(sell.getVolume(), buy.getVolume());
            sell.decreaseVolume(contractVolume);
            buy.decreaseVolume(contractVolume);
        }

        buys = clearEmptyOrders(buys);
    }


    private void processBuy(Order buy) {
        for (Order sell : sells) {
            if (sell.getPrice().compareTo(buy.getPrice()) > 0
                    || buy.getVolume() == 0) {
                break;
            }

            long contractVolume = Math.min(buy.getVolume(), sell.getVolume());
            buy.decreaseVolume(contractVolume);
            sell.decreaseVolume(contractVolume);
        }

        sells = clearEmptyOrders(sells);
    }

    List<Order> clearEmptyOrders(List<Order> orders) {
        int index = 0;

        for (Order o : orders) {
            if (o.getVolume() > 0L) {
                break;
            }

            index++;
            this.ordersMemory.remove(o.getId());
        }

        int lastIndex = Math.max(0, orders.size());

        return new ArrayList(orders.subList(Math.min(index, lastIndex), lastIndex));
    }

    public void remove(Long orderId) {
        if (ordersMemory.containsKey(orderId)) {
            Order toBeRemoved = ordersMemory.remove(orderId);
            if (OperationType.BUY == toBeRemoved.getOperationType()) {
                buys.remove(toBeRemoved);
            } else if (OperationType.SELL == toBeRemoved.getOperationType()){
                sells.remove(toBeRemoved);
            }
        }
    }



    public List<Order> getBuys() {
        return buys;
    }

    public List<Order> getSells() {
        return sells;
    }

    public Order getOrder(Long orderId) {
        return ordersMemory.get(orderId);
    }
}
