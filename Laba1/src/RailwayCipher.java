import java.util.Arrays;

public class RailwayCipher {

    /**
     * Шифрование текста
     * @param plainText исходный текст для шифрования
     * @param rails количество уровней (рельсов)
     * @return зашифрованный текст
     */
    public static String encrypt(String plainText, int rails) {
        if (rails < 2) throw new IllegalArgumentException("Количество рельсов должно быть ≥ 2");
        if (plainText == null || plainText.isEmpty()) return plainText;

        StringBuilder[] railStrings = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            railStrings[i] = new StringBuilder();
        }

        int currentRail = 0;
        int direction = 1;

        for (char c : plainText.toCharArray()) {
            railStrings[currentRail].append(c);
            currentRail += direction;

            if (currentRail == rails - 1 || currentRail == 0) {
                direction *= -1;
            }
        }

        StringBuilder cipherText = new StringBuilder();
        for (StringBuilder rail : railStrings) {
            cipherText.append(rail);
        }

        return cipherText.toString();
    }

    /**
     * Дешифрование текста
     * @param cipherText зашифрованный текст
     * @param rails количество уровней (рельсов)
     * @return расшифрованный текст
     */
    public static String decrypt(String cipherText, int rails) {
        if (rails < 2) throw new IllegalArgumentException("Количество рельсов должно быть ≥ 2");
        if (cipherText == null || cipherText.isEmpty()) return cipherText;

        char[][] railMatrix = new char[rails][cipherText.length()];
        for (char[] row : railMatrix) {
            Arrays.fill(row, '\0');
        }

        // Отмечаем позиции символов
        int currentRail = 0;
        int direction = 1;
        for (int i = 0; i < cipherText.length(); i++) {
            railMatrix[currentRail][i] = '*';
            currentRail += direction;

            if (currentRail == rails - 1 || currentRail == 0) {
                direction *= -1;
            }
        }

        // Заполняем символами
        int index = 0;
        for (int i = 0; i < rails; i++) {
            for (int j = 0; j < cipherText.length(); j++) {
                if (railMatrix[i][j] == '*' && index < cipherText.length()) {
                    railMatrix[i][j] = cipherText.charAt(index++);
                }
            }
        }

        // Читаем по зигзагу
        StringBuilder plainText = new StringBuilder();
        currentRail = 0;
        direction = 1;
        for (int i = 0; i < cipherText.length(); i++) {
            plainText.append(railMatrix[currentRail][i]);
            currentRail += direction;

            if (currentRail == rails - 1 || currentRail == 0) {
                direction *= -1;
            }
        }

        return plainText.toString();
    }

    public static void main(String[] args) {
        String original = "ПРИВЕТМИР";
        int rails = 3;

        String encrypted = encrypt(original, rails);
        String decrypted = decrypt(encrypted, rails);

        System.out.println("Оригинал: " + original);
        System.out.println("Зашифровано: " + encrypted);
        System.out.println("Расшифровано: " + decrypted);
    }

    }