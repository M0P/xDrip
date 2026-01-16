package jamorham.keks;

import static org.bouncycastle.util.BigIntegers.fromUnsignedByteArray;
import static jamorham.keks.util.Util.arrayAppend;

import org.bouncycastle.math.ec.ECPoint;

/**
 * JamOrHam
 */

public class JECPoint {

    private final ECPoint point;

    public JECPoint(ECPoint point) {
        this.point = point;
    }

    public ECPoint getPoint() {
        return point;
    }

    static ECPoint pointFromBytes(final byte[] xBytes, final byte[] yBytes) {
        return (Curve.curve.createPoint(fromUnsignedByteArray(xBytes), fromUnsignedByteArray(yBytes)));
    }

    byte[] toBytes() {
        return arrayAppend(point.getXCoord().getEncoded(), point.getYCoord().getEncoded());
    }
}
