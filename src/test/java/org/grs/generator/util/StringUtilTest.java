package org.grs.generator.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * StringUtilsTest
 *
 * @author f0rb on 2017-08-11.
 */
public class StringUtilTest {

    @Test
    public void testCamelCase() {
        assertEquals("userNameTable", StringUtils.join(StringUtil.camelize("user_name_table")));
    }

    @Test
    public void testCapitalize() {
        assertEquals("UserNameTable", StringUtil.capitalize("user_name_table", "_"));
    }

}
