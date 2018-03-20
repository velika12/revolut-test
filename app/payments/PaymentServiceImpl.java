package payments;

import play.Logger;
import repositories.TransactionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/** Simple implementation of payment service */
@Singleton
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository repository;

    @Inject
    public PaymentServiceImpl(TransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletionStage<Optional<TransactionData>> transfer(TransactionData data) {
        return repository.createTransaction(data);
    }

}
