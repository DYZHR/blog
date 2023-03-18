import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import io.swagger.models.auth.In;
import org.apache.tomcat.jni.File;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class ListNode {
     int val;
     ListNode next;
     ListNode() {}
     ListNode(int val) { this.val = val; }
     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode() {}
     TreeNode(int val) { this.val = val; }
     TreeNode(int val, TreeNode left, TreeNode right) {
         this.val = val;
         this.left = left;
         this.right = right;
     }
}

class Trie {
    private Trie[] children;
    private boolean isEnd;

    public Trie() {
        children = new Trie[26];
        isEnd = false;
    }

    public void insert(String word) {
        Trie node = this;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            int index = ch - 'a';
            if (node.children[index] == null) {
                node.children[index] = new Trie();
            }
            node = node.children[index];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Trie node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private Trie searchPrefix(String prefix) {
        Trie node = this;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            int index = ch - 'a';
            if (node.children[index] == null) {
                return null;
            }
            node = node.children[index];
        }
        return node;
    }
}

class A {
    private void func(){
        System.out.println("A");
    }
}
class B extends A {
    private void func(){
        System.out.println("B");
    }
}
public class LeetCode{
    final String s = new String("123");
    @Test
    public void main() {
        char[] chars = {'a', 'b','c'};
        System.out.println(Arrays.toString(chars));
    }
    @Test
    public void test() throws IOException, InterruptedException {

    }
    public int maxProfit(int[] prices) {
        if (prices.length == 1) return 0;
        int[][] dp = new int[prices.length][3];
        dp[0][0] = -prices[0];
        dp[0][1] = 0;
        dp[0][2] = 0;
        for (int i = 1; i < prices.length; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], Math.max(dp[i - 1][1] - prices[i], dp[i - 1][2] - prices[i]));
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 1][2]);
            dp[i][2] = dp[i - 1][0] + prices[i];
        }
        return Math.max(dp[prices.length - 1][0], Math.max(dp[prices.length - 1][1], dp[prices.length - 1][2]));
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        int n = in.nextInt();
        int L = in.nextInt();
        List<Integer> ans = new ArrayList<>();
        int sum = 0;
        int minLen = Integer.MAX_VALUE;
        for (int i = 0; i < L; i++) {
            sum += i;
        }
        if (sum > n) {
            System.out.println("No");
            return;
        }
        if (sum == n) {
            minLen = L;
            for (int i = 0; i <= L - 2; i++) {
                System.out.print(i + " ");
            }
            System.out.println(L - 1);
            return;
        }
        int left = 0, right = L - 1;
        //双指针，sum<n就right走，sum>n就left走，直到...
        //如果找到一个区间长L，就直接返回，否则就继续找
        while (!((right - left + 1 == L) && (sum > n))) {
            if (sum == n) {
                if (minLen > right - left + 1) {
                    minLen = right - left + 1;
                    ans.clear();
                    for (int i = left; i <= right; i++) {
                        ans.add(i);
                    }
                    sum -= left;
                    left++;
                }
                if (minLen == L) {
                    break;
                }
            }
            else if (sum < n) {
                right++;
                sum += right;
            }
            else {
                sum -= left;
                left++;
            }
        }
        if (minLen == Integer.MAX_VALUE || minLen > 100) {
            System.out.println("No");
            return;
        }
        for (int i = 0; i < ans.size() - 1; i++) {
            System.out.print(ans.get(i) + " ");
        }
        System.out.println(ans.get(ans.size() - 1));
    }
    public double myPow(double x, int n) {
        return pow(x, n);
    }
    private double pow(double x, int n) {
        if (n == 0) return 1;
        if (n == 1) return x;
        if (n == -1) return 1/x;
        double tmp_x = x;
        if (n > 0) {
            long i = 1;
            while ((i<<1) <= n){
                x = x * x;
                i <<= 1;
            }
            return x * pow(tmp_x, n - (int) i);
        }
        long i = -1;
        while ((i<<1) >= n){
            x = x * x;
            i <<= 1;
        }
        return 1 / x * pow(tmp_x, (int) (n - i));
    }
    public int divide(int dividend, int divisor) {
        if (divisor == Integer.MIN_VALUE && dividend != Integer.MIN_VALUE) return 0;
        if (divisor == -1 && dividend == Integer.MIN_VALUE) return Integer.MAX_VALUE;
        if (divisor == 1 && dividend == Integer.MIN_VALUE) return Integer.MIN_VALUE;
        if (dividend == 0) return 0;

        if (dividend > 0) {
            if (divisor < 0) return -count(Long.valueOf(dividend), Long.valueOf(-divisor));
            if (divisor > 0) return count(Long.valueOf(dividend), Long.valueOf(divisor));
        }
        if (dividend < 0) {
            if (divisor < 0) return count(Long.valueOf(-dividend), Long.valueOf(-divisor));
            if (divisor > 0) return -count(Long.valueOf(-dividend), Long.valueOf(divisor));
        }
        return 0;
    }
    //两个正数
    private int count(long dividend, long divisor) {
        if (dividend < divisor) return 0;
        int count = 1;
        long tmp = divisor;
        while (dividend >= (tmp<<1)) {
            count<<=1;
            tmp<<=1;
        }
        return count + count(dividend - tmp, divisor);
    }

    public int lengthOfLongestSubstring(String s) {
        //最大长度
        int max = 0;
        //当前长度
        int now = 0;
        //维护一个set
        Set<Character> set = new HashSet<>();
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (set.contains(c)) {
                int index = list.indexOf(c);
                for (int k = 0; k <= index; k++) list.remove(0);
                list.add(c);
                now = 1;
                set.clear();
                for (Character c2 : list) set.add(c2);
            }
            else {
                now++;
                set.add(c);
                list.add(c);
            }
            max = Math.max(max, now);
        }
        return max;
    }

}
