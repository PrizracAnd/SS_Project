public class Account implements Runnable{

    private String userName;
    private String publicKey;
    private boolean status;
    private volatile long answerTime;                   // <-- обеспечение синхронизации (volatile)
    private volatile int answerInterval;                // <-- обеспечение синхронизации (volatile)
    private String ipAddress;
    private long idDb;
    private boolean isWorking;

    private Thread statusFalser;
    private volatile boolean statusFalserStopped;       // <-- обеспечение синхронизации (volatile)

    // Constructor
    public Account (String userName){
        this.userName = userName;
        this.status = false;
        this.idDb = -1;            // Пользователя пока нет в БД
        this.isWorking = true;
        this.statusFalser = new Thread(this);
    }

    // Constructor for DB
    public Account(String userName, long idDb, String ipAddress, boolean isWorking){
        this(userName); // <-- вызываем предыдущий конструктор, т.к. в нем есть много вещей для правильного создания нашего объекта
        // А теперь обновляем нужные нам поля объекта:
        this.idDb = idDb;
        this.ipAddress = ipAddress;
        this.isWorking = isWorking; // <-- вот тут внимательно, т.к. в объекте это поле - логическая переменная, а в БД это текст.
                                    //     и в данном случае конвертация из текста в единицу/ноль уже ложиться на тот класс,
                                    //     в котором будет создаваться этот объект.
    }


    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean getStatus() {
        return status;
    }

    public synchronized void setStatus(boolean status) {                        // <-- обеспечение синхронизации (synchronized)
        this.status = status;
        if (status) {
            this.answerTime = System.currentTimeMillis();
            if(!isAliveStatusFalser()) {
                this.statusFalserStopped = false;
                this.statusFalser.start();
            }
        }
    }

    public long getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(long answerTime) {
        this.answerTime = answerTime;
    }

    public int getAnswerInterval() {
        return answerInterval;
    }

    public void setAnswerInterval(int answerInterval) {
        this.answerInterval = answerInterval;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getIdDb() {
        return idDb;
    }

    public void setIdDb(long idDb) {
        this.idDb = idDb;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public boolean getStatusFalserStopped() {
        return statusFalserStopped;
    }

    public void setStatusFalserStopped(boolean statusFalserStopped) {
        this.statusFalserStopped = statusFalserStopped;
    }

    public boolean isAliveStatusFalser(){
        return statusFalser.isAlive();
    }
    // StatusFalser methods

    @Override
    public void run() {
        while (!statusFalserStopped){
            long currentTime = System.currentTimeMillis();
            if (currentTime  > (answerTime + answerInterval)){
                setStatus(false); // <-- теперь работаем через синхронизированный метод.
                break;
            }
            else try {
                this.statusFalser.wait((answerTime + answerInterval - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopStatusFalser(){
        this.statusFalserStopped = true;
        this.statusFalser.notify();
    }
}
