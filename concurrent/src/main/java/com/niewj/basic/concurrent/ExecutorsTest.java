package com.niewj.basic.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by weijun.nie on 2017/10/9.
 */
public class ExecutorsTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<User> resultList = null;

        ThreadPoolExecutor tpe = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        ExecutorService es = Executors.newCachedThreadPool();
        Future<List<User>> future = es.submit(new MyTask());
        resultList = future.get();
        es.shutdown();

    }
}

class MyTask implements Callable<List<User>> {

    public List<User> call() throws Exception {
        List<User> users = new ArrayList<User>();
        for (int i = 10; i < 20; i++) {
            users.add(new User("name_" + i, "no_" + i, 100 - i));
            TimeUnit.SECONDS.sleep(1);
        }
        return users;
    }
}

class User {
    private String name;
    private String id;
    private int age;

    public User(String name, String id, int age) {
        this.name = name;
        this.id = id;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "id: " + this.getId() + "; name: " + this.getName() + "; age: " + this.getAge();
    }
}
