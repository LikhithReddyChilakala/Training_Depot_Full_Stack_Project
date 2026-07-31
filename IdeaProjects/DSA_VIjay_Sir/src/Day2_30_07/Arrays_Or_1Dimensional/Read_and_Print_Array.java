package Day2_30_07.Arrays_Or_1Dimensional;
import java.util.Scanner;

public class Read_and_Print_Array {
    static void main() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter " + n + " Elements: ");
//        Reading an array or elements into arrays
        for(int i=0 ; i < n;i++)arr[i] = sc.nextInt();
        System.out.println("** Array Elements **");
//        Printing elements of array
        for(int i=0;i<n;i++) System.out.println(i +"th elements is " + arr[i]+" ");
        sc.close();

    }
}
