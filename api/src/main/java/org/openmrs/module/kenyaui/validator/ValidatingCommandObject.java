/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaui.validator;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.OpenmrsData;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Abstract base class for command objects that can validate themselves
 */
public abstract class ValidatingCommandObject implements Validator {

	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return getClass().isAssignableFrom(clazz);
	}

	/**
	 * Rejects the specified property if it is empty
	 * @param errors the errors
	 * @param field the field name
	 */
	public void require(Errors errors, String field) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, KenyaUiConstants.MODULE_ID + ".error.required");
	}

	/**
	 *
	 * @param errors
	 * @param fields
	 */
	public void requireAny(Errors errors, String... fields) {
		for (String field : fields) {
			Object value = errors.getFieldValue(field);
			if (value != null && !"".equals(value)) {
				return;
			}
		}
		errors.rejectValue(fields[0], KenyaUiConstants.MODULE_ID + ".error.required");
	}

	/**
	 *
	 * @param errors
	 * @param field
	 */
	public void validateField(Errors errors, String field) {
		validateField(errors, field, false);
	}

	/**
	 *
	 * @param errors
	 * @param field
	 * @param required
	 */
	public void validateField(Errors errors, String field, boolean required) {
		Object value = errors.getFieldValue(field);
		if (value == null) {
			if (required) {
				errors.rejectValue(field, KenyaUiConstants.MODULE_ID + ".error.required");
			} else {
				return;
			}
		}
		errors.pushNestedPath(field);
		ValidateUtil.validate(value, errors);
		errors.popNestedPath();
	}

	/**
	 *
	 * @param data
	 */
	public void voidData(OpenmrsData data) {
		if (!data.isVoided()) {
			data.setVoided(true);
			data.setDateVoided(new Date());
			data.setVoidedBy(Context.getAuthenticatedUser());
		}
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @param props
	 * @return
	 */
	public boolean anyChanges(Object from, Object to, String... props) {
		if (from == null && to == null) {
			return false;
		} else if (from == null || to == null) {
			return true;
		}
		for (String prop : props) {
			try {
				Object fromVal = PropertyUtils.getProperty(from, prop);
				Object toVal = PropertyUtils.getProperty(to, prop);
				if (!OpenmrsUtil.nullSafeEquals(fromVal, toVal)) {
					return true;
				}
			} catch (Exception ex) {
				throw new IllegalArgumentException("Error getting property: " + prop, ex);
			}
		}
		return false;
	}
}