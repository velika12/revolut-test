package repositories;

import models.Account;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

/** Ebean implementation of account repository */
public class AccountRepositoryEbean implements AccountRepository {

    @Inject
    public AccountRepositoryEbean(EbeanConfig ebeanConfig) {}

    @Override
    public Optional<Long> createAccount(BigDecimal balance) {
        Account account = new Account(balance);
        account.save();

        return Optional.of(account.id);
    }

    @Override
    public Optional<BigDecimal> getBalance(Long accountId) {
        Account account = Account.find.byId(accountId);

        if (null == account) {
            return Optional.empty();
        }

        return Optional.of(account.balance);
    }

}
