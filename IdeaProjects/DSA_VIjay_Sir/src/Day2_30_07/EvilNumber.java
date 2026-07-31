package Day2_30_07;
import java.util.Scanner;

public class EvilNumber {
    static void main() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int temp = n;
        int count = 0 ;
        while(temp!=0){
            if(temp%2==1)count++;
            temp = temp/2;
        }
        if(count%2==0) System.out.println(n+" is a Evil Number");
        else System.out.println(n +" is not a Evil number");
    }

}
