package za.co.rmb.algo.app.model;

public class DeleteOrderMessage extends OrderMessage {

    @Override
    public boolean isAddMessage() {
        return false;
    }

    @Override
    public boolean isDeleteMessage() {
        return true;
    }

}
