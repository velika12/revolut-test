package payments;

import com.avaje.ebean.Ebean;
import models.Account;
import models.Transaction;
import play.Logger;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/** Ebean implementation of transaction repository */
@Singleton
public class TransactionRepositoryEbean implements TransactionRepository {

    @Inject
    public TransactionRepositoryEbean(EbeanConfig ebeanConfig) {}

    @Override
    public Optional<TransactionData> create(TransactionData data) {
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
    }

}
