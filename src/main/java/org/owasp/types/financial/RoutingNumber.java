package org.owasp.types.financial;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.types.ParseException;

/**
 * Represents a <a href="http://en.wikipedia.org/wiki/Routing_transit_number">Routing Number</a>.
 * NOTE: This is not yet complete as there are historical issues with the handling of thrifts and
 * other assigment issues. Currently, it will fail for internal use numbers (50-59), travelers checks (80),
 * and it's possible there may be legacy institutions in the 81-92 series.
 * 
 * Furthermore, while a routing number from this may be syntactically valid, it doesn't mean that
 * the number is an actual assigned routing number.
 * 
 * @author colezlaw
 *
 */
public class RoutingNumber {
    private String fedRoutingSymbol;
    private String abaInstitution;
    private String checkDigit;
    private String prefix;
    
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
    
    /**
     * A valid pattern for MICR form numbers.
     */
    public static final Pattern PAT_MICR = Pattern.compile("^(\\d{4})(\\d{4})(\\d)$");
    
    /**
     * A valid pattern for the fraction form.
     */
    public static final Pattern PAT_FRACTION = Pattern.compile("^(([1-9]|[1-9]\\d)-)?(\\d{1,4})/(\\d{1,4})$");
    
    /**
     * Creates a RoutingNumber from a String in MICR format. This will throw
     * a {@code ParseException} if the MICR form is invalid.
     * 
     * @param micr the MICR formatted input
     * @return the Routing Number represented by the MICR
     */
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
     * Creates a Routing Number from the fraction form of a routing number.
     * 
     * @param fraction the input fraction type
     * @return the Routing Number identified by the input
     */
    public static RoutingNumber parseFraction(CharSequence fraction) {
        if (fraction == null) {
            throw new ParseException("Null Fraction");
        }
        final Matcher m = PAT_FRACTION.matcher(fraction);
        if (! m.matches()) {
            throw new ParseException("Invalid Fraction syntax");
        }
        
        final RoutingNumber ret = new RoutingNumber();
        ret.fedRoutingSymbol = String.format("%04d", Integer.parseInt(m.group(4), 10));
        ret.abaInstitution = String.format("%04d", Integer.parseInt(m.group(3), 10));
        ret.checkDigit = Integer.toString(ret.calculateCheckDigit());
        ret.prefix = m.group(2);
        
        final String type = ret.fedRoutingSymbol.substring(0,2);
        if (! TYPE_GOVERNMENT.contains(type)
                && ! TYPE_PRIMARY.contains(type)
                && ! TYPE_THRIFT.contains(type)
                && ! TYPE_ELECTRONIC.contains(type)) {
            throw new ParseException("Invalid Federal Routing Symbol");
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
     * Returns the fraction form (PP-YYYY/XXXX) format.
     * 
     * @return the routing number in Fraction format
     */
    public String toFractionString() {
        StringBuilder result = new StringBuilder(10);
        
        if (prefix != null) {
            result.append(prefix).append("-");
        }
        result.append(Integer.parseInt(abaInstitution, 10))
            .append("/")
            .append(Integer.parseInt(fedRoutingSymbol, 10));
        return result.toString();
    }
    
    /**
     * Returns the type of federal reserve
     * 
     * @return the Federal Reserve type
     */
    public FedReserveType getFederalReserveType() {
        if (TYPE_GOVERNMENT.contains(fedRoutingSymbol.substring(0,2))) {
            return FedReserveType.GOVERNMENT;
        } else if (TYPE_PRIMARY.contains(fedRoutingSymbol.substring(0,2))) {
            return FedReserveType.PRIMARY;
        } else if (TYPE_THRIFT.contains(fedRoutingSymbol.substring(0,2))) {
            return FedReserveType.THRIFT;
        } else {
            return FedReserveType.ELECTRONIC;
        }
    }
}
