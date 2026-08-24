package net.flynn.opentierlist.model.enums;

import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.ConfigHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultTierTest {

    private DefaultTier s, a, b, c, d, e, f, ut;

    private Set<DefaultTier> defaultTiers;

    @Before
    public void setUp() {
        s = DefaultTier.S;
        a = DefaultTier.A;
        b = DefaultTier.B;
        c = DefaultTier.C;
        d = DefaultTier.D;
        e = DefaultTier.E;
        f = DefaultTier.F;
        ut = DefaultTier.UNTIERED;
        defaultTiers = new HashSet<>(Arrays.asList(DefaultTier.values()));
    }

    @After
    public void tearDown() {
        s = null; a = null; b = null; c = null; d = null; e = null; f = null; ut = null;
        defaultTiers.clear();
        defaultTiers = null;
    }

    @Test
    public void value() {
        assertTrue(s.value().equalsTier(new Tier("S", ConfigHolder.DEFAULT_S_COLOR)));
        assertEquals(0, s.value().itemCount());
        assertTrue(a.value().equalsTier(new Tier("A", ConfigHolder.DEFAULT_A_COLOR)));
        assertEquals(0, a.value().itemCount());
        assertTrue(b.value().equalsTier(new Tier("B", ConfigHolder.DEFAULT_B_COLOR)));
        assertEquals(0, b.value().itemCount());
        assertTrue(c.value().equalsTier(new Tier("C", ConfigHolder.DEFAULT_C_COLOR)));
        assertEquals(0, c.value().itemCount());
        assertTrue(d.value().equalsTier(new Tier("D", ConfigHolder.DEFAULT_D_COLOR)));
        assertEquals(0, d.value().itemCount());
        assertTrue(e.value().equalsTier(new Tier("E", ConfigHolder.DEFAULT_E_COLOR)));
        assertEquals(0, e.value().itemCount());
        assertTrue(f.value().equalsTier(new Tier("F", ConfigHolder.DEFAULT_F_COLOR)));
        assertEquals(0, f.value().itemCount());
        assertTrue(ut.value().equalsTier(new Tier("UNTIERED", ConfigHolder.DEFAULT_UNTIERED_COLOR)));
        assertEquals(0, ut.value().itemCount());
    }

    @Test
    public void values() {
        assertTrue(defaultTiers.containsAll(Set.of(s,a,b,c,d,e,f, ut)));

        int TIERS_NUMBER = 8;
        assertEquals(TIERS_NUMBER, defaultTiers.size());
    }

    @Test
    public void valueOf() {
        assertEquals(s, DefaultTier.valueOf("S"));
        assertEquals(a, DefaultTier.valueOf("A"));
        assertEquals(b, DefaultTier.valueOf("B"));
        assertEquals(c, DefaultTier.valueOf("C"));
        assertEquals(d, DefaultTier.valueOf("D"));
        assertEquals(e, DefaultTier.valueOf("E"));
        assertEquals(f, DefaultTier.valueOf("F"));
        assertEquals(ut, DefaultTier.valueOf("UNTIERED"));

        assertThrows(IllegalArgumentException.class, () -> DefaultTier.valueOf("_"));
    }
}