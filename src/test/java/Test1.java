import org.junit.jupiter.api.Test;

/**
 * @author 沙福林
 * @date 2023/9/27 21:31
 * @description 测试类
 */
public class Test1 {
    @Test
    public void test1(){
        String regex = "[\\\\|/]+";
        System.out.println("/home////abc/111/".replaceAll(regex,"/"));
        System.out.println("/home\\\\//abc/111\\2".replaceAll(regex,"/"));
    }
}
