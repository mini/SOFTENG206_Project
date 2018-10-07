package assignment4.ui;

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

import javafx.concurrent.Task;

/**
 * Task for capturing microphone and saving it to a wav file
 */
public class RecordTask extends Task<Void> {
	// 44100Hz 16bit 1 channel signed le
	private static final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
	private static final int MAX_TIME_MILI = 10*1000;
	
	private TargetDataLine line;

	private File file;
	private Timer timer;
	private Thread thread;
	private OnEnd onEnd;

	public RecordTask(File file, OnEnd onEnd) {
		this.file = file;
		this.onEnd = onEnd;
		timer = new Timer(true);
		file.getParentFile().mkdirs();
		thread = new Thread(this);
		thread.setDaemon(true);
	}
	
	public void start() {
		thread.start();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				stop();
			}
		}, MAX_TIME_MILI);
	}

	@Override
	protected Void call() throws Exception {
		try {
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				return null;
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			AudioInputStream input = new AudioInputStream(line);
			AudioSystem.write(input, AudioFileFormat.Type.WAVE, file);

		} catch (LineUnavailableException | IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void stop() {
		line.stop();
		line.close();
		timer.cancel();
		if(onEnd != null){
			onEnd.call();
		}

		RewardsController.records++;
	}
	
	public interface OnEnd{
		public void call();
	}
}
