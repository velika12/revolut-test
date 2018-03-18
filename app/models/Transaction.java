package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/** Basic transaction model */
@Entity
public class Transaction extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    /** Amount of money sent */
    public BigDecimal amount;

    @ManyToOne
    public Account sender;

    @ManyToOne
    public Account receiver;

    public Transaction(BigDecimal amount, Account sender, Account receiver) {
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
    }

    /** Finder */
    public static final Find<Long, Transaction> find = new Find<Long, Transaction>(){};

}
