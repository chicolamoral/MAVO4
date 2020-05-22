import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author      Yaniv Krol
 * @version     1.1
 */

public class BTreeTester {

    public static void main(String[] args) throws NoSuchMethodException {

        // These 6 params are what you can change, explanations in constructor string doc
        int order = 2;
        int firstNumber = 1;
        int lastNumber = 10;
        boolean repetitionsAllowed = false;
        String insertMethod = "add"; // "insert", "insert2pass"
        String deleteMethod = "remove"; // "delete"

        BTreeTester btreeTest = new BTreeTester(order,firstNumber, lastNumber, repetitionsAllowed, insertMethod, deleteMethod);
        btreeTest.startUI();
    }

    /**
     * @param order - order of tree (t as learned in class)
     * @param repetitionsAllowed - whether numbers can appear multiple times
     * @param firstNumber - key of the first node
     * @param lastNumber - key of the last node
     * @param insertMethod - name of insert method to use ("add", "insert", "insert2pass")
     * @param deleteMethod - name of delete method to use ("remove", "delete")
     *
     * firstNumber - lastNumber + 1 will be the number of keys available to insert
     */
    public BTreeTester(int order, int firstNumber, int lastNumber, boolean repetitionsAllowed,
                       String insertMethod, String deleteMethod) throws NoSuchMethodException {
        System.out.println("Generating BTree with order(t) "+order);
        this.btree = new BTree<>(order);
        this.insertMethod = BTree.class.getMethod(insertMethod, Comparable.class);
        this.deleteMethod = BTree.class.getMethod(deleteMethod, Comparable.class);
        this.repetitionsAllowed = repetitionsAllowed;
        int initialCapacity = lastNumber - firstNumber + 1;
        availableToInsert = new ArrayList<>(initialCapacity);
        inserted = new ArrayList<>(initialCapacity);
        for (int i=firstNumber; i<=lastNumber; i++)
            availableToInsert.add(i);
    }

    private Random rand = new Random();
    private Scanner reader = new Scanner(System.in);
    private BTree<Integer> btree;
    private Method insertMethod;
    private Method deleteMethod;
    private List<Integer> availableToInsert;
    private List<Integer> inserted;
    private boolean repetitionsAllowed;
    private List blockedOptionsWhenFull = new ArrayList<>(Arrays.asList(1, 11, 111));
    private List blockedOptionsWhenEmpty = new ArrayList<>(Arrays.asList(2, 22, 222));
    private String[][] options = {
            {"Insert", "Delete"},
            {"Insert random", "Delete random"},
            {"Insert random multiple times", "Delete random multiple times"}
    };

    /**
     * Start the user interface
     */
    public void startUI() {

        printOptions();

        while (true) {
            System.out.print("Enter option number: ");
            int whatToDo = reader.nextInt();
            if (inserted.isEmpty() && blockedOptionsWhenEmpty.contains(whatToDo)) {
                System.out.println("The tree is empty, can't do this operation");
                continue;
            } else if (availableToInsert.isEmpty() && blockedOptionsWhenFull.contains(whatToDo)) {
                System.out.println("No more keys to insert, can't do this operation");
                continue;
            }

            switch (whatToDo) {
                case (1): // Insert
                    insertByInput();
                    break;
                case (11): // Insert random
                    insertRandom();
                    break;
                case (111): // Insert random multiple times
                    System.out.print("How many times? ");
                    int times = reader.nextInt();
                    for (int i=0; i<times; i++) {
                        if (availableToInsert.isEmpty())
                            break;
                        insertRandom();
                    }
                    break;
                case (2): // Delete
                    deleteByInput();
                    break;
                case (22):  // Delete random
                    deleteRandom();
                    break;
                case (222): // delete random multiple times
                    System.out.print("How many times? ");
                    times = reader.nextInt();
                    for (int i=0; i<times; i++) {
                        if (inserted.isEmpty())
                            break;
                        deleteRandom();
                    }
                    break;
            }
            System.out.println("\n---------------------------------------\n");
        }
    }

    private void printTree() {
        System.out.println(btree.toString());
    }

    private void invokeInsert(int toInsert) {
        try {
            insertMethod.invoke(btree, toInsert);
        } catch (IllegalAccessException e) {
            System.out.println("Wrong method name");
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        }
    }

    private void invokeDelete(int toDelete) {
        try {
            deleteMethod.invoke(btree, toDelete);
        } catch (IllegalAccessException e) {
            System.out.println("Wrong method name");
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        }
    }

    private void insertByInput() {
        StringBuilder toPrint = new StringBuilder("Available keys: ");
        for (int i : availableToInsert)
            toPrint.append(i).append(" ");
        System.out.println(toPrint);
        System.out.print("Enter value to insert: ");
        int toInsert = reader.nextInt();
        if (!repetitionsAllowed)
            toInsert = availableToInsert.remove(availableToInsert.indexOf(toInsert));
        inserted.add(toInsert);
        System.out.println("Inserting " + toInsert+"\n");
        invokeInsert(toInsert);
        printTree();
    }

    private void deleteByInput() {
        System.out.print("Enter value to delete: ");
        int toDelete = reader.nextInt();
        System.out.println("Deleting " + toDelete+"\n");
        invokeDelete(toDelete);
        inserted.remove((Integer) toDelete);
        if (!repetitionsAllowed) {
            availableToInsert.add(toDelete);
            availableToInsert.sort(Comparator.comparingInt(o -> o));
        }
        printTree();
    }

    private void insertRandom() {
        int rn = rand.nextInt(availableToInsert.size());
        int toInsert = repetitionsAllowed ? availableToInsert.get(rn) : availableToInsert.remove(rn);
        inserted.add(toInsert);
        System.out.println("Inserting " + toInsert+"\n");
        invokeInsert(toInsert);
        printTree();
    }

    private void deleteRandom() {
        int toDelete = inserted.remove(rand.nextInt(inserted.size()));
        if (!repetitionsAllowed) {
            availableToInsert.add(toDelete);
            availableToInsert.sort(Comparator.comparingInt(o -> o));
        }
        System.out.println("Deleting " + toDelete+"\n");
        invokeDelete(toDelete);
        printTree();
    }

    private void printOptions() {
        System.out.println("Options:");
        for (int i = 0; i < options.length; i++)
            for (int j = 0; j < options[i].length; j++) {
                StringBuilder str = new StringBuilder();
                for (int k=0; k<i+1; k++)
                    str.append(j+1);
                for (int k = options.length-1; k>i; k--)
                    str.append(" ");
                System.out.println(str.append(" - ").append(options[i][j]));
            }
    }

}
