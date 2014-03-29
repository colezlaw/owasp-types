package org.owasp.types.financial;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.types.ParseException;

/**
 * Represents a <a href="http://en.wikipedia.org/wiki/Routing_transit_number">Routing Number</a>
 * 
 * @author colezlaw
 *
 */
public class RoutingNumber {
    private String fedRoutingSymbol;
    private String abaInstitution;
    private String checkDigit;
    
    private static final Set<CharSequence> TYPE_GOVERNMENT = new HashSet<CharSequence>();
    private static final Set<CharSequence> TYPE_PRIMARY = new HashSet<CharSequence>();
    private static final Set<CharSequence> TYPE_THRIFT = new HashSet<CharSequence>();
    private static final Set<CharSequence> TYPE_ELECTRONIC = new HashSet<CharSequence>();
    
    public enum FedReserveType {
        GOVERNMENT,
        PRIMARY,
        THRIFT,
        ELECTRONIC
    }
    
    public enum FedReserveBank {
        GOVERNMENT,
        BOSTON,
        NEW_YORK,
        PHILADELPHIA,
        CLEVELAND,
        RICHMOND,
        ATLANTA,
        CHICAGO,
        ST_LOUIS,
        MINNEAPOLIS,
        KANSAS_CITY,
        DALLAS,
        SAN_FRANCISCO
    }
    
    static {
        TYPE_GOVERNMENT.add("00");
        for (int i = 1; i <= 12; i++) {
            TYPE_PRIMARY.add(String.format("%02d", i));
        }
        for (int i = 21; i <= 32; i++) {
            TYPE_THRIFT.add(String.format("%02d", i));
        }
        for (int i = 61; i <= 72; i++) {
            TYPE_ELECTRONIC.add(String.format("%02d", i));
        }
    }
    
    /**
     * Private so you can only create these by parsing.
     */
    private RoutingNumber() {}
    
    public static final Pattern PAT_MICR = Pattern.compile("^(\\d{4})(\\d{4})(\\d)$");
    
    public static RoutingNumber parseMICR(CharSequence micr) {
        if (micr == null) {
            throw new ParseException("Null MICR");
        }
        final Matcher m = PAT_MICR.matcher(micr);
        if (! m.matches()) {
            throw new ParseException("Invalid MICR format");
        }
        
        final RoutingNumber ret = new RoutingNumber();
        ret.fedRoutingSymbol = m.group(1);
        ret.abaInstitution = m.group(2);
        ret.checkDigit = m.group(3);
        
        final String type = ret.fedRoutingSymbol.substring(0,2);
        if (! TYPE_GOVERNMENT.contains(type)
                && ! TYPE_PRIMARY.contains(type)
                && ! TYPE_THRIFT.contains(type)
                && ! TYPE_ELECTRONIC.contains(type)) {
            throw new ParseException("Invalid Federal Routing Symbol");
        }
        
        if (Integer.parseInt(ret.checkDigit) != ret.calculateCheckDigit()) {
            throw new ParseException("Check Digit not correct");
        }
        
        return ret;
    }
    
    /**
     * Returns the Federal Reserve Routing Symbol.
     * 
     * @return the Federal Reserve Routing Symbol
     */
    public String getFedRoutingSymbol() {
        return fedRoutingSymbol;
    }
    
    /**
     * Gets the ABA Institution Identifier.
     * 
     * @return the ABA Institution Identifier
     */
    public String getAbaInstitution() {
        return abaInstitution;
    }
    
    /**
     * Gets the Check Digit.
     * 
     * @return the Check Digit
     */
    public String getCheckDigit() {
        return checkDigit;
    }
    
    /**
     * Calculates what the check digit should be.
     * 
     * @return the calculated check digit.
     */
    public int calculateCheckDigit() {
        // Formula for the MICR form is:
        // 10 - ((3(d1 + d4 + d7) + 7(d2 + d5 + d8) + (d3 + d6)) % 10)
        return 10 - (
                3 * Integer.parseInt(fedRoutingSymbol.substring(0,1))
                + 7 * Integer.parseInt(fedRoutingSymbol.substring(1,2))
                + Integer.parseInt(fedRoutingSymbol.substring(2,3))
                + 3 * Integer.parseInt(fedRoutingSymbol.substring(3,4))
                + 7 * Integer.parseInt(abaInstitution.substring(0,1))
                + Integer.parseInt(abaInstitution.substring(1,2))
                + 3 * Integer.parseInt(abaInstitution.substring(2,3))
                + 7 * Integer.parseInt(abaInstitution.substring(3,4))
        ) % 10;
    }

    /**
     * Returns this routing number in MICR (XXXXYYYYC) format.
     * 
     * @return the routing number in MICR format
     */
    public String toMICRString() {
        return String.format("%4s%4s%1s", fedRoutingSymbol, abaInstitution, checkDigit);
    }
    
    /**
     * Returns the type of federal reserve
     */
}
