package com.aaden.pay.service.biz.route;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractRoute<T> implements ApplicationContextAware {

    protected Collection<T> thirdBanks;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, T> thirdBankVerifyBeansMap = applicationContext.getBeansOfType(getClazz());

        this.thirdBanks = thirdBankVerifyBeansMap.values();
    }

    abstract Class<T> getClazz();
}
