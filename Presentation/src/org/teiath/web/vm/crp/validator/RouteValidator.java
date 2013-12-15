package org.teiath.web.vm.crp.validator;

import org.teiath.data.domain.crp.Route;
import org.teiath.service.crp.EditRouteService;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.select.annotation.WireVariable;

public class RouteValidator
		extends AbstractValidator {

	@WireVariable
	private EditRouteService editRouteService;

	@Override
	public void validate(ValidationContext ctx) {
		Route route = new Route();
		route.setTotalSeats((Integer) ctx.getProperties("totalSeats")[0].getValue());
	}
}

