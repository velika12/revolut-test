package payments;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/** Payment service interface */
public interface PaymentService {

    /** Transfer money between accounts */
    public CompletionStage<Optional<TransactionData>> transfer(TransactionData data);

}
