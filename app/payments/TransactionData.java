package payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.Transaction;

import java.math.BigDecimal;

/** Data transfer object for a transaction */
public class TransactionData {

    /** Transaction id */
    private Long id;

    private Long senderId;

    private Long receiverId;

    /** Amount of money sent */
    private BigDecimal amount;

    public TransactionData() {}

    public TransactionData(Transaction transaction) {
        this.id = transaction.id;
        this.senderId = transaction.sender.id;
        this.receiverId = transaction.receiver.id;
        this.amount = transaction.amount;
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @JsonIgnore
    public boolean isValid() {
        return (null != senderId && null != receiverId && null != amount);
    }

}
