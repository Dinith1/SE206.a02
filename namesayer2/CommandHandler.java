import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandHandler {


	
	public Process doCommand(String cmd) {
        try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();

			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            BufferedReader stderrBuffered = new BufferedReader(new InputStreamReader(stderr));
            
			String errStr = stderrBuffered.readLine();
			
			String line = null;
			while ((errStr == null) && (line = stdoutBuffered.readLine()) != null) {
				System.out.println(line);
			}
			if (errStr != null) {
				System.out.println("errStr is not null: " + errStr);
			}
			
			return process;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(cmd + " ***************TRY CATCH FAILED");
		}
		return null;
    }
	
	public void removeTempFiles() {
		this.doCommand("rm _audio.wav _video.mp4");
	}

}
