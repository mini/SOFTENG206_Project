package assignment4.utils;

import javafx.concurrent.Task;

import javax.sound.sampled.*;

import assignment4.ui.TestMicrophoneController;

/**
 * -- TestMicrophone Class --
 *
 * TestMicrophone detects the microphone being used by the system and takes the input to be used for analysis
 * for further output. The class also handles the calculations of RMS values to be passed into the controller
 * for visualisation.
 *
 * @author Dhruv Phadnis, Vanessa Ciputra
 */
public class TestMicrophone {

	private volatile boolean activate = true;
	private Thread audioThread;
	private TestMicrophoneController controller;

	/**
	 * When the TestMicrophone constructor is called, audio input is assigned and sampled at a fixed rate for further
	 * analysis for testing the microphone.
	 *
	 * Sample code reference: https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
	 */
	public TestMicrophone() {

		// Create a thread to ensure that the GUI does not freeze for concurrency when recording audio
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				// Create a TargetDataLine for receiving the microphone input and sound level
				TargetDataLine line = null;

				// Fix the sampling rate and bits as the line is received
				AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);

				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

				// If there is no line, ensure that an error message is produced as an output
				if (AudioSystem.isLineSupported(info) == false) {
					System.out.println("The line is not supported");
				}

				// Obtain and open the line for audio input
				try {
					line = (TargetDataLine) AudioSystem.getLine(info);
					line.open(format);
					line.start();
				} catch (LineUnavailableException e) {
					System.out.println("The TargetDataLine is unavailable");
				}

				// Continue to receive and open the line while the user remains on the TestMicrophone screen
				while (activate) {
					// Obtain the audio info from the line
					byte[] bytes = new byte[line.getBufferSize() / 5];
					line.read(bytes, 0, bytes.length);

					// Call the controller to visualise the sound input to the user
					controller.varySound(calculateRMS(bytes));
				}

				return null;
			}

		};

		// Start the thread for audio
		audioThread = new Thread(task);
		audioThread.setDaemon(true);
		audioThread.start();

	}

	/**
	 * calculateRMS() calculates the current RMS (root-mean-squared) of the audio date
	 * being recorded, and calculates the level of audio. The RMS value is calculate
	 * by adding up the squares, dividing by the number of samples and then taking
	 * the square root of that average value. This is used in order to find the level
	 * of audio being recorded.
	 *
	 * @param audioData
	 *            the byte information about the line being read
	 * @return the RMS value of the byte information
	 */
	protected static int calculateRMS(byte[] audioData) {

		long dataSum = 0;
		int dataLength = audioData.length;

		// Collect all buffered bytes within the audio recording
		for (int i = 0; i < dataLength; i++) {
			dataSum = dataSum + audioData[i];
		}

		// Find the average data byte
		double dataAvg = dataSum / dataLength;

		double sumMeanSquare = 0d;
		// Collect the sum-mean-square of the data by finding the residue of the
		// data to the average, and then squaring the result.
		for (int j = 0; j < dataLength; j++) {
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dataAvg, 2d);
		}

		// Find the average of the sum mean squares
		double avgMeanSquare = sumMeanSquare / dataLength;

		// Calculate the root mean square by square rooting the result
		int RMS = (int) (Math.pow(avgMeanSquare, 0.5d) + 0.5);

		return RMS;

	}

	/**
	 * Stops the line once exited out of the test microphone screen to stop audio input
	 */
	public void endLine() {
		activate = false;
	}

	/**
	 * Sets the controller for TestMicrophone
	 * 
	 * @param cont
	 *            the corresponding controller
	 */
	public void setController(TestMicrophoneController cont) {
		controller = cont;
	}

}
