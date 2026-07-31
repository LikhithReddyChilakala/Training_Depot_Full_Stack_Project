package Day3_31_07;

import java.util.Scanner;

public class Even_Odd_count {
    static void main() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the size of array : ");
        int n = sc.nextInt();
        int even =0 ;
        int odd =0 ;
        int[] arr = new int[n];
        for(int i=0;i<n;i++) {
            arr[i] = sc.nextInt();
            if (arr[i] % 2 == 0) even++;
            else odd++ ;
        }
        System.out.println("Even count : " + even);
        System.out.println("Odd count : " + odd);
    }
}
