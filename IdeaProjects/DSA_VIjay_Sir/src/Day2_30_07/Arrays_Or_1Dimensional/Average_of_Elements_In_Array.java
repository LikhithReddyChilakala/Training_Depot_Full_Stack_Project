package Day2_30_07.Arrays_Or_1Dimensional;
import java.util.Scanner;

public class Average_of_Elements_In_Array {
    static void main() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter " + n + " Elements: ");
//       Sum of elements in array
        int sum =0 ;
        for(int i=0;i<n;i++){
//       Read elements in array
           arr[i] = sc.nextInt();
           sum+=arr[i];
        }
        double avg = (double)sum/n;
        System.out.println("Average of elements in given array : "+ avg);
    }
}
