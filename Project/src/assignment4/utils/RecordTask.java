package assignment4.utils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import assignment4.NameSayerApp;
import javafx.concurrent.Task;

/**
 * Task for capturing microphone and saving it to a wav file
 * 
 * Inspired by www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 * 
 * @author Dhruv Phadnis, Vanessa Ciputra
 */
public class RecordTask extends Task<Void> {
	// 44100Hz 16bit 1 channel signed le
	private static final AudioFormat FORMAT = new AudioFormat(44100, 16, 1, true, false);
	private static final int MAX_TIME_MILI = 10_000;
	private static final File TEMP = new File(NameSayerApp.ROOT_DIR + "temp/recording.wav");
	
	private TargetDataLine line;

	private String dest;
	private Timer timer;
	private Thread thread;
	private OnEnd onEnd;

	/**
	 * @param dest where to save recording
	 * @param onEnd optional callback when recording stopped
	 */
	public RecordTask(File dest, OnEnd onEnd) {
		this.dest = dest.getPath();
		this.onEnd = onEnd;
		timer = new Timer(true);
		dest.getParentFile().mkdirs();
		thread = new Thread(this);
		thread.setDaemon(true);
	}
	
	public void start() {
		thread.start();
		timer.schedule(new TimerTask() { // Time limit in case user forgets
			@Override
			public void run() {
				stop();
			}
		}, MAX_TIME_MILI);
	}

	@Override
	protected Void call() throws Exception {
		try {
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);

			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				return null;
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(FORMAT);
			line.start();

			AudioInputStream input = new AudioInputStream(line);
			AudioSystem.write(input, AudioFileFormat.Type.WAVE, TEMP);

		} catch (LineUnavailableException | IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void stop() {
		line.stop();
		line.close();
		timer.cancel();
		AudioUtils.equaliseVolume(TEMP.getPath(), dest);
		if(onEnd != null){
			onEnd.call();
		}
	}
	
	public interface OnEnd{
		public void call();
	}
}
