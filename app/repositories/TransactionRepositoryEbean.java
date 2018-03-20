package repositories;

import com.avaje.ebean.Ebean;
import models.Account;
import models.Transaction;
import payments.TransactionData;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/** Ebean implementation of transaction repository */
@Singleton
public class TransactionRepositoryEbean implements TransactionRepository {

    @Inject
    public TransactionRepositoryEbean(EbeanConfig ebeanConfig) {
    }

    @Override
    public CompletionStage<Optional<TransactionData>> createTransaction(TransactionData data) {
        return supplyAsync(() -> {
            // Find sender and receiver in database
            Account sender = Account.find.byId(data.getSenderId());
            Account receiver = Account.find.byId(data.getReceiverId());

            if (null == sender || null == receiver) {
                return Optional.empty();
            }

            // Check sender has enough money on balance
            if (sender.balance.compareTo(data.getAmount()) < 0) {
                return Optional.empty();
            }

            // Transfer money between accounts
            sender.balance = sender.balance.subtract(data.getAmount());
            receiver.balance = receiver.balance.add(data.getAmount());

            // Create a new transaction record
            Transaction transaction = new Transaction(data.getAmount(), sender, receiver);

            // Save all changes to database in one transaction
            // If transaction cannot be commited due to some exception,
            // it is rollbacked and this exception is thrown
            Ebean.execute(() -> {
                transaction.save();
                sender.save();
                receiver.save();
            });

            // Return created transaction data on success
            return Optional.of(new TransactionData(transaction));
        });
    }

}
