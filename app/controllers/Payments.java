package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import payments.PaymentService;
import payments.TransactionData;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.io.IOException;

/** Controller dealing with payments */
public class Payments extends Controller {

    private final PaymentService paymentService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public Payments(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Transfer money between accounts */
    public Result transfer() {
        // Parse incoming data
        TransactionData data;
        try {
            data = mapper.readValue(request().body().asText(), TransactionData.class);
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
            return badRequest();
        }

        // Check received data object is what we expect
        if (!data.isValid()) {
            return badRequest();
        }

        return paymentService.transfer(data)
            .map(result -> ok(Json.toJson(result)))
            .orElseGet(Results::badRequest);
    }

}
