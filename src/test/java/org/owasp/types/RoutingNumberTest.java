package org.owasp.types;

import org.junit.Test;
import static org.junit.Assert.*;
import org.owasp.types.financial.RoutingNumber;

public class RoutingNumberTest {
    @Test(expected=ParseException.class)
    public void testInvalidRoutingNumber() {
        RoutingNumber rn = RoutingNumber.parseMICR("1234");
    }
    
    @Test(expected=ParseException.class)
    public void testNullRoutingNumber() {
        RoutingNumber rn = RoutingNumber.parseMICR(null);
    }
    
    @Test(expected=ParseException.class)
    public void testInvalidCheckDigit() {
        RoutingNumber rn = RoutingNumber.parseMICR("123456789");
    }

    @Test
    public void testInvalidFedNumber() {
        try {
            RoutingNumber rn = RoutingNumber.parseMICR("130000022");
            fail("Expected Bad Federal Reserve Number");
        } catch (ParseException pe) {
            assertEquals("Invalid Federal Routing Symbol", pe.getMessage());
        }
    }
    
    @Test
    public void testValidRoutingNumber() {
        RoutingNumber rn = RoutingNumber.parseMICR("111000025");
        assertEquals("Federal Reserve Routing Symbol", "1110", rn.getFedRoutingSymbol());
        assertEquals("ABA Institution Identifier", "0002", rn.getAbaInstitution());
        assertEquals("Check Digit", "5", rn.getCheckDigit());
        assertEquals("111000025", rn.toMICRString());
    }
}