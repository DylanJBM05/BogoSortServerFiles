/**
 * BogoSort logic.
 * @author Brayden Buchner & Dylan McGowan
 */
import java.util.Arrays;
import java.util.Random;

public class BogoSort implements Runnable {
	private boolean finished = false;
/**
 * Randomizes data in BogoSort
 * @param data - Array containing randomized numbers
 */
    public static void shuffle(int[] data) {
    	//Uses random and swaps place of data using for loop
        Random random = new Random();
        for (int i = data.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }
    }
    /**
     * Method for sorting and checking if array is sorted.
     * @param data - Array containing randomized numbers
     */
    public static void sort(int[] data) {
        boolean needsSorting = true;
        while (needsSorting) {
        	//Calls randomizer
            BogoSort.shuffle(data);
            needsSorting = false;
            
            //Checks if array is correct
            for (int i = 0; i < data.length - 1; i++) {
                if (data[i] > data[i + 1]) {
                    needsSorting = true;
                    break;
                }
            }
            //If it isn't sorted try again
            if (needsSorting) {
                continue;
            }
        }
    }

    /**
     * Thread for starting the method, includes all logic and connections to other methods in the class.
     */
	@Override
	public void run() {
		//Creates a 16 integer sized array
		int[] data = new int[16];
		
		//Uses random to randomize the digits in the array from 1-100
        Random random = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextInt(100);
        }
        //First shuffle
        BogoSort.shuffle(data);
        
        //Start sort cycle
        BogoSort.sort(data);
        
        //If finished change flag to true
        finished = true;
        
        //Display array when done to check if it is sorted.
        System.out.printf("BOGO Sort: %s\n", Arrays.toString(data));
	}
/**
 * Getter and setter for if the loop is done.
 * @return - Returns finished flag.
 */
	public boolean isFinished() {
		return finished;
	}
}
 