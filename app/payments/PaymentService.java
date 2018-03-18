package payments;

import java.util.Optional;

/** Payment service interface */
public interface PaymentService {

    /** Transfer money between accounts */
    public Optional<TransactionData> transfer(TransactionData data);

}
