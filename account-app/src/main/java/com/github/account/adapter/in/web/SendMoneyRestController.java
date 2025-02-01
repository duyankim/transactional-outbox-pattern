package com.github.account.adapter.in.web;

import com.github.account.application.port.in.SendMoneyCommand;
import com.github.account.application.port.in.SendMoneyUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class SendMoneyRestController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping
    public void sendMoney(@RequestBody SendMoneyCommand sendMoneyCommand) {
        sendMoneyUseCase.sendMoney(sendMoneyCommand);
    }
}
