public class Cipher {
    LFSR lfsr;

    // Метод шифрования
    public byte[] encrypt(byte[] bytes, long key) {
        lfsr = new LFSR(key);
        byte[] fullKey = lfsr.getKey(bytes.length);
        for (int i = 0; i < fullKey.length; i++) {
            bytes[i] = (byte)(fullKey[i] ^ bytes[i]);
        }
        return fullKey;
    }

}
