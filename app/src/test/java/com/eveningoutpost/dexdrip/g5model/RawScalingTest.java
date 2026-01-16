package com.eveningoutpost.dexdrip.g5model;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.Test;

import java.util.LinkedList;

//import lombok.val;

// jamorham

public class RawScalingTest {

    @Test
    public void scaleTest() {
        assertWithMessage("G5 1").that((int) RawScaling.scale(10000, RawScaling.DType.G5, false)).isEqualTo(10000);

        assertWithMessage("G6v1 1").that((int) RawScaling.scale(294, RawScaling.DType.G6v1, false)).isEqualTo(9996);

        assertWithMessage("G6v2 1").that((int) RawScaling.scale(1168582904, RawScaling.DType.G6v2, false)).isEqualTo(187219);
    }

    @Test
    public void scale1Test() {
        LinkedList raw = new LinkedList<Integer>();
        raw.add(0x4583aa08);
        raw.add(0x4583b210);
        raw.add(0x4583b6f0);
        raw.add(0x45c90c00);
        raw.add(0x45b93ac8);
        raw.add(0x45b941c0);
        raw.add(0x4662ec58);
        raw.add(0x4662f124);
        raw.add(0x4662e19c);

        LinkedList filtered = new LinkedList<Integer>();
        filtered.add(0xc25499c3);       // error state
        filtered.add(0x4550a1ac);
        filtered.add(0x458c0249);
        filtered.add(0xc25499c3);       // error state
        filtered.add(0x459134bf);
        filtered.add(0x45c46edf);
        filtered.add(0x465b0404);
        filtered.add(0x467070dd);
        filtered.add(0x46754541);


    }
}