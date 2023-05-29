import java.util.Arrays;
import java.util.Arrays;
import java.util.Random;

public class Main {

	// Define the maximum temperature and cooling rate for the annealing process
	private static final double MAX_TEMPERATURE = 10000;
	private static final double COOLING_RATE = 0.03;
	private static int[] values = { 68, 64, 47, 55, 72, 53, 81, 60, 72, 80, 62, 42, 48, 47, 68, 51, 48, 68, 83, 55, 48,
			44, 49, 68, 63, 71, 82, 55, 60, 63, 56, 75, 42, 76, 42, 60, 75, 68, 67, 42, 71, 58, 66, 72, 67, 78, 49, 50,
			51 };
	private static int[] weights = { 21, 11, 11, 10, 14, 12, 12, 14, 17, 13, 11, 13, 17, 14, 16, 10, 18, 10, 16, 17, 19,
			12, 12, 16, 16, 13, 17, 12, 16, 13, 21, 11, 11, 10, 14, 12, 12, 14, 17, 13, 11, 13, 17, 14, 16, 10, 18, 10,
			16 };
	private static int knapsackCapacity = 300;

	// Define the solution state variables
	private static boolean[] currentSolution;
	private static boolean[] bestSolution;
	private static int currentValue;
	private static int bestValue;

	public static void main(String[] args) {
		// Initialize the solution state variables
		currentSolution = new boolean[values.length];
		bestSolution = new boolean[values.length];
		currentValue = 0;
		bestValue = 0;

		// we choose items like excel example
		int tempKnapsackCapacity = knapsackCapacity;
		int tempCurrentValue = currentValue;

		int iteration = 0;
		int lastiteration = 200;
		long executionTime[] = new long[lastiteration + 1];
		for (int i = 0; i < values.length; i++) {
			long startingTime = System.nanoTime();
			if (tempKnapsackCapacity > weights[i]) {
				tempKnapsackCapacity -= weights[i];
				tempCurrentValue += values[i];
				currentSolution[i] = true;
			} else
				currentSolution[i] = false;
			long stoppingTime = System.nanoTime();
			executionTime[0] = stoppingTime - startingTime;
		}
		// Start the simulated annealing process
		// ..............

		// Initialize the random number generator
		Random random = new Random();
		Double randomArr[] = new Double[lastiteration + 1];
		for (int i = 0; i < randomArr.length; i++) {
			randomArr[i] = random.nextDouble();
		}
		int listofOFV[] = new int[lastiteration + 1];
		String Acceptancy[] = new String[lastiteration + 1];
		listofOFV[0] = tempCurrentValue;
		bestValue = tempCurrentValue;
		double currentTemp = MAX_TEMPERATURE;
		double currentProb = 0;

		while (iteration != lastiteration) {
			long startingTime = System.nanoTime();
			iteration++;
			int counterTrue = 0;
			int counterFalse = 0;
			for (int i = 0; i < currentSolution.length; i++) {
				if (currentSolution[i] == true) {
					counterTrue++;
				} else {
					counterFalse++;
				}
			}
			int intobag = random.nextInt(counterFalse) + 1;
			int outofbag = random.nextInt(counterTrue) + 1;
			int falsee = 0;
			int trueee = 0;

			for (int i = 0; i < currentSolution.length; i++) {
				if (currentSolution[i] == true) {
					trueee++;
					if (trueee == outofbag) {
						currentSolution[i] = false;
					}
				} else {
					falsee++;
					if (falsee == intobag) {
						currentSolution[i] = true;
					}
				}
			}

			currentValue = calculateValue(currentSolution);
			long stoppingTime = System.nanoTime();

			// we think comparison does not need to affect the time; therefore stopping time
			// has to be calculated before the comparison
			if (bestValue < currentValue) {
				bestValue = currentValue;
				for (int i = 0; i < currentSolution.length; i++) {
					bestSolution[i] = currentSolution[i];
				}
			}

			listofOFV[iteration] = currentValue;
			currentTemp = calculateTemperature(currentTemp);
			currentProb = calculateAcceptanceProbability(listofOFV[iteration], listofOFV[iteration - 1], currentTemp);
			if (currentProb > randomArr[iteration]) {
				Acceptancy[iteration] = "Accept";
			} else {
				Acceptancy[iteration] = "Reject";
			}

			executionTime[iteration] = stoppingTime - startingTime;
		}

		// Print the best solution found
		System.out.println("Best Solution: " + Arrays.toString(bestSolution));
		System.out.println("Best Value: " + bestValue);
		System.out.println("Execution Time: " + Arrays.toString(executionTime));
	}

	// Calculate the current Temperature
	private static double calculateTemperature(double temperature) {
		return temperature * COOLING_RATE;
	}

	// Helper method to calculate the fitness value of a solution
	private static int calculateValue(boolean[] solution) {
		int value = 0;
		int weight = 0;
		for (int i = 0; i < solution.length; i++) {
			if (solution[i]) {
				value += values[i];
				weight += weights[i];
			}
		}
		if (weight > knapsackCapacity) {
			return 0;
		} else {
			return value;
		}
	}

	// Helper method to calculate the acceptance probability of a neighbor solution
	private static double calculateAcceptanceProbability(int currentValue, int neighborValue, double temperature) {
		if (neighborValue > currentValue) {
			return 1;
		} else {
			return Math.exp((neighborValue - currentValue) / temperature);

		}
	}
}