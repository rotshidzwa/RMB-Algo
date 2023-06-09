package za.co.rmb.algo.app.model;

import za.co.rmb.algo.app.OperationType;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private long id;
    private BigDecimal price;
    private int quantity;
    private SIDE side;
    public static final Order EMPTY = new Builder().build();



    public static Order copyOf(Order order) {
        return new Builder()
                .id(order.id)
                .operationType(order.operationType)
                .price(order.price)
                .volume(order.volume)
                .build();
    }

    public static class Builder {
        public BigDecimal price;
        private long id;

        private OperationType operationType;


        private long volume;

        public Builder() {
        }

        public Order.Builder id(long id) {
            this.id = id;
            return this;
        }

        public Order.Builder operationType(OperationType operationType) {
            this.operationType = operationType;
            return this;
        }

        public Order.Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Order.Builder volume(long volume) {
            this.volume = volume;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    private Order(Order.Builder builder) {
        this.id = builder.id;
        this.operationType = builder.operationType;
        this.price = builder.price;
        this.volume = builder.volume;
    }


    private OperationType operationType;


    private long volume;

    private Date timestamp;

    public void decreaseVolume(long delta) {
        volume = getVolume() - delta;
    }

    @Override
    public String toString() {
        return String.format("[%s] %d; %f; %d", operationType.toString(), id, price, volume);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public long getId() {
        return id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getVolume() {
        return volume;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
