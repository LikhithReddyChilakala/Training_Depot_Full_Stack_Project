package Day3_31_07;

import java.util.Scanner;

public class Rotate_Array_ClockWise {
    static void main() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter the elements ");
        for(int i=0;i<n;i++)arr[i] = sc.nextInt();
        int first = arr[0];
        for(int i= 0; i< n-1;i++)arr[i]=arr[i+1];
        arr[n-1]=first;
        System.out.println("Here is the elements after rotation");
        for(int i=0 ; i<n;i++) System.out.print(arr[i]+" ");
    }
}
