package chap2.com.ns.nie;

import java.util.Properties;

/**
 * Created by niewj on 2016/12/11.
 */
public class ConsumerTest {

    public static void init(){
        Properties props = new Properties();
        props.put("zookeeper.connect","NIE-00:2181");
        props.put("group.id","");
    }

    public static void main(String[] args) {

    }
}
