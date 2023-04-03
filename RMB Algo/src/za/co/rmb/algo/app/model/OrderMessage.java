package za.co.rmb.algo.app.model;

public abstract class OrderMessage {
    public static final OrderMessage EMPTY = new OrderMessage() {
        @Override
        public boolean isDeleteMessage() {
            return false;
        }

        @Override
        public boolean isAddMessage() {
            return false;
        }

        public String toString() {
            return "EMPTY";
        }
    };

    private String bookId;

    private long orderId;

    @Override
    public String toString() {
        return toString();
    }

    public abstract boolean isAddMessage();

    public abstract boolean isDeleteMessage();

    public AddOrderMessage asAddMessage() {
        if(this instanceof AddOrderMessage) {
            return (AddOrderMessage) this;
        }
        return null;
    }

    public DeleteOrderMessage asDeleteOrderMessage() {
        if(this instanceof DeleteOrderMessage) {
            return (DeleteOrderMessage) this;
        }
        return null;

    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }
}
