package it;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import payments.AccountRepository;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Result;
import play.test.WithApplication;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class IntegrationTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testTransferReturnsOk() {
        // Create accounts
        AccountRepository repository = app.injector().instanceOf(AccountRepository.class);
        Long senderId = repository.create(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        Long receiverId = repository.create(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));

        // Request body
        String body = String.format("{\"sender_id\":%d,\"receiver_id\":%d,\"amount\":%d}", senderId, receiverId, 4000);
        JsonNode json = Json.parse(body);

        // Make a request for the transfer action
        Call call = controllers.routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyJson(json));

        // Check the response is ok
        assertThat(result.status(), equalTo(OK));

        // Check data in the database changed
        assertThat(BigDecimal.valueOf(6000), equalTo(repository.getBalance(senderId)));
        assertThat(BigDecimal.valueOf(4000), equalTo(repository.getBalance(receiverId)));
    }

}
