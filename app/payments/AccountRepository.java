package payments;

import java.math.BigDecimal;
import java.util.Optional;

/**  */
public interface AccountRepository {

    public Optional<Long> create(BigDecimal balance);

    public Optional<BigDecimal> getBalance(Long id);

}
