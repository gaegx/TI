import java.util.*;

public class VigenereCipher {

    // Алфавит с учетом, что "А" - это 1
    static final String ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    static final Map<Character, Integer> CHAR_INDEX_MAP = new HashMap<>();
    static final Map<Integer, Character> INDEX_CHAR_MAP = new HashMap<>();

    static {
        for (int i = 0; i < ALPHABET.length(); i++) {
            CHAR_INDEX_MAP.put(ALPHABET.charAt(i), i + 1); // "А" будет иметь индекс 1
            INDEX_CHAR_MAP.put(i + 1, ALPHABET.charAt(i)); // Маппинг обратно
        }
    }

    // Возвращает следующую букву в алфавите
    private static char shiftLetter(char sym) {
        int index = CHAR_INDEX_MAP.get(sym);
        index = (index) % ALPHABET.length(); // Следующий индекс
        if (index == 0) {
            index = ALPHABET.length(); // Переход к началу алфавита
        }
        return INDEX_CHAR_MAP.get(index);
    }

    // Расширяет ключ до длины текста с прогрессией
    private static String formKeyword(int length, String keyword) {
        StringBuilder result = new StringBuilder(keyword);
        while (result.length() < length) {
            int size = result.length();
            for (int i = 0; i < size && result.length() < length; i++) {
                result.append(shiftLetter(result.charAt(i)));
            }
        }
        return result.substring(0, length);
    }

    public static String encrypt(String text, String keyword) {
        text = text.toUpperCase();
        keyword = formKeyword(text.length(), keyword.toUpperCase());
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            int textIndex = CHAR_INDEX_MAP.get(text.charAt(i)) - 1; // Индекс в алфавите начиная с 0
            int keyIndex = CHAR_INDEX_MAP.get(keyword.charAt(i)) - 1; // Индекс ключа начиная с 0
            int cipherIndex = (textIndex + keyIndex) % ALPHABET.length(); // Шифрование
            result.append(INDEX_CHAR_MAP.get(cipherIndex + 1)); // Получаем символ с учетом 1-индексации
        }

        return result.toString();
    }

    public static String decrypt(String cipherText, String keyword) {
        cipherText = cipherText.toUpperCase();
        keyword = formKeyword(cipherText.length(), keyword.toUpperCase());
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < cipherText.length(); i++) {
            int cipherIndex = CHAR_INDEX_MAP.get(cipherText.charAt(i)) - 1; // Индекс зашифрованного символа
            int keyIndex = CHAR_INDEX_MAP.get(keyword.charAt(i)) - 1; // Индекс ключа
            int plainIndex = (cipherIndex - keyIndex + ALPHABET.length()) % ALPHABET.length(); // Дешифрование
            result.append(INDEX_CHAR_MAP.get(plainIndex + 1)); // Получаем символ с учетом 1-индексации
        }

        return result.toString();
    }

    public static void main(String[] args) {
        String text = "ЁРРарпврапрапрвапраправапрваЁЁТЁ";
        String keyword = "АБА";

        System.out.println("Исходный текст: " + text);
        System.out.println("Ключ: " + keyword);

        String encrypted = encrypt(text, keyword);
        System.out.println("Зашифрованный: " + encrypted);

        String decrypted = decrypt(encrypted, keyword);
        System.out.println("Расшифрованный: " + decrypted);
    }
}
