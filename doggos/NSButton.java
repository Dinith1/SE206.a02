package namesayer;
import javafx.scene.control.Button;

public class NSButton extends Button {
	private final Double btnWidth = 90.0, btnHeight = 45.0;
	
	NSButton(String title) {
		super(title);
		this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefSize(btnWidth, btnHeight);
	}
	
	
}
