import org.lunaris.entity.data.EntityDataFlag;

/**
 * Created by RINES on 14.09.17.
 */
public class FlagChecker {

    public static void main(String[] args) {
        long number = 70377334685696L;
        System.out.println(Long.toBinaryString(number));
        for(EntityDataFlag flag : EntityDataFlag.values()) {
            if((number & (1L << flag.ordinal())) > 0)
                System.out.println(flag.ordinal() + " " + flag.name());
        }
    }

}
