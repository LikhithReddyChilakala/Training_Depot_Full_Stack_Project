//package Day1_29_07_NumberManipulation;
//import java.util.*;
//
//public class Check_palindrome {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        int original = n;
//        int reverse = 0;
//        while (n > 0) {
//            int digit = n % 10;
//            reverse = reverse * 10 + digit;
//            n = n / 10;
//        }
//        if (reverse == original)System.out.println("Yes the given number is my favourite number palindrome saaru");
//        else System.out.println("Aaata bhathakadam ante idhe saaru");
//        sc.close();
//    }
//}