
public class BoyerMoore {

    public BoyerMoore(String pattern) {

    }

    public int search(String text, String pattern) {
        int BASE = 256;
        int[] occurrence;
        int n = text.length();
        int m = pattern.length();
        int skip;

        occurrence = new int[BASE];
        for (int c = 0;
                c < BASE;
                c++) {
            occurrence[c] = -1;
        }
        for (int j = 0;
                j < pattern.length();
                j++) {
            occurrence[pattern.charAt(j)] = j;
        }

        for (int i = 0;
                i <= n - m;
                i += skip) {
            skip = 0;
            for (int j = m - 1; j >= 0; j--) {
                if (pattern.charAt(j) != text.charAt(i + j)) {
                    skip = Math.max(1, j - occurrence[text.charAt(i + j)]);
                    break;
                }
            }
            if (skip == 0) {
                return i;
            }
        }
        return n;
    }
}
