import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Ron Rachev
 */
public class CuckooHashTester {

    public static void main(String [] args) {
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("Input number of tests :");
        int numberTests = inputScanner.nextInt();

        ArrayList<CuckooHashTester> myTesters = new ArrayList<>();

        for(int i = 0 ; i < numberTests; i++){
            CuckooHashTester myTester = new CuckooHashTester(i,5);
            myTester.generateRandomTest();
            myTesters.add(myTester);
        }

        int passed    = 0;
        int nonPassed = 0;

        for(int i = 0 ; i <myTesters.size(); i++){
            if(myTesters.get(i).passedTest())
                passed++;
        }
        nonPassed = numberTests - passed;

        System.out.println("\r\n");
        System.out.println("Passed --> " + passed);
        System.out.println("failed --> " + nonPassed);

//        HashMethods hashMethod = new StringHashMethods();
//        CuckooHashing hastTable = new CuckooHashing(hashMethod, 3);
//        System.out.println(hashMethod.hash("a", 0));
//        System.out.println(hashMethod.hash("b", 1));
//        System.out.println(hashMethod.hash("c", 0));
//        hastTable.insert("a");
//        hastTable.insert("b");
//        hastTable.insert("d");
//        hastTable.undo();
    }
    
    private int numStrings;
    private int numInsertActions = 0;
    private Random rand;
    
    private CuckooHashing myCuckoo;
    private HashMethods   myHF;
    
    private int testId;
    
    private boolean failedTest = false;
    
    public CuckooHashTester(int testId, int numStrings)
    {
        this.numStrings           = numStrings;
        this.rand                 = new Random();
        this.myHF                 = new StringHashMethods();
        this.testId               = testId;
    }
    /* Returns a random integer within a given range */ 
    
    public int randInt(int min, int max) {
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
    }
    /* bypasses the private reference*/
    public Object getFieldByReflection(Object classObject,String fieldName)
    {
       try{
        Field parentField = classObject.getClass().getDeclaredField(fieldName);  
        parentField.setAccessible(true);   
        Object parentObj = parentField.get(classObject); 
        
        return (Object)parentObj;
        }catch(Exception reflectionExceptino){
        }
       return null;
    }
    
    public boolean passedTest()
    {
        return !failedTest;
    }
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public static String randomAlphaNumeric(int count) {
    StringBuilder builder = new StringBuilder();
    while (count-- != 0) {
    int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
    builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    return builder.toString();
    }  
    
    private ArrayList<String> beforeArrayStatus   ;
    private ArrayList<String> beforeOverflowStatus;
    
    private ArrayList<String> afterArrayStatus   ;
    private ArrayList<String> afterOverflowStatus;
    
    
    
    
    public void generateRandomTest()
    {
        int sizeOfArray     = randInt(5,15);
        int numOfInsertions = randInt(1,sizeOfArray);
        int sizeString      = randInt(5,10);
        String randomStr    = "Ron";
        
        myCuckoo  = new CuckooHashing(myHF,sizeOfArray);
        
        System.out.println("Generated Test --> "+testId+"\r\n-----------------");
        System.out.println("HashMethods myHF                 = new StringHashMethods();");
        System.out.println("CuckooHashing myCuckoo = new CuckooHashing(myHF,"+sizeOfArray+");");
        
        for(int i = 0 ; i < numOfInsertions-1; i++ )
        {
            randomStr = randomAlphaNumeric(sizeString);
            sizeString = randInt(5,10);
            if(!randomStr.isEmpty()){
            myCuckoo.insert(randomStr);
            System.out.println("myCuckoo.insert(\""+randomStr+"\");");
            }
        } 
        System.out.println("\r\n");
        System.out.println("Current Array\r\n--------");
        System.out.println(myCuckoo.toString());
        System.out.println("--------");
        
        beforeArrayStatus    = new ArrayList<String>();
        beforeOverflowStatus = new ArrayList<String>();
        
        afterArrayStatus     = new ArrayList<String>();
        afterOverflowStatus  = new ArrayList<String>();
        
        String []         ourReflectedArr         =  (String [])getFieldByReflection(myCuckoo,"array");
        ArrayList<String> ourReflectedOverflow    = (ArrayList<String>)getFieldByReflection(myCuckoo,"stash");
        
        for(int i = 0 ; i < ourReflectedArr.length; i++)
            beforeArrayStatus.add(ourReflectedArr[i]+":"+i); 
        for(int i = 0;  i < ourReflectedOverflow.size() ; i++)
            beforeOverflowStatus.add(ourReflectedOverflow.get(i)+":"+i);
        
        System.out.println("Saved Array Status!");
        System.out.println("\r\nInserting Another String & Backtracking");
        randomStr = randomAlphaNumeric(6);
        System.out.println("Generated --> " + randomStr + " & Added To The Array\r\n");
        
        System.out.println("myCuckoo.insert(\""+randomStr+"\");");
        myCuckoo.insert(randomStr);
        
        System.out.println("\r\n-------Starting One backtrack-------\r\n");
        myCuckoo.undo();
        
        for(int i = 0 ; i < ourReflectedArr.length; i++)
            afterArrayStatus.add(ourReflectedArr[i]+":"+i);
        
        for(int i = 0;  i < ourReflectedOverflow.size() ; i++)
            afterOverflowStatus.add(ourReflectedOverflow.get(i)+":"+i);
        
        if(afterArrayStatus.size() != beforeArrayStatus.size()){
            failedTest = true;
            System.out.println("Failed 1");
        }
        if(afterOverflowStatus.size() != beforeOverflowStatus.size()){
            System.out.println("Failed 2");
            failedTest = true;
        }
        for(int i = 0 ; i < afterArrayStatus.size(); i++){
            if(!afterArrayStatus.get(i).equals(beforeArrayStatus.get(i))){
                System.out.println("Failed 3");
                failedTest = true;
                break;
            }
        }
        for(int i = 0 ; i < beforeOverflowStatus.size(); i++){
            if(!beforeOverflowStatus.get(i).equals(afterOverflowStatus.get(i))){
                System.out.println("Failed 4");
                failedTest = true;
                break;
            }
        }
        if(!failedTest){
            System.out.println("Passed Test ---> " + testId+" ");
        }
        System.out.println("-----------------\r\n");
        
    }
    
    public void startTest()
    {
        generateRandomTest();
    }
         
    
}
