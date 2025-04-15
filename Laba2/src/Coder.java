    public class Coder {

        public byte[] binaryToBytes(String s) {
            int ost = s.length() % 8;
            if (ost != 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8 - ost; i++) {
                    sb.append("0");
                }
                s = s + sb.toString();
            }
            byte[] res = new byte[s.length() / 8];
            char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                int j = i / 8;
                res[j] = (byte)((res[j] << 1) + (chars[i] - '0'));
            }
            return res;
        }


        public String bytesToBinary(byte[] bytes) {
            char[] chars = new char[bytes.length * 8];
            for (int i = 0; i < bytes.length; i++) {
                for (int j = 7; j >= 0; j--) {
                    int b = (bytes[i] >> j) & 1;
                    chars[(i * 8) + (7 - j)] = (char)(b + 48);
                }
            }
            return new String(chars);
        }


    }
