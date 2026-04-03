package com.unicenta.format;

import com.unicenta.basic.BasicException;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import javax.swing.SwingConstants;

public class FormatsValidateTest {

    // ---- format delegates to wrapped format ----

    @Test
    public void formatDelegatesToWrappedFormat() {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertEquals("hello", fv.formatValue("hello"));
    }

    @Test
    public void formatNullReturnsEmpty() {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertEquals("", fv.formatValue(null));
    }

    @Test
    public void formatIntDelegatesToWrappedFormat() {
        FormatsValidate fv = new FormatsValidate(Formats.INT);
        String result = fv.formatValue(42);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("42"));
    }

    // ---- parse delegates to wrapped format ----

    @Test
    public void parseDelegatesToWrappedFormat() throws BasicException {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertEquals("world", fv.parseValue("world"));
    }

    @Test
    public void parseNullReturnsNull() throws BasicException {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertNull(fv.parseValue(null));
    }

    @Test
    public void parseEmptyReturnsNull() throws BasicException {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertNull(fv.parseValue(""));
    }

    // ---- parse with custom constraint — doubles int value ----

    @Test
    public void parseWithDoublingConstraint() throws BasicException {
        FormatsConstrain doubler = new FormatsConstrain() {
            @Override
            public Object check(Object value) throws ParseException {
                int v = (Integer) value;
                return v * 2;
            }
        };

        FormatsValidate fv = new FormatsValidate(Formats.INT, new FormatsConstrain[]{doubler});
        String formatted = Formats.INT.formatValue(5);
        Object result = fv.parseValue(formatted);
        Assert.assertEquals(10, result);
    }

    // ---- multiple constraints chain ----

    @Test
    public void multipleConstraintsChainInOrder() throws BasicException {
        // First constraint: add 10; second constraint: multiply by 3
        // Input 5 -> add 10 -> 15 -> multiply 3 -> 45
        FormatsConstrain addTen = new FormatsConstrain() {
            @Override
            public Object check(Object value) throws ParseException {
                return (Integer) value + 10;
            }
        };
        FormatsConstrain timesThree = new FormatsConstrain() {
            @Override
            public Object check(Object value) throws ParseException {
                return (Integer) value * 3;
            }
        };

        FormatsValidate fv = new FormatsValidate(Formats.INT, new FormatsConstrain[]{addTen, timesThree});
        String formatted = Formats.INT.formatValue(5);
        Object result = fv.parseValue(formatted);
        Assert.assertEquals(45, result);
    }

    // ---- failing constraint throws BasicException ----

    @Test(expected = BasicException.class)
    public void failingConstraintThrowsBasicException() throws BasicException {
        FormatsConstrain alwaysFails = new FormatsConstrain() {
            @Override
            public Object check(Object value) throws ParseException {
                throw new ParseException("constraint violation", 0);
            }
        };

        FormatsValidate fv = new FormatsValidate(Formats.STRING, new FormatsConstrain[]{alwaysFails});
        fv.parseValue("anything");
    }

    // ---- alignment delegates to wrapped format ----

    @Test
    public void alignmentDelegatesToStringFormat() {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertEquals(SwingConstants.LEFT, fv.getAlignment());
    }

    @Test
    public void alignmentDelegatesToIntFormat() {
        FormatsValidate fv = new FormatsValidate(Formats.INT);
        Assert.assertEquals(SwingConstants.RIGHT, fv.getAlignment());
    }

    @Test
    public void alignmentDelegatesToBooleanFormat() {
        FormatsValidate fv = new FormatsValidate(Formats.BOOLEAN);
        Assert.assertEquals(SwingConstants.CENTER, fv.getAlignment());
    }

    // ---- single-constrain convenience constructor ----

    @Test
    public void singleConstrainConstructorAppliesConstraint() throws BasicException {
        FormatsConstrain negate = new FormatsConstrain() {
            @Override
            public Object check(Object value) throws ParseException {
                return -((Integer) value);
            }
        };

        FormatsValidate fv = new FormatsValidate(Formats.INT, negate);
        String formatted = Formats.INT.formatValue(7);
        Object result = fv.parseValue(formatted);
        Assert.assertEquals(-7, result);
    }

    // ---- parse with default delegates through constraints ----

    @Test
    public void parseWithDefaultReturnsDefaultForNull() throws BasicException {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertEquals("fallback", fv.parseValue(null, "fallback"));
    }

    @Test
    public void parseWithDefaultReturnsDefaultForEmpty() throws BasicException {
        FormatsValidate fv = new FormatsValidate(Formats.STRING);
        Assert.assertEquals("fallback", fv.parseValue("", "fallback"));
    }
}
