import java.util.*;

/**
 * @author      Yaniv Krol
 * @version     1.0
 */

public class BTreeTester {

    public static void main(String[] args) {
        BTreeTester btreeTest = new BTreeTester(1, 10, true);
        btreeTest.start();
    }

    private Random rand = new Random();
    private Scanner reader = new Scanner(System.in);
    private BTree<Integer> btree = new BTree<>();
    private List<Integer> availableToInsert;
    private List<Integer> inserted;
    private boolean repetitions;
    private final int RANDOM_OP_NUM = 3;
    private String[] randomOptions = {"Insert random", "Delete random"};
    private String[] specificOptions = {"Insert", "Delete"};
    private List blockedOptionsWhenEmpty = new ArrayList<>(Arrays.asList(2, 22));
    private List blockedOptionsWhenFull = new ArrayList<>(Arrays.asList(1, 11));

    /**
     * @param repetitions - whether numbers can appear multiple times
     * @param firstNumber - key of the first node
     * @param lastNumber - key of the last node
     *
     * firstNumber - lastNumber + 1 will be the number of numbers available to insert
     */
    public BTreeTester(int firstNumber, int lastNumber, boolean repetitions) {
        this.repetitions = repetitions;
        int initialCapacity = lastNumber - firstNumber + 1;
        availableToInsert = new ArrayList<>(initialCapacity);
        inserted = new ArrayList<>(initialCapacity);
        for (int i=firstNumber; i<=lastNumber; i++)
            availableToInsert.add(i);
    }

    public void start() {

        printOptions();

        while (true) {
            System.out.print("Enter option number: ");
            int whatToDo = reader.nextInt();
            if (whatToDo == RANDOM_OP_NUM) {
                whatToDo = (rand.nextInt(2) + 1)*11;
                while ((inserted.isEmpty() && whatToDo == 22) || (availableToInsert.isEmpty() && whatToDo == 11))
                    whatToDo = (rand.nextInt(2) + 1)*11;
                System.out.println("Random operation: "+whatToDo);
            } else {
                if (inserted.isEmpty() && blockedOptionsWhenEmpty.contains(whatToDo)) {
                    System.out.println("The tree is empty, can't do this operation");
                    continue;
                } else if (availableToInsert.isEmpty() && blockedOptionsWhenFull.contains(whatToDo)) {
                    System.out.println("No more numbers to insert, can't do this operation");
                    continue;
                }
            }

            switch (whatToDo) {
                case (1): // Insert
                    StringBuilder toPrint = new StringBuilder("Available nodes: ");
                    for (int i : availableToInsert)
                        toPrint.append(i).append(" ");
                    System.out.println(toPrint);
                    System.out.print("Enter value to insert: ");
                    int toInsert = reader.nextInt();
                    if (!repetitions)
                        toInsert = availableToInsert.remove(availableToInsert.indexOf(toInsert));
                    inserted.add(toInsert);
                    System.out.println("Inserting " + toInsert+"\n");
                    btree.add(toInsert);
//                    btree.insert(toInsert);
                    printTree();
                    break;
                case (11): // Insert random
                    int rn = rand.nextInt(availableToInsert.size());
                    toInsert = repetitions ? availableToInsert.get(rn) : availableToInsert.remove(rn);
                    inserted.add(toInsert);
                    System.out.println("Inserting " + toInsert+"\n");
                    btree.add(toInsert);
//                    btree.insert(toInsert);
                    printTree();
                    break;
                case (2): // Delete
                    System.out.print("Enter value to delete: ");
                    int toDelete = reader.nextInt();
                    System.out.println("Deleting " + toDelete+"\n");
                    btree.remove(toDelete);
//                    btree.delete(toDelete);
                    inserted.remove((Integer) toDelete);
                    if (!repetitions) {
                        availableToInsert.add(toDelete);
                        availableToInsert.sort(Comparator.comparingInt(o -> o));
                    }
                    printTree();
                    break;
                case (22):  // Delete random
                    toDelete = inserted.remove(rand.nextInt(inserted.size()));
                    if (!repetitions) {
                        availableToInsert.add(toDelete);
                        availableToInsert.sort(Comparator.comparingInt(o -> o));
                    }
                    System.out.println("Deleting " + toDelete+"\n");
                    btree.remove(toDelete);
//                    btree.delete(toDelete);
                    printTree();
                    break;
            }
            System.out.println();
            System.out.println("---------------------------------------\n");
        }
    }

    private void printTree() {
        System.out.println(btree.toString());
    }

    private void printOptions() {
        System.out.println("Options:");
        for (int i = 1; i <= specificOptions.length; i++)
            System.out.println(i + "  - " + specificOptions[i-1]);
        System.out.println(RANDOM_OP_NUM+"  - Random operation");
        for (int i = 1; i <= randomOptions.length; i++)
            System.out.println(i+""+i + " - " + randomOptions[i-1]);
        System.out.println("---------------------------------------\n");
    }

}
