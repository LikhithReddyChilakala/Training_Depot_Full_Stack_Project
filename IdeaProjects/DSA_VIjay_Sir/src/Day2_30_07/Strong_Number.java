package Day2_30_07;
import java.util.Scanner;
public class Strong_Number {
    static void main() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int t = n ;
        int sum =0 ;
        while( t > 0){
            int d = t%10;
            int fact = 1 ;
            for(int i=1;i<=d;i++)fact*=i;
            sum+=fact;
            t=t/10;
        }
        if(sum==n) System.out.println("The number "+ n + " is a strong/Peterson number");
        else System.out.println("Not a Krishnamurthy number");
        sc.close();
    }
}
