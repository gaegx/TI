public class CipMath {
    // Быстрое возведение в степень по модулю (x^k mod n)
    public static int intPower(int x, int k, int n) {
        int x1 = x;
        int k1 = k;
        int y = 1;
        while (k1 > 0) {
            while ((k1 & 1) == 0) {
                k1 >>= 1;
                x1 = (x1 * x1) % n;
            }
            k1--;
            y = (y * x1) % n;
        }
        return y;
    }

    // Проверка числа на простоту
    public static boolean isPrime(int v) {
        if (v == 4) return false;
        for (int i = 2; i <= Math.sqrt(v); i++) {
            if (v % i == 0) return false;
        }
        return true;
    }

    // Функция Эйлера (для RSA)
    public static int fEuler(int i, int j) {
        return (i - 1) * (j - 1);
    }

    // Нахождение обратного элемента (a⁻¹ mod b)
    public static int[] getInverse(int a, int b) {
        int x0 = 1, x1 = 0, y0 = 0, y1 = 1;
        int d0 = a, d1 = b;
        while (d1 > 1) {
            int q = d0 / d1;
            int d2 = d0 % d1;
            int x2 = x0 - q * x1;
            int y2 = y0 - q * y1;
            d0 = d1;
            d1 = d2;
            x0 = x1;
            x1 = x2;
            y0 = y1;
            y1 = y2;
        }
        if (y1 < 0) y1 += a;
        return new int[]{x1, y1, d1};
    }
}