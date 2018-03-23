import java.util.Random;

public class test {
    private int arr[];
    public void init(int n)
    {
        arr=new int[n+1];
        for(int i=1;i<=n;i++)
        {
            arr[i]=new Random().nextInt(n) + 1;
            boolean isRepeat = false;
            for (int j = 1; j < i; j++) {
                if (arr[i] == arr[j]) {
                    isRepeat = true;
                }
            }
            if (isRepeat) {
                i--;
            }
        }
    }

    public static void main(String[] args) {

    }
}
