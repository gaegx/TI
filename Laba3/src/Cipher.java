
public class Cipher {
    // Шифрование (RSA-подобное)
    public static byte[] encode(byte[] data, int key, int r) {
        byte[] result = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            int k = CipMath.intPower(data[i] & 0xFF, key, r);
            int j = i << 1;
            result[j] = (byte) (k & 0xFF);
            result[j + 1] = (byte) ((k >> 8) & 0xFF);
        }
        return result;
    }

    // Дешифрование (RSA-подобное)
    public static byte[] decode(byte[] data, int key, int r) {
        byte[] res = new byte[data.length / 2];
        for (int i = 0; i < res.length; i++) {
            int j = i << 1;
            int k = (data[j] & 0xFF) + ((data[j + 1] & 0xFF) << 8);
            int decrypted = CipMath.intPower(k, key, r);
            res[i] = (byte) (decrypted & 0xFF);
        }
        return res;
    }
}