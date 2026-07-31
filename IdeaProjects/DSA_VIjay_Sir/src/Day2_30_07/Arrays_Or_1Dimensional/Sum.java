package Day2_30_07.Arrays_Or_1Dimensional;
import java.util.Scanner;

public class Sum {
    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter " + n + " Elements: ");
//        Sum of elements in array
        int sum =0 ;
        for(int i=0;i<n;i++){
//            Read elements in array
            arr[i] = sc.nextInt();
            sum+=arr[i];
        }
        System.out.println("Sum of elements in given array : "+ sum);
    }
}
