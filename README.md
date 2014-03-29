OWASP Parsers
=============

This is a series of type parsers (validators) which operate under
semantics which help with static analysis and make particular
assertions about the result.

Normal validation methods which return a `boolean` specifying whether
the data is valid or not still leave opportunity to use the invalid
date in a tainted manner. For example, given the following:

    if (isValidAba(tainted)) {
      ...
    } else {
      LOG.error("Invalid ABA: " + tainted);
    }

If `isValidAba` is marked as a validation function, cleansing its one
argument, the `else` branch above will be missed because `tainted` is
presumed cleansed at every point beyond.

These validators parse an input and return a new data type which can
only be constructed using the parser, and a value is only returned if
the input is valid. The general form of the parsers is:

    static T parseX(S);

Each of the parse functions also throw a `ParseException`.

Furthermore, for sensitive types of data, `toString()` will return a
redacted form.
