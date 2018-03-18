package payments;

import models.Account;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

/**  */
public class AccountRepositoryEbean implements AccountRepository {

    @Inject
    public AccountRepositoryEbean(EbeanConfig ebeanConfig) {}

    @Override
    public Optional<Long> create(BigDecimal balance) {
        Account account = new Account(balance);
        account.save();

        return Optional.of(account.id);
    }

    @Override
    public Optional<BigDecimal> getBalance(Long id) {
        Account account = Account.find.byId(id);

        if (null == account) {
            return Optional.empty();
        }

        return Optional.of(account.balance);
    }

}
