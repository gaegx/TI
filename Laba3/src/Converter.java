public class Converter {
    // Байты в строку (для отладки)
    public static String bytesToStr(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data.length < 100) {
            for (byte b : data) sb.append(b).append(" ");
        } else {
            for (int i = 0; i < 50; i++) sb.append(data[i]).append(" ");
            sb.append("\n\n...\n\n");
            for (int i = data.length - 1; i > data.length - 50; i--) sb.append(data[i]).append(" ");
        }
        return sb.toString();
    }

    // Байты в строку 16-битных чисел
    public static String wordsToStr(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data.length < 200) {
            for (int i = 0; i < data.length - 1; i += 2) {
                int word = (data[i] & 0xFF) + ((data[i + 1] & 0xFF) << 8);
                sb.append(word).append(" ");
            }
        } else {
            for (int i = 0; i < 100; i += 2) {
                int word = (data[i] & 0xFF) + ((data[i + 1] & 0xFF) << 8);
                sb.append(word).append(" ");
            }
            sb.append("\n\n...\n\n");
            for (int i = data.length - 2; i > data.length - 100; i -= 2) {
                int word = (data[i] & 0xFF) + ((data[i + 1] & 0xFF) << 8);
                sb.append(word).append(" ");
            }
        }
        return sb.toString();
    }
}