import java.util.*;

public class VigenereCipher {
    static final String ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    static final Map<Character, Integer> CHAR_INDEX_MAP = new HashMap<>();
    static final Map<Integer, Character> INDEX_CHAR_MAP = new HashMap<>();

    static {
        for (int i = 0; i < ALPHABET.length(); i++) {
            CHAR_INDEX_MAP.put(ALPHABET.charAt(i), i); // Теперь "А" имеет индекс 0
            INDEX_CHAR_MAP.put(i, ALPHABET.charAt(i));
        }
    }

    private static char shiftLetter(char sym) {
        int index = CHAR_INDEX_MAP.get(sym);
        return INDEX_CHAR_MAP.get((index + 1) % ALPHABET.length());
    }

//    private static String formKeyword(int length, String keyword) {
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            result.append(keyword.charAt(i % keyword.length()));
//        }
//        return result.toString();
//    }

    private static String formKeyword(int length, String keyword) {
        StringBuilder result = new StringBuilder();
        keyword = keyword.toUpperCase();

        for (int i = 0; i < length; i++) {
            char baseChar = keyword.charAt(i % keyword.length());


            char shiftedChar = baseChar;
            if (i >= keyword.length()) {
                shiftedChar = shiftLetter(baseChar);
            }

            result.append(shiftedChar);
        }

        return result.toString();
    }



    public static String encrypt(String text, String keyword) {
        text = text.toUpperCase();
        StringBuilder result = new StringBuilder();
        String extendedKey = formKeyword(text.length(), keyword.toUpperCase());

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!CHAR_INDEX_MAP.containsKey(c)) {
                result.append(c);
                continue;
            }

            int textIndex = CHAR_INDEX_MAP.get(c);
            int keyIndex = CHAR_INDEX_MAP.get(extendedKey.charAt(i));
            int cipherIndex = (textIndex + keyIndex) % ALPHABET.length();
            result.append(INDEX_CHAR_MAP.get(cipherIndex));
        }

        return result.toString();
    }

    public static String decrypt(String cipherText, String keyword) {
        cipherText = cipherText.toUpperCase();
        StringBuilder result = new StringBuilder();
        String extendedKey = formKeyword(cipherText.length(), keyword.toUpperCase());

        for (int i = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);
            if (!CHAR_INDEX_MAP.containsKey(c)) {
                result.append(c);
                continue;
            }

            int cipherIndex = CHAR_INDEX_MAP.get(c);
            int keyIndex = CHAR_INDEX_MAP.get(extendedKey.charAt(i));
            int plainIndex = (cipherIndex - keyIndex + ALPHABET.length()) % ALPHABET.length();
            result.append(INDEX_CHAR_MAP.get(plainIndex));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        String text = "ЁЁЁЁЁ";
        String keyword = "ААА";

        System.out.println("Исходный текст: " + text);
        System.out.println("Ключ: " + keyword);

        String encrypted = encrypt(text, keyword);
        System.out.println("Зашифрованный: " + encrypted);

        String decrypted = decrypt(encrypted, keyword);
        System.out.println("Расшифрованный: " + decrypted);
    }
}