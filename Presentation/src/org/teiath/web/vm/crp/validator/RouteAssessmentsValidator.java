package org.teiath.web.vm.crp.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

public class RouteAssessmentsValidator
		extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		Integer val = (Integer) ctx.getProperty().getValue();
		System.out.println(val);
		if (val == null || val == 0) {
			addInvalidMessage(ctx, "fx_rating", "Η βαθμολόγηση είναι υποχρεωτική");
		}
	}
}

