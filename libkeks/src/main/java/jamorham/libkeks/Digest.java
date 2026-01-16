package jamorham.libkeks;


/**
 * JamOrHam
 */

public class Digest {

    private byte[] store = new byte[0];
    private final byte[] destination;

    public Digest(byte[] destination) {
        this.destination = destination;
    }

    public void update(byte[] data) {
        byte[] n = new byte[store.length + data.length];
        System.arraycopy(data, 0, n,  store.length, data.length);
        System.arraycopy(store, 0, n, 0, store.length);
        store = n;
    }

    public void doFinal() {
        byte[] result = SHA256.hash(store);
        System.arraycopy(result, 0, destination, 0, destination.length);
    }
}
