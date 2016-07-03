/*
 * Copyright (C) 2012-2016 Opher Shachar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ciju.client.ipp;

import org.ciju.ipp.IppEncoding;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Opher Shachar
 */
public class IppEncodingTest {
    
    public IppEncodingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        IppEncoding.GroupTag dt[] = new IppEncoding.GroupTag[8];
        int i = 0;
        dt[i++] = IppEncoding.GroupTag.valueOf(0x01);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x02);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x03);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x04);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x05);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x06);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x07);
        dt[i++] = IppEncoding.GroupTag.valueOf(0x09);
        assertArrayEquals(IppEncoding.GroupTag.values(), dt);
    }
}
