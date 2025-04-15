public class LFSR {
    public static final int LENGTH = 27;
    private long reg;

    public LFSR(long initReg) {
        this.reg = initReg;
    }

    public byte[] getKey(int keyLength) {
        byte[] res = new byte[keyLength];

        for (int i = 0; i < keyLength; i++) {
            res[i] = (byte)(reg >> (LENGTH - 8));

            for (int j = 0; j < 8; j++) {
                long bit = ((reg >> 33) & 1) ^ ((reg >> 14) & 1) ^ ((reg >> 13) & 1) ^ (reg & 1);

            }
        }

        return res;
    }
}
