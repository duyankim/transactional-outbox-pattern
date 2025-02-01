package com.github.account.application.port.out;

import com.github.account.domain.Account;
import com.github.account.domain.Activity;

import java.util.List;

public interface UpdateAccountStatePort {

    List<Activity> updateActivities(Account account);
}
