import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		UI ui = new UI();
		WordleGame game = new WordleGame();
		Controller controller = new Controller(ui, game);
		ui.addController(controller);
		controller.start();
		
	}

}