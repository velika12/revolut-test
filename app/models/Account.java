package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

/** Basic account model */
@Entity
public class Account extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    /** Current balance of the account */
    public BigDecimal balance;

    /*@OneToMany(mappedBy = "sender")
    public List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiver")
    public List<Transaction> receivedTransactions;*/

    /** Constructor */
    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    /** Finder */
    public static final Find<Long, Account> find = new Find<Long, Account>(){};

}
