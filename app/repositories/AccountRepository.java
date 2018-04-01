package repositories;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository {

    public Optional<Long> createAccount(BigDecimal balance);

    public Optional<BigDecimal> getBalance(Long accountId);

}
