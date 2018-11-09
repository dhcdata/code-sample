package com.dhcdata.altsource.controller.shell.provider;

import java.util.List;
import java.util.stream.Collectors;

import com.dhcdata.altsource.model.finance.FinanceFactory;
import com.dhcdata.altsource.model.finance.account.TransactionAccount;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProvider;
import org.springframework.stereotype.Component;

@Component
class AccountValueProvider implements ValueProvider {

    private FinanceFactory finFactory;

    AccountValueProvider(FinanceFactory financeFactory) {
        this.finFactory = financeFactory;
    }

    @Override
    public boolean supports(MethodParameter parameter, CompletionContext completionContext) {
        return parameter.getParameterType().isAssignableFrom(TransactionAccount.class);
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext,
            String[] hints) {
        String currentInput = completionContext.currentWordUpToCursor();
        return finFactory.getAccounts().stream().filter(a -> a.getId().toString().contains(currentInput))
                .map(p -> String.format("[%s] %s", p.getId(), p.getBalance().toString())).map(CompletionProposal::new)
                .collect(Collectors.toList());

    }

}
