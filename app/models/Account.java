package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/** Basic account model */
@Entity
public class Account extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    /** Current balance */
    public BigDecimal balance;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public static final Find<Long, Account> find = new Find<Long, Account>(){};

}
