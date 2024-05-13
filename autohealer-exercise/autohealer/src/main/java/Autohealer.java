/* 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

/**
 *
 * @author zead shalaby
 */
import org.apache.zookeeper.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Autohealer implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;

    // Parent Znode where each worker stores an ephemeral child to indicate it is
    // alive
    private static final String AUTOHEALER_ZNODES_PATH = "/workers";

    // Path to the worker jar
    private final String pathToProgram;

    // The number of worker instances we need to maintain at all times
    private final int numberOfWorkers;
    private ZooKeeper zooKeeper;
    private int numberOfNodes = 0;

    public Autohealer(int numberOfWorkers, String pathToProgram) {
        this.numberOfWorkers = numberOfWorkers;
        this.pathToProgram = pathToProgram;
    }

    public void startWatchingWorkers() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(AUTOHEALER_ZNODES_PATH, false) == null) {
            zooKeeper.create(AUTOHEALER_ZNODES_PATH, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        launchWorkersIfNecessary();
    }

    public void connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to Zookeeper :)....");
                } else {
                    synchronized (zooKeeper) {
                        System.out.println("Disconnected from Zookeeper event :(..!)");
                        zooKeeper.notifyAll();
                    }
                }
                break;
            case NodeChildrenChanged:
                launchWorkersIfNecessary();
                break;
        }
    }

    /*
     * // ! launchWorkersIfNecessary prints out the number of currently active
     * // ? workers.
     * // ? If the number of workers is less than a specified threshold
     * // ? (numberOfWorkers), it starts a new worker.
     * // ? Here's a breakdown of the main components:
     ***/
    private void launchWorkersIfNecessary() {
        try {
            List<String> children = zooKeeper.getChildren(AUTOHEALER_ZNODES_PATH, this);
            System.out.println(String.format("Currently there are %d workers", children.size()));

            if (children.size() < numberOfWorkers) {
                startNewWorker();
            }
        } catch (InterruptedException | KeeperException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Helper method to start a single worker
     * 
     * @throws IOException
     */
    private void startNewWorker() throws IOException {
        File file = new File(pathToProgram);
        String command = "java -jar " + file.getName();
        System.out.println(String.format("Launching worker instance : %s ", command));
        Runtime.getRuntime().exec(command, null, file.getParentFile());
    }
}
