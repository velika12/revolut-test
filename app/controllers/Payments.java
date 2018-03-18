package controllers;

import payments.PaymentService;
import payments.TransactionData;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;

/** Controller dealing with payments */
public class Payments extends Controller {

    private final PaymentService paymentService;

    @Inject
    public Payments(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Result transfer() {
        // Parse incoming data
        TransactionData data = Json.fromJson(request().body().asJson(), TransactionData.class);

        return paymentService.transfer(data)
            .map(result -> ok(Json.toJson(result)))
            .orElseGet(Results::badRequest);
    }

}
