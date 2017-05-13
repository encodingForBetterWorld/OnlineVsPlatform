package soft.chess.util;

import java.io.File;

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import soft.chess.client.GameHall.GameHallMusicPlayerPanel;


public class AudioPlayMP3 implements Runnable
{
	//能否不要互相回调，减小耦合？
	private GameHallMusicPlayerPanel playerView=null;
	//需要保证读写都同步
	private volatile static boolean isstop=false;
	//volatile只能保证读同步
	private volatile static boolean ispause=false;
	private volatile static boolean cutNextMic=false;
	private volatile static boolean cutPreMic=false;
	
	private String[] musicList=null;
	/**
	 * Constructor for PlayerTest.
	 * @param arg0
	 */
	//传入播放列表
	public AudioPlayMP3(String[] list,GameHallMusicPlayerPanel playerView){
		this.musicList=list;
		this.playerView=playerView;		
	}

	//播放音乐
	public void play()
	{
		playerView.changeMusic("第1曲:"+getMusicName(musicList[0]));
		for(int listIndex=0;listIndex<musicList.length;){
		 try
		 {
			//显示第几首歌
			int j=1;
			
			System.out.println("---  Start : "+musicList[listIndex]+"  ---");
			File file = new File(musicList[listIndex]);
			//URL file = new URL(props.getProperty("shoutcast"));
			AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
			System.out.println("Audio Type : "+aff.getType());
			AudioInputStream in= AudioSystem.getAudioInputStream(file);
			AudioInputStream din = null;
			if (in != null)
			{
			  AudioFormat baseFormat = in.getFormat();
			  System.out.println("Source Format : "+baseFormat.toString());
			  AudioFormat  decodedFormat = new AudioFormat(
				  AudioFormat.Encoding.PCM_SIGNED,
				  baseFormat.getSampleRate(),
				  16,
				  baseFormat.getChannels(),
				  baseFormat.getChannels() * 2,
				  baseFormat.getSampleRate(),
				  false);
			  System.out.println("Target Format : "+decodedFormat.toString());
			  din = AudioSystem.getAudioInputStream(decodedFormat, in);
			  rawplay(decodedFormat, din);
			  in.close();
			  //切换下一首歌
			  if(cutNextMic){
				  System.out.println("---  Cut next Music   ---");
				  //如果当前播放最后一首歌,切到第一首歌
					if(listIndex==musicList.length-1){
						listIndex=-1;
					}
				  ++listIndex;	
				  j+=listIndex;
				  cutNextMic=false;
				  playerView.changeMusic("第"+j+"曲:"+getMusicName(musicList[listIndex]));
				  continue;
			  }
			  //切换上一首歌
			  if(cutPreMic){
				  System.out.println("---  Cut pre Music   ---");
				  //如果当前播放第一首歌,切到最后一首歌
				  if(listIndex==0){
						listIndex=musicList.length;
					}
				  --listIndex;
				  j+=listIndex;
				  playerView.changeMusic("第"+j+"曲:"+getMusicName(musicList[listIndex]));
				  cutPreMic=false;
				  continue;
			  }
			  if(isstop){
				  System.out.println("--- Stop play Music");
				  break;
			  }
				  System.out.println("---  Stop : "+musicList[listIndex]+"  ---");				  
			}			
			++listIndex;
			j+=listIndex;
			playerView.changeMusic("第"+j+"曲:"+getMusicName(musicList[listIndex]));
			//执行回调方法
		 }
		 catch (Exception e)
		 {
			e.printStackTrace();
		 }
		}
	}

	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
	  SourceDataLine res = null;
	  DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	  res = (SourceDataLine) AudioSystem.getLine(info);
	  res.open(audioFormat);
	  return res;
	}
	
	private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException
	{
		byte[] data = new byte[4096];
		SourceDataLine line = getLine(targetFormat);		
		if (line != null)
		{
		  // Start
		  line.start();
		  int nBytesRead = 0, nBytesWritten = 0;
		  while (nBytesRead != -1)
		  {
		  	if(cutNextMic)break;
		  	if(cutPreMic)break;
		  	if(isstop)break;
		  	//进入死循环,暂停
		  	while(ispause);
			nBytesRead = din.read(data, 0, data.length);
			if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
		  }
		  // Stop
		  line.drain();
		  line.stop();
		  line.close();
		  din.close();
		}		
	}
	public synchronized void cutNext(){
		cutNextMic=true;
	}
	public synchronized void cutPre(){		
		cutPreMic=true;
	}
	public synchronized void pause(){		
		ispause=true;
	}
	public synchronized void goon(){		
		ispause=false;
	}
	public synchronized void stopPlay(){
		isstop=true;
	}
	public synchronized void beginPlay(){
		isstop=false;
	}
	//获取当前歌曲名称
	public String getMusicName(String str) {
		String returnVal=str.substring(str.lastIndexOf("/")+1);
		return returnVal.substring(0,returnVal.indexOf('.'));
	}
	//设置循环播放
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isstop){
			play();
		}
	}
}
