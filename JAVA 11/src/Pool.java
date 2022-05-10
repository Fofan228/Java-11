import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Pool {

    public static void main(String[] args) throws InterruptedException {

        // создаем пул для выполнения наших задач
        //   максимальное количество созданных задач - 3
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                // не изменяйте эти параметры
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

        // сколько задач выполнилось
        AtomicInteger count = new AtomicInteger(0);

        // сколько задач выполняется
        AtomicInteger inProgress = new AtomicInteger(0);

        // отправляем задачи на выполнение
        for (int i = 0; i < 12; i++) {

            final int number = i;
            Thread.sleep(10);
            System.out.println("creating #" + number);

            executor.submit(() -> {
                int working = inProgress.incrementAndGet();
                System.out.println("start #" + number + ", in progress: " + working);
                try {

                    while (working < executor.getMaximumPoolSize()) {
                        executor.wait();
                        Thread.sleep(Math.round(1000 + Math.random() * 2000));
                    }

                } catch (InterruptedException e) {
                    // ignore
                }
                while (working > 0) {
                    working = inProgress.decrementAndGet();
                    System.out.println("end #" + (number - working) + ", in progress: " + working + ", done tasks: " + count.incrementAndGet());
                }
                executor.notifyAll();
                return null;
            });

        }
    }
}