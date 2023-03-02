package com.stockticker.alarm;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class Alarm {

	
	public void playAlarm(String musicLocation, String description) {
		
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        
	    	@Override
	        protected Void doInBackground() throws Exception {
	            try {
	            	
	                File musicPath = new File(musicLocation);
	                
	                if (musicPath.exists()) {
	                	
	                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
	                    Clip clip = AudioSystem.getClip();
	                    
	                    clip.open(audioInput);
	                    clip.start();
	                    clip.loop(Clip.LOOP_CONTINUOUSLY);
	                    
	                    JOptionPane.showMessageDialog(null, description);
	                    clip.stop();
	                
	                } else {
	                
	                	System.out.println("Can't find file");
	                
	                }
	            
	            } catch (Exception e) {
	            
	            	e.printStackTrace();
	            
	            }
	            return null;
	        }
	    };
	    worker.execute();
	}

	
}
