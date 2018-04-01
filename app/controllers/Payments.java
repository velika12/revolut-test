package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import payments.PaymentService;
import payments.TransactionData;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

/** Controller dealing with payments */
public class Payments extends Controller {

    private final PaymentService paymentService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public Payments(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Transfer money between accounts */
    public CompletionStage<Result> transfer() {
        TransactionData data;
        try {
            data = mapper.readValue(request().body().asJson().toString(), TransactionData.class);
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
            return completedFuture(badRequest());
        }

        if (!data.isValid()) {
            return completedFuture(badRequest());
        }

        return paymentService.transfer(data)
            .thenApply(result ->
                result.map(
                    r -> ok(Json.toJson(r))
                ).orElseGet(Results::badRequest)
            );
    }

}
