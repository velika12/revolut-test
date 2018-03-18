package payments;

import java.util.Optional;

/** Transaction repository interface */
public interface TransactionRepository {

    public Optional<TransactionData> create(TransactionData data);

}
