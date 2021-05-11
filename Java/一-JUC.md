# JUC

## 1. Java JUC简介



## 2. volatile 关键字-内存可见性

### 2.1 作用、特点

> `volatile` ：多线程操作共享数据，保证内存中的数据可见，相比于 ***synchronized*** 是轻量级的同步策略。
>
> 1. 保证内存的**可见性**（在底层防止指令重排序）
> 2. 不保证**“互斥性”**
> 3. 不保证**“原子性”**

### 2.2 例

- 多线程共享数据 flag 

```java
public class VolatileTest {
    public static void main(String[] args) {
        ThreadDemo threadDemo = new ThreadDemo();

        Thread t = new Thread(threadDemo);
        t.start();

        while (true) {
            if (threadDemo.flag) {
                System.out.println("----------------------");
                break;
            }
        }
    }
}

class ThreadDemo implements Runnable {

    public boolean flag = false;

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag = true;
        if (flag) {
            System.out.println("flag=" + flag);
        }
    }
}
```

运行结果（程序不终止）：

```shell
flag=true
```

图

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210508145537.png" style="zoom:90%;" />

加上 volatile 关键字

```java
public volatile boolean flag = false;
```

运行结果（程序终止）：

```shell
----------------------
flag=true
```

## 3. 原子变量-CAS算法

### 3.1 原子性

#### 3.1.1 i++的原子性问题

- i = i++的底层操作 分成【读-改-写】三个步骤

```java
int temp = i;
i = i + 1;
i = temp;
```

#### 3.1.2 多线程操作非原子变量 例

- 多线程共享数据：i

```java
public class AtomicTest {
    public static void main(String[] args) {
        Thread1 thread1 = new Thread1();
        // 执行10次线程
        for (int i = 0; i < 10; i++) {
            new Thread(thread1).start();
        }
    }
}

class Thread1 implements Runnable {

    private int i = 0;

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print(getNum() + "  ");
    }

    public int getNum() {
        return i++;
    }
}
```

其中一次结果：(其中1和0重复出现了)

```shell
1  1  6  0  4  2  0  5  3  0 
```

分析：

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210508161417.png)

### 3.2 原子类 java.util.concurrent.atomic

#### 3.2.1  多线程操作原子变量 例

```java
public class AtomicTest {
    public static void main(String[] args) {
        Thread1 thread1 = new Thread1();
        // 执行10次线程
        for (int i = 0; i < 10; i++) {
            new Thread(thread1).start();
        }
    }
}

class Thread1 implements Runnable {

    private AtomicInteger i = new AtomicInteger(0);

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print(getNum() + "  ");
    }

    public int getNum() {
        return i.getAndIncrement();
    }
}
```

其中一次运行结果：

```shell
1  7  8  9  6  4  3  0  2  5  
```

#### 3.2.2 原子类底层原理

1. 使用了 **volatile**  关键字：保证内存可见性
2. CAS 算法：保证数据的原子性

### 3.3 CAS 算法

> CAS：Compare-And-Swap
>
> 硬件对并发操作共享数据的支持
>
> 一个线程失败或挂起并不会导致其他线程也失败或挂起，那么这种算法就被称为**<u>非阻塞算法</u>**。而CAS就是一种非阻塞算法实现，也是一种**<u>乐观锁</u>**技术，它能在不使用锁的情况下实现多线程安全，所以CAS也是一种**<u>无锁算法</u>**。
>
> 
>
> 优点：不使用锁实现多线程的变量同步，资源消耗相对少
>
> 缺点：**<u>ABA问题</u>**
>
> CAS 包含 3 个操作数：
>
> 内存值：V
>
> 预估值：A
>
> 更新值：B
>
> 只有当 V == A 时，V = B（B 赋值给 V），否则不做任何操作

图解：（源知乎：<https://zhuanlan.zhihu.com/p/81025535>）

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210510103443.jpg)

#### 3.3.1 模拟CAS 例

```java
public class CASTest {
    public static void main(String[] args) {
        final ComparedAndSwap cas = new ComparedAndSwap();

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int random = new Random().nextInt(100);
                    boolean res = cas.comparedAndSet(cas.get(), random);
                    System.out.println(res + ": " + cas.get());
                }
            }).start();
        }
    }

}

class ComparedAndSwap {
    private int value = 0;

    /**
     * 获取内存值
     *
     * @return int
     */
    public synchronized int get() {
        return value;
    }

    /**
     * 比较和交换
     *
     * @param exceptedValue 预估值
     * @param newValue      新值
     * @return int 内存旧值
     */
    public synchronized int comparedAndSwap(int exceptedValue, int newValue) {
        int oldValue = value;

        if (oldValue == exceptedValue) {
            this.value = newValue;
        }
        return oldValue;
    }

    /**
     * 比较设置 成功与否
     *
     * @param exceptedValue
     * @param newValue
     * @return
     */
    public synchronized boolean comparedAndSet(int exceptedValue, int newValue) {
        return exceptedValue == comparedAndSwap(exceptedValue, newValue);
    }
}
```

一次结果：

```shell
true: 74
true: 55
false: 74
true: 79
false: 74
true: 74
false: 74
true: 79
true: 78
true: 89
```



## 4. ConcurrentHashMap 锁分段机制

### 4.1 [Java集合之HashMap+ConcurrentHashMap详解](https://zhuanlan.zhihu.com/p/370228995)

### 4.2 CopyOnWriteArrayList

> CopyOnWriteArrayList、CopyOnWriteArraySet          写入并复制
>
> 注意："添加"操作多时，效率低，因为每次添加时都会复制，开销大；并发迭代操作多时可选择。

- **<u>在迭代时添加数据不会报并发异常</u>**

```java
public class CopyOnWriteTest {
    public static void main(String[] args) {
        Thread3 thread3 = new Thread3();
        for (int i = 0; i < 10; i++) {
            new Thread(thread3).start();
        }
    }
}


class Thread3 implements Runnable {
    private static CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    static {
        copyOnWriteArrayList.add("AA");
        copyOnWriteArrayList.add("BB");
        copyOnWriteArrayList.add("CC");
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " ----> ");
        Iterator<String> iterator = copyOnWriteArrayList.iterator();
        // 遍历时又添加
        while (iterator.hasNext()) {
            copyOnWriteArrayList.add("DD");
            System.out.print(iterator.next() + ", ");
        }
    }
}
```

一次运行结果：

```shell
Thread-0 ----> 
Thread-4 ----> 
Thread-3 ----> 
Thread-2 ----> 
Thread-1 ----> 
Thread-7 ----> 
Thread-6 ----> 
Thread-5 ----> 
AA, Thread-9 ----> 
AA, Thread-8 ----> 
AA, BB, BB, AA, BB, CC, BB, AA, BB, CC, AA, BB, DD, CC, CC, AA, AA, DD, DD, DD, AA, AA, BB, CC, DD, CC, DD, DD, BB, DD, BB, BB, CC, DD, DD, DD, DD, DD, DD, DD, CC, CC, CC,
```



## 5. CountDownLatch 闭锁

### 5.1 介绍

> **<u>闭锁：在完成某些运算时，只有其他所有线程的运算都完成，当前线程运算才继续执行。</u>**
>
> 用于：
>
> 1. 计算多个线程执行时间
> 2. 需要多个线程完成运算，对每个结果再操作
> 3. ...

### 5.2 使用 CountDownLatch 计算多个线程执行时间

```java
public class CountDownLatchTest {
    public static void main(String[] args) {
        long s = System.currentTimeMillis();

        // 5个线程
        CountDownLatch latch = new CountDownLatch(5);
        Thread4 thread4 = new Thread4(latch);
        for (int i = 0; i < 5; i++) {
            new Thread(thread4).start();
        }

        // 主线程等待分线程执行完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long e = System.currentTimeMillis();

        System.out.println("消耗时间：" + (e - s));
    }
}


class Thread4 implements Runnable {
    private CountDownLatch latch;

    public Thread4(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            // 操作
            for (int i = 0; i < 1000; i++) {
                if (1000 % 2 == 0) {
                    System.out.print(Thread.currentThread().getName() + "-" + i + ", ");
                }
            }
        } finally {
            latch.countDown();
        }
    }
}
```

结果：

```shell
...省略 消耗时间：28
```



## 6. 实现Callable接口

### 6.1 **Callable** 与 **Runnable** 区别

> Callable：
>
> 1. 可以有返回值，使用 FutureTask 接收返回值
> 2. 可以抛出异常

### 6.2 FutureTask 特点

> 也具有“**<u>闭锁</u>**”特征

### 6.3 Callable、FutureTask 例

- 开5个线程
- FutureTask 具有“闭锁”
- futureTask.get() 获取结果时，对应线程一定执行完成

```java
public class CallableTest {

    public static void main(String[] args) {
        Thread5 thread5 = new Thread5();
        FutureTask<Integer> futureTask1 = new FutureTask<>(thread5);
        FutureTask<Integer> futureTask2 = new FutureTask<>(thread5);
        FutureTask<Integer> futureTask3 = new FutureTask<>(thread5);
        FutureTask<Integer> futureTask4 = new FutureTask<>(thread5);
        FutureTask<Integer> futureTask5 = new FutureTask<>(thread5);


        new Thread(futureTask1).start();
        new Thread(futureTask2).start();
        new Thread(futureTask3).start();
        new Thread(futureTask4).start();
        new Thread(futureTask5).start();

        System.out.println("----可能在线程前执行--->");

        // 获取线程返回结果
        try {
            Integer sum1 = futureTask1.get();
            System.out.println("sum1");
            Integer sum2 = futureTask2.get();
            System.out.println("sum2");
            Integer sum3 = futureTask3.get();
            System.out.println("sum3");
            Integer sum4 = futureTask4.get();
            System.out.println("sum4");
            Integer sum5 = futureTask5.get();
            System.out.println("sum5");

            System.out.println(sum1 + sum2 + sum3 + sum4 + sum5);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("<----------------");
    }

}

class Thread5 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("开始线程-" + Thread.currentThread().getName());
        Thread.sleep(200);
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            sum += i;
        }
        return sum;
    }
}
```

结果：

```shell
开始线程-Thread-0
开始线程-Thread-4
开始线程-Thread-3
开始线程-Thread-2
----可能在线程前执行--->
开始线程-Thread-1
sum1
sum2
sum3
sum4
sum5
24750
<----------------
```

## 7. Lock 同步锁

### 7.1 解决线程安全的三种方式

***synchronized*** (隐式锁)

1. 同步代码块
2. 同步方法

***Lock*** (显式锁，需要手动 ***lock.lock()、lock.unlock()*** 上锁、释放锁)

3. 同步锁

### 7.2 多线程卖票 ReentrantLock

```java
public class LockTest {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(ticket, "1号窗口").start();
        new Thread(ticket, "2号窗口").start();
        new Thread(ticket, "3号窗口").start();
    }
}

class Ticket implements Runnable {
    /**
     * 共享数据
     */
    private int ticket = 50;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        while (true) {
            // 对操作共享数据部分上锁
            lock.lock();
            try {
                if (ticket > 0) {

                    Thread.sleep(20);

                    System.out.println(Thread.currentThread().getName() + "卖出，剩余 " + --ticket);
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
    }
}
```



## 8. Condition 控制线程通信

### 8.1 synchronized 生产者、消费者例

- 使用 ***synchronized*** 关键字，同步方法
- ***this.wait()、this.notifyAll()***  线程通信

```java
public class ProductorAndConsumerTest {
    public static void main(String[] args) {
        Clerk clerk = new Clerk();

        Productor productor = new Productor(clerk);
        Consumer consumer = new Consumer(clerk);

        new Thread(productor, "生产者 A").start();
        new Thread(productor, "生产者 C").start();
        new Thread(consumer, "消费者 B").start();
        new Thread(consumer, "消费者 D").start();
    }
}

/**
 * 店员
 */
class Clerk {
    /**
     * 最大可容纳产品数
     */
    private int maxSize = 10;

    /**
     * 产品数
     */
    private int product = 0;

    /**
     * 进货
     */
    public synchronized void get() {
        // 产品已满，避免虚假唤醒，使用循环判断
        while (product >= maxSize) {
            System.out.println(Thread.currentThread().getName() + "：产品已满");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 产品+1
        System.out.println(Thread.currentThread().getName() + "进货，剩余产品: " + ++product);
        // 通知有货
        this.notifyAll();
    }


    /**
     * 卖货
     */
    public synchronized void sale() {
        while (product <= 0) {
            System.out.println(Thread.currentThread().getName() + "：缺货！");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "卖货，剩余产品: " + --product);
        // 通知有空位
        this.notifyAll();
    }
}

/**
 * 生产者
 */
class Productor implements Runnable {
    private Clerk clerk;

    public Productor(Clerk clerk) {
        this.clerk = clerk;
    }


    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.get();
        }
    }
}

/**
 * 消费者
 */
class Consumer implements Runnable {
    private Clerk clerk;

    public Consumer(Clerk clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.sale();
        }
    }
}
```

### 8.2 Lock、Condition 生产者、消费者例

- 使用 ***ReentrantLock*** 锁
- 使用 ***condition.await()、condition.signalAll()*** 线程通信

```java
public class ProductorAndConsumerLockTest {
    public static void main(String[] args) {
        Clerk clerk = new Clerk();

        Productor productor = new Productor(clerk);
        Consumer consumer = new Consumer(clerk);

        new Thread(productor, "生产者 A").start();
        new Thread(productor, "生产者 C").start();
        new Thread(consumer, "消费者 B").start();
        new Thread(consumer, "消费者 D").start();
    }
}

/**
 * 店员
 */
class Clerk {
    /**
     * 最大可容纳产品数
     */
    private int maxSize = 10;

    /**
     * 产品数
     */
    private int product = 0;


    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();


    /**
     * 进货
     */
    public void get() {
        lock.lock();
        try {
            // 产品已满，避免虚假唤醒，使用循环判断
            while (product >= maxSize) {
                System.out.println(Thread.currentThread().getName() + "：产品已满");
                try {
                    // 等待空位
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 产品+1
            System.out.println(Thread.currentThread().getName() + "进货，剩余产品: " + ++product);
            // 通知有货
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 卖货
     */
    public void sale() {
        lock.lock();
        try {
            while (product <= 0) {
                System.out.println(Thread.currentThread().getName() + "：缺货！");
                try {
                    // 等待有货
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "卖货，剩余产品: " + --product);
            // 通知有空位
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 生产者
 */
class Productor implements Runnable {
    private Clerk clerk;

    public Productor(Clerk clerk) {
        this.clerk = clerk;
    }


    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.get();
        }
    }
}

/**
 * 消费者
 */
class Consumer implements Runnable {
    private Clerk clerk;

    public Consumer(Clerk clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.sale();
        }
    }
}
```



## 9. 线程锁竞争的八种情况

锁竞争总结：

- 同一个类中的同步方法或同步变量存在**锁竞争**；
- 规律，调用时的锁是什么？
  - 默认 ***synchronized*** 关键字，锁是 ***this***
  - 静态同步方法、变量，锁是 ***类.class*** 对象，下例中，调用静态同步方法时，两个 **Thread8MonitorDemo** 对象锁都是 ***类.class*** 对象，是同一把锁，存在锁竞争

```java
/**
 * @author zhanghua
 * @createTime 2021/5/11 13:40
 * 1. 两个普通同步方法，打印：one two
 * 2. 两个普通同步方法，新增 sleep 给 printOne()，打印：one  two
 * 3. 新增普通方法 printThree()，打印：three  one  two
 * 4. 两个普通同步方法，两个 Thread8MonitorDemo 对象，打印：two  one
 * 5. 两个普通同步方法，printOne()修改为静态，打印：two  one
 * 6. 两个普通静态同步方法，打印：one  two
 * 7. 一个静态，一个非静态，两个 Thread8MonitorDemo 对象，打印：two  one
 * 8. 两个静态，两个 Thread8MonitorDemo 对象，打印：one  two
 */
public class Thread8MonitorTest {
    public static void main(String[] args) {
        final Thread8MonitorDemo demo = new Thread8MonitorDemo();
        final Thread8MonitorDemo demo2 = new Thread8MonitorDemo();

        // 打印 one
        new Thread(new Runnable() {
            @Override
            public void run() {
                demo.printOne();
            }
        }).start();


        // 打印 two
        new Thread(new Runnable() {
            @Override
            public void run() {
//                demo.printTwo();
                demo2.printTwo();
            }
        }).start();

        // 打印 three
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                demo.printThree();
//            }
//        }).start();
    }
}


class Thread8MonitorDemo {
    public static synchronized void printOne() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("one");
    }

    public static synchronized void printTwo() {
        System.out.println("two");
    }

    public void printThree() {
        System.out.println("three");
    }
}
```



## 10. 线程按序交替

> 编写一个程序，开启三个线程，这三个线程的ID分别为**A、B、C**，每个线程将自己的ID在屏幕上打印 **10** 遍，要求输出的结果必须按顺序显示
>
> 如：**ABCABCABC**...依次递归

- 定义状态变量：打印状态，1为A线程，2为B线程，3为C线程
- ReentrantLock
- 三个通信Condition

```
public class AternativeThreadTest {
    public static void main(String[] args) {
        final AternateDemo aternateDemo = new AternateDemo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打印 10 次
                for (int i = 0; i < 10; i++) {
                    aternateDemo.printA();
                }
            }
        }, "A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打印 10 次
                for (int i = 0; i < 10; i++) {
                    aternateDemo.printB();
                }
            }
        }, "B").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打印 10 次
                for (int i = 0; i < 10; i++) {
                    aternateDemo.printC();
                }
            }
        }, "C").start();
    }
}

class AternateDemo {
    /**
     * 打印状态，1为A线程，2为B线程，3为C线程
     */
    private int num = 1;

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void printA() {
        lock.lock();
        try {
            // 不该A打印
            if (num != 1) {
                condition1.await();
            }
            // 每次打印一次
            for (int i = 0; i < 1; i++) {
                System.out.print(Thread.currentThread().getName());
            }
            // 完成后交给 B 线程
            num = 2;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            // 不该B打印
            if (num != 2) {
                condition2.await();
            }
            // 每次打印一次
            for (int i = 0; i < 1; i++) {
                System.out.print(Thread.currentThread().getName());
            }
            // 完成后交给 C 线程
            num = 3;
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            // 不该 C 打印
            if (num != 3) {
                condition3.await();
            }
            // 每次打印一次
            for (int i = 0; i < 1; i++) {
                System.out.print(Thread.currentThread().getName());
            }
            // 完成后交给 A 线程
            num = 1;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

结果

```shell
ABCABCABCABCABCABCABCABCABCABC
```



## 11. ReadWriteLock 读写锁

> **写与写、读与写** **互斥**
>
> **读与读不互斥**
>
> 相比于每次**读**，每次**写**的独占锁的方式，效率高

```java
public class ReadWriteLockTest {
    public static void main(String[] args) {
        final ReadWriteLockDemo demo = new ReadWriteLockDemo();

        // 写 线程
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    demo.write(new Random().nextInt(100));
                }
            }).start();
        }

        // 读 线程
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    demo.get();
                }
            }).start();
        }
    }
}

class ReadWriteLockDemo {
    /**
     * 共享变量
     */
    private int num = 0;

    /**
     * 读写锁
     */
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 读
     *
     * @return int
     */
    public int get() {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + ": num = " + num);
            return num;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 写
     *
     * @param n 参数
     */
    public void write(int n) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + ": write = " + n);
            num = n;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```



## 12. 线程池

### 12.1 介绍

1. 线程池：提供一个线程队列，队列中保存着所有等待状态的线程。避免了创建与销毁的额外开销，提高了相应速度；
2. 线程池的体系结构

```java
java.util.concurrent.Executor : 负责线程使用与调度的根接口
    |-- ExecutorService 子接口 ：线程池的主要接口
    	|-- ThreadPoolExecutor : 线程池实现类
    	|-- SchedualedExecutorService : 子接口：负责线程的调度
            |-- SchedualedThreadPoolExecutor ： 继承 ThreadPoolExecutor，实现 SchedualedExecutorService
```

3. 工具类：Executors

- ExecutorService newFixedThreadPool(int nThreads)  创建固定大小的线程池
- ExecutorService newCachedThreadPool()  创建大小不固定的线程池，根据需求自动更改数量
- ExecutorService newSingleThreadExecutor()  创建大小为1的线程池
- ScheduledExecutorService newScheduledThreadPool()  ：创建固定大小的线程池，可延时或定时执行任务。

### 12.2 使用 Executors 创建 线程池

- 例：
- submit() 提交线程
- 接收线程返回值
- shutdown() 关闭线程池

```java
public class ThreadPoolTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread1 thread1 = new Thread1();
        Thread2 thread2 = new Thread2();

        // 新建线程池
        ExecutorService pool = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            pool.submit(thread1);
        }

        List<Future> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Future<Integer> future = pool.submit(thread2);
            futureList.add(future);
        }

        for (Future<Integer> f : futureList
        ) {
            System.out.println(f.get());
        }
        
        pool.shutdown();
    }
}


class Thread1 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + " : " + i);
        }
    }
}


class Thread2 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 0; i < 101; i++) {
            sum += i;
        }
        return sum;
    }
}
```



## 13. 线程调度

### 13.1 使用 ScheduledThreadPool

```java
public class ScheduledThreadPoolTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);

        for (int i = 0; i < 5; i++) {
            ScheduledFuture<Integer> future = pool.schedule(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int i1 = new Random().nextInt(10);
                    System.out.println(i1);
                    return i1;
                }
            }, 2, TimeUnit.SECONDS);
        }

        pool.shutdown();
    }
}
```



## 14. ForkJoinPool 分支/合并框架 工作窃取

### 14.1 介绍

- 运行效率取决于：临界值、计算值大小

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210511160242.png)

### 14.2 例

```java
public class ForkJoinPoolTest {
    public static void main(String[] args) {
        Instant start = Instant.now();

        ForkJoinPool pool = new ForkJoinPool();

        ForkJoinTask<Long> task = new ForkJoinCalculate(0, 100000000000L);

        Long sum = pool.invoke(task);

        System.out.println(sum);

        Instant end = Instant.now();

        System.out.println("时间: " + Duration.between(start, end).toMillis());
    }

    @Test
    public void commonTest() {
        Instant start = Instant.now();

        long sum = 0;
        for (long i = 0; i < 100000000000L; i++) {
            sum += i;
        }
        System.out.println(sum);

        Instant end = Instant.now();

        System.out.println("时间: " + Duration.between(start, end).toMillis());
    }

}

/**
 * 计算end->start之和
 */
class ForkJoinCalculate extends RecursiveTask<Long> {
    private static final long serialVersionUID = -167898665569008L;

    private long start;
    private long end;

    private static final long THURSHOLD = 10000L;

    public ForkJoinCalculate(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long length = end - start;
        // 在临界值以下
        if (length <= THURSHOLD) {
            long sum = 0L;
            for (Long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        } else {
            long middle = (start + end) / 2;
            ForkJoinCalculate left = new ForkJoinCalculate(start, middle);
            // 拆分小任务，压入线程队列
            left.fork();

            ForkJoinCalculate right = new ForkJoinCalculate(middle + 1, end);
            // 拆分小任务，压入线程队列
            right.fork();

            // 合并小任务
            return left.join() + right.join();
        }
    }
}
```



### 14.3 使用 Java8 新特性 例

- LongStream