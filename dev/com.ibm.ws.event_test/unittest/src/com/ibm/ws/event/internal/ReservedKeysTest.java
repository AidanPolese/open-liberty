package com.ibm.ws.event.internal;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReservedKeysTest {

    @Test
    public void testReserveSlot() {
        int startingIndex = ReservedKeys.reserveSlot("STARTING_POINT_FOR_TEST") + 1;
        List<String> names = Arrays.asList("foo", "bar", "baz");
        for (int i = 0; i < 5; i++) {
            for (String s : names) {
                assertEquals(startingIndex + names.indexOf(s), ReservedKeys.reserveSlot(s));
            }
        }
    }

}
