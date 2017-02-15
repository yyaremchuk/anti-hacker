/**
 * 
 */
package com.somecompany.detector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author yyaremchuk
 *
 */
public class ValidatorTest {

	@Test
	public void validateLine() {
		// line in the correct format
		final Validator validator = new Validator();
		
		assertFalse(validator.validate(null));
		assertFalse(validator.validate(""));

		assertTrue(validator.validate("80.238.9.179,133612947,SIGNIN_SUCCESS,Dave.Branning"));

		// username is missing
		assertFalse(validator.validate("80.238.9.179,133612947,SIGNIN_SUCCESS"));
		
		// some incorrect IP
		assertFalse(validator.validate("blah-blah,133612947,SIGNIN_SUCCESS,Dave.name"));
		assertFalse(validator.validate("359.112.1.0,133612947,SIGNIN_SUCCESS,Dave.name"));

		// some incorrect TIME
		assertFalse(validator.validate("80.238.9.179,sfsdfhlsk,SIGNIN_SUCCESS,Dave.name"));

		// some incorrect Status
		assertFalse(validator.validate("80.238.9.179,133612947,NOT_SUCCESS,Dave.name"));
	}
}
