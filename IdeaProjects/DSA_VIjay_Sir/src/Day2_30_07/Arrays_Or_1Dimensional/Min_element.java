package Day2_30_07.Arrays_Or_1Dimensional;

import java.util.Scanner;

public class Min_element {
    static void main() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter " + n + " Elements: ");
//        Reading an array or elements into arrays
        for(int i=0 ; i < n;i++)arr[i] = sc.nextInt();
//        Min element inside array index
        int min_int_index = 0 ;
        for(int i=0 ; i< n ;i++)if(arr[i]<arr[min_int_index])min_int_index=i;
    }
}
