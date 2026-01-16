package jamorham.keks;

import static org.bouncycastle.util.BigIntegers.asUnsignedByteArray;
import static org.bouncycastle.util.BigIntegers.fromUnsignedByteArray;
import static jamorham.keks.Curve.FIELD_SIZE;
import static jamorham.keks.Curve.PACKET_SIZE;
import static jamorham.keks.JECPoint.pointFromBytes;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import jamorham.keks.util.ByteArrayHashMap;

/**
 * JamOrHam
 *
 * Data packet serialization
 */

public class Packet {

    BigInteger hash;
    ECPoint publicKeyPoint1;
    ECPoint publicKeyPoint2;

    public Packet(BigInteger hash, ECPoint publicKeyPoint1, ECPoint publicKeyPoint2) {
        this.hash = hash;
        this.publicKeyPoint1 = publicKeyPoint1;
        this.publicKeyPoint2 = publicKeyPoint2;
    }

    public BigInteger getHash() {
        return hash;
    }

    public ECPoint getPublicKeyPoint1() {
        return publicKeyPoint1;
    }

    public ECPoint getPublicKeyPoint2() {
        return publicKeyPoint2;
    }

    public Packet(final ByteArrayHashMap bhm) {
        this(fromUnsignedByteArray(bhm.mget(HBYTES1_ID)),
                pointFromBytes(bhm.mget(POINT1X_ID), bhm.mget(POINT1Y_ID)),
                pointFromBytes(bhm.mget(POINT2X_ID), bhm.mget(POINT2Y_ID)));
    }

    public static final int POINT1X_ID = 28082;
    public static final int POINT1Y_ID = 37603;
    public static final int POINT2X_ID = 54247;
    public static final int POINT2Y_ID = 40255;
    public static final int HBYTES1_ID = 65535;

    private static final int[] ID_LIST = {POINT1X_ID, POINT1Y_ID, POINT2X_ID, POINT2Y_ID, HBYTES1_ID};

    public static Packet parse(final byte[] packet) {
        if (packet.length < PACKET_SIZE) return null;
        ByteArrayHashMap bhm = new ByteArrayHashMap();
        ByteBuffer buf = ByteBuffer.wrap(packet);
        for (int id : ID_LIST) {
            buf.get(bhm.mget(id));
        }
        return new Packet(bhm);
    }

    public byte[] output() {
        ByteBuffer packet = ByteBuffer.allocate(PACKET_SIZE);
        packet.put(new JECPoint(getPublicKeyPoint1()).toBytes());
        packet.put(new JECPoint(getPublicKeyPoint2()).toBytes());
        packet.put(asUnsignedByteArray(FIELD_SIZE, getHash()));
        byte[] array = packet.array();
        if (array.length != PACKET_SIZE) {
            throw new RuntimeException("Invalid size");
        }
        return array;
    }

}
