package com.tddapps.handlers;

import com.tddapps.actions.StatusGetAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerSupplier;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;
import com.tddapps.ioc.IocContainer;
import lombok.val;

@SuppressWarnings("unused")
public class StatusGet extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        val action = IocContainer.getInstance().Resolve(StatusGetAction.class);
        return new HttpJsonControllerSupplier(action);
    }
}
